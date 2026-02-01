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
 * Description   : 주식 실시간 가격 정보
 */

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stock_prices")
@Builder
@AllArgsConstructor
public class StockPrices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 주식 번호
    @Column(name = "stock_id")
    private Long stockId;

    @Column(name = "code")
    private String code;

    // 시장종류 (ex. 코스피, 코스닥, 나스닥)
    @Column(name = "close_price")
    private Long price;

    // 거래 일시
    @Column(name = "date")
    private Date date;

}
