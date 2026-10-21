package com.spe.calculator.controller;

import com.spe.calculator.dto.CalculatorResponse;
import com.spe.calculator.service.CalculatorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CalculatorController {

    private final CalculatorService service;

    public CalculatorController(CalculatorService service) {
        this.service = service;
    }

    @GetMapping("/add")
    public CalculatorResponse add(
            @RequestParam double a,
            @RequestParam double b) {

        return new CalculatorResponse(
            "add", a, b, service.add(a, b)
        );
    }

    @GetMapping("/subtract")
    public CalculatorResponse subtract(
            @RequestParam double a,
            @RequestParam double b) {

        return new CalculatorResponse(
            "subtract", a, b, service.subtract(a, b)
        );
    }

    @GetMapping("/multiply")
    public CalculatorResponse multiply(
            @RequestParam double a,
            @RequestParam double b) {

        return new CalculatorResponse(
            "multiply", a, b, service.multiply(a, b)
        );
    }

    @GetMapping("/divide")
    public CalculatorResponse divide(
            @RequestParam double a,
            @RequestParam double b) {

        return new CalculatorResponse(
            "divide", a, b, service.divide(a, b)
        );
    }
}
