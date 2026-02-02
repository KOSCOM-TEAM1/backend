package com.hyeongkyu.template.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockDto {

    private String nameKr;
    private String nameEn;

    private String industry; // 산업분류(이미지 구별용)

    private Boolean isForeign;
    private Double balance;

    private Double percent;
}
