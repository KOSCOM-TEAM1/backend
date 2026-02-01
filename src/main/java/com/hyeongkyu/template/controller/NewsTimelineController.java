package com.hyeongkyu.template.controller;

import com.hyeongkyu.template.domain.dto.response.NewsTimelineItemDto;
import com.hyeongkyu.template.service.NewsTimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsTimelineController {

    private final NewsTimelineService newsTimelineService;

    // 전체 뉴스 타임라인 조회
    @GetMapping("/timeline")
    public List<NewsTimelineItemDto> getNewsTimeline() {
        return newsTimelineService.getNewsTimeline();
    }
}
