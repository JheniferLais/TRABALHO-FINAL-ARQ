package com.sched.api.service;

import com.sched.api.repository.AIRepository;
import com.sched.api.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.sched.api.domain.User;
import com.sched.api.dto.response.AIPredictionResponse;
import com.sched.api.dto.request.AIDemandDataRequest;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIService {

    private final AIRepository AIRepository;
    private final RestTemplate restTemplate;

    @Transactional(readOnly = true)
    public List<AIPredictionResponse> getPredictions() {

        List<AIDemandDataRequest> demandData = getDemandData();

        String FLASK_URL = "http://127.0.0.1:5000/predict";

        AIPredictionResponse[] response =
                restTemplate.postForObject(
                        FLASK_URL,
                        demandData,
                        AIPredictionResponse[].class
                );

        return List.of(response);
    }

    public List<AIDemandDataRequest> getDemandData() {
        User authUser = SecurityUtils.getAuthenticatedUser();
        Long companyId = authUser.getCompany().getId();

        return AIRepository.getDemandDataByCompany(companyId);
    }
}