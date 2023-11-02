package web.dev.webdev.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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
    @CreationTimestamp
    private LocalDateTime createdOn;
}
