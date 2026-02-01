package com.hyeongkyu.template.service;

import com.hyeongkyu.template.domain.dto.response.NewsTimelineItemDto;

import java.util.List;

public interface NewsTimelineService {
    // 전체 뉴스 타임라인 조회
    List<NewsTimelineItemDto> getNewsTimeline();
}