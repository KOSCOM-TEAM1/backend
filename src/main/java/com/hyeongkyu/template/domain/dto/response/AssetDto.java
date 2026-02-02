package com.hyeongkyu.template.domain.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AssetDto {

    private LocalDateTime time;

    private Long domesticBalance; //국내주식 잔액
    private Long domesticDiff; //국내주식 차액
    private List<Long> domesticCharts = new ArrayList<>(); // 국내주식 그래프

    private Long foreignBalnce; // 해외주식 잔액
    private Long foreignDiff; // 해외주식 차액
    private List<Long> foreignCharts = new ArrayList<>(); // 해외주식 그래프

    private Long allBalnce; // 총 자산
    private Long allDiff; // 총 수익
    private List<Long> allCharts = new ArrayList<>();

}
