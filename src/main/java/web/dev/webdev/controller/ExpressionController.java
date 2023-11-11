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
import java.util.concurrent.Future;


@Controller
public class ExpressionController {
    private volatile boolean calculationCancelled = false;
    private ExpressionService expressionService;




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
    public String sendMessage(@Payload String expression, @Headers MessageHeaders headers) {
        System.out.println(expression);

        try {
            // Створюємо об'єкт ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Читаємо JSON-рядок у JsonNode
            JsonNode jsonNode = objectMapper.readTree(expression);
            String expressionCalc = jsonNode.get("value").asText();
            expressionService.calculateMathExpressionAsync(expressionCalc);

        } catch (JsonProcessingException e){
            System.out.println(e);
        }

        return expression; // You can modify the return type as needed
    }

    @GetMapping("/calculate/cancel")
    public String cancelCalculation(Model model){
        calculationCancelled = true;
        expressionService.cancelLastCalculation();
        return "redirect:/calculate";
    }

}
