package com.hyeongkyu.template.repository;

import com.hyeongkyu.template.domain.entity.News;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * packageName   : com.hyeongkyu.template.repository
 * Author        : AI Assistant
 * Date          : 2026. 2. 1.
 * Description   : AI 분석용 뉴스 Repository (OpenAI)
 */

@Repository
public interface NewsAiRepository extends JpaRepository<News, Long> {

    // 분석되지 않은 뉴스 조회
    List<News> findByIsAnalyzedFalse();

    // 특정 기간 사이의 뉴스 조회
    List<News> findByPublishedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    // 특정 기간 사이의 미분석 뉴스 조회
    List<News> findByPublishedAtBetweenAndIsAnalyzedFalse(LocalDateTime startTime, LocalDateTime endTime);

}
