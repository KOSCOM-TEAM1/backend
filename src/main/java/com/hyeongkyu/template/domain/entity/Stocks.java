package com.hyeongkyu.template.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
 * Description   : 주식 기본 정보
 */

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stocks")
@Builder
@AllArgsConstructor
public class Stocks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 식별코드 (코스피의 경우 isuSrtCd)
    @Column(name = "code")
    private String code;

    // 종목 이름 (ex. 삼성전자, SK하이닉스, 네이버)
    @Column(name = "name")
    private String name;

    // 종목 이름 영문 (ex. samsung, naver)
    @Column(name = "name_en")
    private String nameEn;

    // 시장명 (ex. 코스피, 코스닥, 나스닥)
    @Column(name = "market")
    private String market;

    // 산업 섹터 대분류 (ex. 기술, 금융, 헬스케어)
    @Column(name = "sector")
    private String sector;

    // 산업 섹터 소분류 (ex. 반도체, 로봇)
    @Column(name = "industry")
    private String industry;

    // 국내시장 여부
    @Column(name = "is_foreign")
    private Boolean isForeign;

}
