package com.hyeongkyu.template.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyeongkyu.template.global.common.ResponseDto;
import com.hyeongkyu.template.global.constants.Constants;
import com.hyeongkyu.template.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_PREFIX + "/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    private static final LocalDateTime BASE_DATE = LocalDate.of(2026, 1, 30)
                                                            .atStartOfDay();

    @Operation(summary = "기상 후 정보 조회", description = "기상 후 정보 조회")
    @GetMapping("/asset")
    public ResponseDto<?> stockAsset(@RequestHeader(value = "id", defaultValue = "1") Long userId)
        throws JsonProcessingException {
        return ResponseDto.ok(stockService.selectReportDetail(userId, BASE_DATE));
    }

    @Operation(summary = "기상 후 종목 조회", description = "기상 후 종목 조회")
    @GetMapping("/list")
    public ResponseDto<?> stockList(@RequestHeader(value = "id", defaultValue = "1") Long userId)
        throws NotFoundException {
        return ResponseDto.ok(stockService.selectStockList(userId, BASE_DATE));
    }


}
