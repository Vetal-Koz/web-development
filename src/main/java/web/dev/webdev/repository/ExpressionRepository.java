package web.dev.webdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.dev.webdev.models.ExpressionCalc;

import java.util.List;
import java.util.Optional;

public interface ExpressionRepository extends JpaRepository<ExpressionCalc, Long> {
    Optional<ExpressionCalc> findById(Long url);
}
