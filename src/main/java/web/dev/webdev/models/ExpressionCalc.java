package web.dev.webdev.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "expressions")
public class ExpressionCalc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String expressionToCalculate;
    private double result;
}
