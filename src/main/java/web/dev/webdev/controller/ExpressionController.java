package web.dev.webdev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import web.dev.webdev.dto.ExpressionCalcDto;
import web.dev.webdev.models.ExpressionCalc;
import web.dev.webdev.service.ExpressionService;

import java.util.List;


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

    @PostMapping("/calculate/result")
    public String calculateAndSave(@RequestParam("expression") String expression, Model model){
        try {
            double result = expressionService.calculateMathExpression(expression);
            if (!calculationCancelled) {
                // Calculate the math expression (you need to implement this logic)

                // Save the expression and result to the database
                ExpressionCalc expressionCalc = new ExpressionCalc();
                expressionCalc.setExpressionToCalculate(expression);
                expressionCalc.setResult(result);
                expressionService.saveExpression(expressionCalc);
            }
            // Pass the result to the view
            model.addAttribute("result", result);
        } catch (Exception e) {
            // Handle any errors or invalid expressions here
            model.addAttribute("error", "Invalid expression: " + e.getMessage());
        }
        calculationCancelled = false;
        return "result-page";
    }

    @GetMapping("/calculate/cancel")
    public String cancelCalculation(Model model){
        calculationCancelled = true;
        expressionService.cancelLastCalculation();
        return "redirect:/calculate";
    }

}
