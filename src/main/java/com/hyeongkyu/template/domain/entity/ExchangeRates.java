package com.hyeongkyu.template.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * packageName   : com.hyeongkyu.template.domain.entity
 * Author        : yu-mi
 * Data          : 2026. 2. 1.
 * Description   : 해외주식을 위한 환율 정보
 */

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stock_prices")
@Builder
@AllArgsConstructor
public class ExchangeRates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 통화 코드 (ex. USD, KRX)
    @Column(name = "currency_code")
    private String code;

    // 환율 값
    @Column(name = "rate")
    private double rate;

    // 기준 일시
    @Column(name = "date")
    private Date date;

}
