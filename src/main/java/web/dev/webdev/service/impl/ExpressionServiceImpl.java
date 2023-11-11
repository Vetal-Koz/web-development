package web.dev.webdev.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.SessionException;
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

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class ExpressionServiceImpl implements ExpressionService {
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


    @Async
    public Future<Double> calculateMathExpressionAsync(String expression) {

        double result;
        double progress = 0;
        try {
            int insideCircle = 0;
            long numPoints = Long.parseLong(expression);

            for (int i = 0; i < numPoints; i++) {
                if(calculationCancelled){
                    break;
                }
                double x = Math.random();
                double y = Math.random();

                double distance = Math.sqrt(x * x + y * y);

                if (distance <= 1) {
                    insideCircle++;
                }
                progress = (double) ((i+1 / numPoints) * 100);
                sendProgress(progress);

            }

            result = 4.0 * insideCircle / numPoints;
            if (!calculationCancelled) {
                ExpressionCalc expressionCalc = new ExpressionCalc();
                expressionCalc.setExpressionToCalculate(expression);
                expressionCalc.setResult(result);
                saveExpression(expressionCalc);
            }
            return new AsyncResult<>(result);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating expression: " + e.getMessage(), e);
        }
    }

    private void sendProgress(double progress) {
        messagingTemplate.convertAndSend("/topic/progress", progress);
    }



}
