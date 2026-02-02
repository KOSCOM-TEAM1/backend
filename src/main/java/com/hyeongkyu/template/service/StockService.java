package com.hyeongkyu.template.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyeongkyu.template.domain.dto.response.StockDto;
import com.hyeongkyu.template.domain.entity.Reports;
import com.hyeongkyu.template.repository.StocksRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StocksRepository stocksRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper; // Spring이 알아서 주입해줍니다.

    public JsonNode selectReportDetail(Long id, LocalDateTime baseTime)
        throws JsonProcessingException {

        Reports reports = stocksRepository.findByUserIdAndCreatedAt(id, baseTime)
                                          .orElse(null);

        return reports == null ? null : objectMapper.readTree(reports.getDetails());
    }

    public List<StockDto> selectStockList(Long id, LocalDateTime baseTime)
        throws NotFoundException {

        Map<String, String> userTimes = userService.selectUserTimes(id, baseTime);

        List<StockDto> list = stocksRepository.findUserStocksSummary(id,
            userTimes.get("sleepTime"), userTimes.get("wakeupTime"));

        return list;
    }
}
