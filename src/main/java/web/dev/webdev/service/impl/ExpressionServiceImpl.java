package web.dev.webdev.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.SessionException;
import lombok.ToString;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.security.core.context.SecurityContextHolder;
import web.dev.webdev.dto.ExpressionCalcDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.dev.webdev.messages.ProgressMessage;
import web.dev.webdev.service.ExpressionService;
import web.dev.webdev.models.ExpressionCalc;
import web.dev.webdev.repository.ExpressionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class ExpressionServiceImpl implements ExpressionService {
    private final Map<Long, CompletableFuture<Double>> activeTasks = new ConcurrentHashMap<>();
    private volatile boolean calculationCancelled = false;
    private ExpressionRepository expressionRepository;
    private final SimpMessagingTemplate messagingTemplate;


    @Autowired
    public ExpressionServiceImpl(ExpressionRepository expressionRepository, SimpMessagingTemplate messagingTemplate){
        this.expressionRepository = expressionRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public List<ExpressionCalcDto> findAllExpressions() {
        List<ExpressionCalc> expressionCalcs = expressionRepository.findAll();
        return expressionCalcs.stream().map((expressionCalc) -> mapToExpressionCalcDto(expressionCalc)).collect(Collectors.toList());
    }

    @Override
    public void cancelTask(Long taskId) {
        CompletableFuture<Double> task = activeTasks.get(taskId);
        try {
            if (task != null && !task.isDone()) {
                task.cancel(true);

                activeTasks.remove(taskId);
            }
        } catch (Exception e){
            System.out.println("task " + taskId + "are canceled");
        }

    }
    @Override
    public ExpressionCalc saveExpression(ExpressionCalc expressionCalc) {
        return expressionRepository.save(expressionCalc);
    }

    @Override
    public void cancelLastCalculation() {
        calculationCancelled = true;

    }

    @Override
    public void delete(Long expressionId) {
        expressionRepository.deleteById(expressionId);
    }

    private ExpressionCalcDto mapToExpressionCalcDto(ExpressionCalc expressionCalc){
        ExpressionCalcDto expressionCalcDto = ExpressionCalcDto.builder()
                .id(expressionCalc.getId())
                .expressionToCalculate(expressionCalc.getExpressionToCalculate())
                .result(expressionCalc.getResult())
                .createdOn(expressionCalc.getCreatedOn())
                .build();
        return expressionCalcDto;
    }


    @Async("asyncExecutor")
    public CompletableFuture<Double> calculateMathExpressionAsync(String expression) {
        CompletableFuture<Double> future = new CompletableFuture<>();


        double progress = 0;
        double lastSentProgress = -1.0;
        Long id = new Random().nextLong(100);
        activeTasks.put(id, future);

        System.out.println(getActiveTaskCount());

        try {
            double result; // Move the result variable inside the try block
            int insideCircle = 0;
            long numPoints = Long.parseLong(expression);

            for (int i = 0; i < numPoints; i++) {
                if (future.isCancelled()) {
                    break;
                }
                double x = Math.random();
                double y = Math.random();

                double distance = Math.sqrt(x * x + y * y);

                if (distance <= 1) {
                    insideCircle++;
                }
                if (future.isCancelled()) {
                    break;
                }
                progress = ((i + 1.0) / numPoints) * 100;
                if (Math.abs(progress - lastSentProgress) >= 1) {
                    sendProgress(Long.toString(id), progress);
                    lastSentProgress = ((i + 1.0) / numPoints) * 100; // Update last sent progress
                }
                if (i == numPoints - 1) {
                    sendProgress(Long.toString(id), 100);
                }
            }

            result = 4.0 * insideCircle / numPoints;

            if (!future.isCancelled()) {
                ExpressionCalc expressionCalc = new ExpressionCalc();
                expressionCalc.setExpressionToCalculate(expression);
                expressionCalc.setResult(result);
                saveExpression(expressionCalc);
            }

            future.complete(result);
        } catch (CompletionException e) {
            // Task was cancelled, handle it as needed
            // For example, you can log the cancellation or perform cleanup
            System.out.println("Task was cancelled: " + e.getMessage());
        } catch (CancellationException e) {
            // Task was cancelled, handle it as needed
            // For example, you can log the cancellation or perform cleanup
            System.out.println("Task was cancelled: " + e.getMessage());
        } catch (Exception e) {
            future.completeExceptionally(new RuntimeException("Error calculating expression: " + e.getMessage(), e));
        }

        // Remove the completed future from activeTasks when it completes
        return future.whenComplete((result, throwable) -> {
            activeTasks.remove(future);
        });
    }


    private void sendProgress(String expressionId,double progress)
    {
        messagingTemplate.convertAndSend("/topic/progress/" + expressionId, progress);
    }

    public int getActiveTaskCount() {
        return activeTasks.size();
    }

}
