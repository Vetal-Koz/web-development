package web.dev.webdev.service.impl;

import web.dev.webdev.dto.ExpressionCalcDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.dev.webdev.service.ExpressionService;
import web.dev.webdev.models.ExpressionCalc;
import web.dev.webdev.repository.ExpressionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpressionServiceImpl implements ExpressionService {
    private ExpressionRepository expressionRepository;

    @Autowired
    public ExpressionServiceImpl(ExpressionRepository expressionRepository){this.expressionRepository = expressionRepository;}

    @Override
    public List<ExpressionCalcDto> findAllExpressions() {
        List<ExpressionCalc> expressionCalcs = expressionRepository.findAll();
        return expressionCalcs.stream().map((expressionCalc) -> mapToExpressionCalcDto(expressionCalc)).collect(Collectors.toList());
    }

    @Override
    public ExpressionCalc saveExpression(ExpressionCalc expressionCalc) {
        return expressionRepository.save(expressionCalc);
    }

    private ExpressionCalcDto mapToExpressionCalcDto(ExpressionCalc expressionCalc){
        ExpressionCalcDto expressionCalcDto = ExpressionCalcDto.builder()
                .id(expressionCalc.getId())
                .expressionToCalculate(expressionCalc.getExpressionToCalculate())
                .result(expressionCalc.getResult())
                .build();
        return expressionCalcDto;
    }
}
