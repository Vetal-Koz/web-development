package web.dev.webdev.service;

import web.dev.webdev.dto.ExpressionCalcDto;
import web.dev.webdev.models.ExpressionCalc;

import java.util.List;
import java.util.concurrent.Future;

public interface ExpressionService {
    List<ExpressionCalcDto> findAllExpressions();

    ExpressionCalc saveExpression(ExpressionCalc expressionCalc);

    void cancelLastCalculation();

    double calculateMathExpression(String expression);
    void delete(Long expressionId);

    Future<Double> calculateMathExpressionAsync(String expression);
}
