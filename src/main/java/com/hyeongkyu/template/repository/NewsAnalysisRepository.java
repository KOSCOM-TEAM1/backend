package com.hyeongkyu.template.repository;

import com.hyeongkyu.template.domain.entity.NewsAnalysis;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * packageName   : com.hyeongkyu.template.repository
 * Author        : AI Assistant
 * Date          : 2026. 2. 1.
 * Description   : 뉴스 분석 결과 Repository
 */

@Repository
public interface NewsAnalysisRepository extends JpaRepository<NewsAnalysis, Long> {

    // 특정 뉴스와 사용자의 분석 결과 조회
    Optional<NewsAnalysis> findByNewsIdAndUserId(Long newsId, Long userId);

    // 특정 사용자의 모든 분석 결과 조회
    List<NewsAnalysis> findByUserId(Long userId);

    // 특정 뉴스의 모든 분석 결과 조회
    List<NewsAnalysis> findByNewsId(Long newsId);

}
