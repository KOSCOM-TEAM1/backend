package com.hyeongkyu.template.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hyeongkyu.template.domain.entity.NewsArticles;

import java.util.List;

public interface NewsRepository extends JpaRepository<NewsArticles, Long> {

    // 전체 타임라인 (최신순)
    List<NewsArticles> findAllByOrderByCreatedAtDesc();
}
