package com.sched.api.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.sched.api.dto.response.AIPredictionResponse;
import com.sched.api.service.AIService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @GetMapping("/predictions")
    public List<AIPredictionResponse> getPredictions() {
        return aiService.getPredictions();
    }
}
