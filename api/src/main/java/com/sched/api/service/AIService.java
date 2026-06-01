package com.sched.api.service;

import java.util.List;

import com.sched.api.repository.AIRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sched.api.domain.User;
import com.sched.api.dto.response.AIPredictionResponse;
import com.sched.api.dto.request.AIDemandDataRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AIService {

    private final AIRepository AIRepository;
    private final RestTemplate restTemplate;

    public List<AIPredictionResponse> getPredictions(User user) {

        List<AIDemandDataRequest> demandData = getDemandData(user);

        String FLASK_URL = "http://127.0.0.1:5000/predict";

        AIPredictionResponse[] response =
                restTemplate.postForObject(
                        FLASK_URL,
                        demandData,
                        AIPredictionResponse[].class
                );

        return List.of(response);
    }

    public List<AIDemandDataRequest> getDemandData(User user) {

        Long companyId = user.getCompany().getId();

        return AIRepository.getDemandDataByCompany(companyId);
    }
}