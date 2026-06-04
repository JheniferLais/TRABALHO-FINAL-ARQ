package com.sched.api.client;

import com.sched.api.dto.request.AIDemandDataRequest;
import com.sched.api.dto.response.AIPredictionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class FlaskDemandForecastClient implements DemandForecastPort {

    private final RestTemplate restTemplate;
    private final String forecastUrl;

    public FlaskDemandForecastClient(
            RestTemplate restTemplate,
            @Value("${forecast.service.url:http://127.0.0.1:5000/predict}") String forecastUrl
    ) {
        this.restTemplate = restTemplate;
        this.forecastUrl = forecastUrl;
    }

    @Override
    public List<AIPredictionResponse> forecast(List<AIDemandDataRequest> demandData) {
        AIPredictionResponse[] response = restTemplate.postForObject(
                forecastUrl,
                demandData,
                AIPredictionResponse[].class
        );

        return response == null ? List.of() : List.of(response);
    }
}
