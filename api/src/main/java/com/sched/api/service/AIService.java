package com.sched.api.service;

import com.sched.api.client.DemandForecastPort;
import com.sched.api.domain.User;
import com.sched.api.dto.request.AIDemandDataRequest;
import com.sched.api.dto.response.AIPredictionResponse;
import com.sched.api.repository.AIRepository;
import com.sched.api.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIService {

    private final AIRepository aiRepository;
    private final DemandForecastPort demandForecastPort;

    @Transactional(readOnly = true)
    public List<AIPredictionResponse> getPredictions() {
        return demandForecastPort.forecast(getDemandData());
    }

    public List<AIDemandDataRequest> getDemandData() {
        User authUser = SecurityUtils.getAuthenticatedUser();
        Long companyId = authUser.getCompany().getId();

        return aiRepository.getDemandDataByCompany(companyId);
    }
}
