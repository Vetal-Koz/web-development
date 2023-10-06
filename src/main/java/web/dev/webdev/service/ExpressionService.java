package web.dev.webdev.service;

import web.dev.webdev.dto.ExpressionCalcDto;
import web.dev.webdev.models.ExpressionCalc;

import java.util.List;

public interface ExpressionService {
    List<ExpressionCalcDto> findAllExpressions();

    ExpressionCalc saveExpression(ExpressionCalc expressionCalc);
}
