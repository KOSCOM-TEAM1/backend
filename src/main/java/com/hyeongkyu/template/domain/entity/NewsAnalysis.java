package com.hyeongkyu.template.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * packageName   : com.hyeongkyu.template.domain.entity
 * Author        : AI Assistant
 * Date          : 2026. 2. 1.
 * Description   : AI 뉴스 분석 결과 엔티티
 */

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "news_analysis")
@Builder
@AllArgsConstructor
public class NewsAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 뉴스 ID (외래키)
    @Column(name = "news_id", nullable = false)
    private Long newsId;

    // 사용자 ID (외래키) - 사용자별 보유 주식에 따라 다른 분석 결과
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // AI 요약 내용
    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    // 사용자 보유 주식에 대한 영향 분석
    @Column(name = "impact_analysis", columnDefinition = "TEXT")
    private String impactAnalysis;

    // 과거 유사 사례 분석
    @Column(name = "similar_cases", columnDefinition = "TEXT")
    private String similarCases;

    // 유사 사례들의 뉴스 ID들 (쉼표로 구분, ex. "15,23,45")
    @Column(name = "similar_news_ids", length = 500)
    private String similarNewsIds;

    // AI 모델 버전 (ex. gpt-4, gpt-3.5-turbo)
    @Column(name = "ai_model", length = 50)
    private String aiModel;

    // AI 응답 신뢰도 점수 (0.0 ~ 1.0)
    @Column(name = "confidence_score")
    private Double confidenceScore;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
