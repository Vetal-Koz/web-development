package web.dev.webdev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import web.dev.webdev.dto.ExpressionCalcDto;
import web.dev.webdev.models.ExpressionCalc;
import web.dev.webdev.service.ExpressionService;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Long.parseLong;

@Controller
public class ExpressionController {
    private ExpressionService expressionService;
    private int progress;



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

    @GetMapping("/getProgress")
    @ResponseBody
    public Map<String, Integer> getProgress() {
        Map<String, Integer> progressMap = new HashMap<>();
        progressMap.put("progress", progress);

        return progressMap;
    }
    @PostMapping("/calculate")
    public String calculateAndSave(@RequestParam("expression") String expression, Model model){
        try {
            // Calculate the math expression (you need to implement this logic)
            double result = calculateMathExpression(expression);

            // Save the expression and result to the database
            ExpressionCalc expressionCalc = new ExpressionCalc();
            expressionCalc.setExpressionToCalculate(expression);
            expressionCalc.setResult(result);
            expressionService.saveExpression(expressionCalc);

            // Pass the result to the view
            model.addAttribute("result", result);
        } catch (Exception e) {
            // Handle any errors or invalid expressions here
            model.addAttribute("error", "Invalid expression: " + e.getMessage());
        }
        return "result-page";
    }



    private double calculateMathExpression(String expression){
        int insideCircle = 0;
        long numPoints = Long.parseLong(expression);

        for (int i = 0; i < numPoints; i++) {
            double x = Math.random();
            double y = Math.random();

            double distance = Math.sqrt(x * x + y * y);

            if (distance <= 1) {
                insideCircle++;
            }
            progress = (int) ((i + 1) * 100 / numPoints);
        }

        double pi = 4.0 * insideCircle / numPoints;
        return pi;
    }
}
