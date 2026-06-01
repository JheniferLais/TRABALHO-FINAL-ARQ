package com.sched.api.service;

import java.util.List;

import com.sched.api.repository.IARepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sched.api.domain.User;
import com.sched.api.dto.response.AiPredictionResponse;
import com.sched.api.dto.request.DemandDataRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiService {

    private final IARepository iaRepository;
    private final RestTemplate restTemplate;

    public List<AiPredictionResponse> getPredictions(User user) {

        List<DemandDataRequest> demandData = getDemandData(user);

        String FLASK_URL = "http://127.0.0.1:5000/predict";

        AiPredictionResponse[] response =
                restTemplate.postForObject(
                        FLASK_URL,
                        demandData,
                        AiPredictionResponse[].class
                );

        return List.of(response);
    }

    public List<DemandDataRequest> getDemandData(User user) {

        Long companyId = user.getCompany().getId();

        return iaRepository.getDemandDataByCompany(companyId);
    }
}