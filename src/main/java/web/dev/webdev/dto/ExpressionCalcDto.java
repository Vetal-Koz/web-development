package web.dev.webdev.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpressionCalcDto {
    private long id;
    private String expressionToCalculate;
    private double result;
}
