package web.dev.webdev.service;

import jakarta.servlet.http.HttpServletRequest;
import web.dev.webdev.dto.ExpressionCalcDto;
import web.dev.webdev.models.ExpressionCalc;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface ExpressionService {
    List<ExpressionCalcDto> findAllExpressions();

    ExpressionCalc saveExpression(ExpressionCalc expressionCalc);

    void cancelLastCalculation();

    void delete(Long expressionId);

    CompletableFuture<Double> calculateMathExpressionAsync(String expression);
}
