package web.dev.webdev.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import web.dev.webdev.dto.ExpressionCalcDto;
import web.dev.webdev.models.ExpressionCalc;
import web.dev.webdev.service.ExpressionService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;


@Controller
public class ExpressionController {
    private volatile boolean calculationCancelled = false;
    private ExpressionService expressionService;
    private CompletableFuture<Void> previousCalculation = CompletableFuture.completedFuture(null);





    @Autowired
    public ExpressionController(ExpressionService expressionService){this.expressionService = expressionService;}

    @GetMapping("/calculate")
    public String createExpressionForm(Model model){

        return "create-expression";
    }


    @GetMapping("/results")
    public String listResults(Model model){
        List<ExpressionCalcDto> results = expressionService.findAllExpressions();
        model.addAttribute("results", results);
        return "results-list";
    }



    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public CompletableFuture<String> sendMessage(@Payload String expression, @Headers MessageHeaders headers) {
        System.out.println(expression);

        CompletableFuture<String> future = new CompletableFuture<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Read JSON string into JsonNode
            JsonNode jsonNode = objectMapper.readTree(expression);
            String expressionCalc = jsonNode.get("value").asText();

            // Asynchronously calculate the math expression
            expressionService.calculateMathExpressionAsync(expressionCalc)
                    .thenCompose(result -> {


                        // Return the result as the new completion stage
                        return CompletableFuture.completedFuture(result);
                    })
                    .thenAccept(result -> {
                        // Complete the CompletableFuture when the entire chain is done
                        future.complete(expression);
                    })
                    .exceptionally(ex -> {
                        // Handle exceptions and complete the CompletableFuture exceptionally
                        future.completeExceptionally(ex);
                        return null;
                    });

        } catch (JsonProcessingException e) {
            System.out.println(e);
            // Complete the CompletableFuture exceptionally in case of an exception
            future.completeExceptionally(e);
        }

        return future;
    }

    @GetMapping("/calculate/{taskId}/cancel")
    public String cancelCalculation(@PathVariable("taskId") Long taskId){
        expressionService.cancelTask(taskId);
        return "redirect:/calculate";
    }

}
