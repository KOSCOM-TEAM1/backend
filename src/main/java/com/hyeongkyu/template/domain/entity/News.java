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
 * Description   : 뉴스 정보 엔티티
 */

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "news")
@Builder
@AllArgsConstructor
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 뉴스 제목
    @Column(name = "title", nullable = false, length = 500)
    private String title;

    // 뉴스 내용
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // 뉴스 출처 (ex. 연합뉴스, 한국경제)
    @Column(name = "source", length = 100)
    private String source;

    // 뉴스 URL
    @Column(name = "url", length = 1000)
    private String url;

    // 뉴스 발행 시간
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    // 관련 주식 ID (여러 개일 경우 쉼표로 구분, ex. "1,2,3")
    @Column(name = "related_stock_ids", length = 500)
    private String relatedStockIds;

    // AI 분석 완료 여부
    @Column(name = "is_analyzed", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isAnalyzed;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
