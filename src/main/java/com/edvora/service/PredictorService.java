package com.edvora.service;

import com.edvora.dto.CoveragePredictionDto;
import com.edvora.model.User;
import java.util.List;

public interface PredictorService {
    List<CoveragePredictionDto> predict(User u);
}
