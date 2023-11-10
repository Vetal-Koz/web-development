package web.dev.webdev.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/calculate")
    public String calculateAndSave(@RequestParam("expression") String expression, Model model) {
        try {
            Future<Double> resultFuture = expressionService.calculateMathExpressionAsync(expression);
            model.addAttribute("resultFuture", resultFuture);
        } catch (Exception e) {
            model.addAttribute("error", "Invalid expression: " + e.getMessage());
        }

        return "create-expression";
    }

    @GetMapping("/calculate/cancel")
    public String cancelCalculation(Model model){
        calculationCancelled = true;
        expressionService.cancelLastCalculation();
        return "redirect:/calculate";
    }

}
