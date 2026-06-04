package com.sched.api.client;

import com.sched.api.dto.request.AIDemandDataRequest;
import com.sched.api.dto.response.AIPredictionResponse;

import java.util.List;

public interface DemandForecastPort {

    List<AIPredictionResponse> forecast(List<AIDemandDataRequest> demandData);
}
