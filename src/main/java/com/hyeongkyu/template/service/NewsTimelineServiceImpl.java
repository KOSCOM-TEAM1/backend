package com.hyeongkyu.template.service;

import java.util.List;
import com.hyeongkyu.template.domain.dto.response.NewsTimelineItemDto;
import com.hyeongkyu.template.domain.entity.NewsArticles;
import com.hyeongkyu.template.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsTimelineServiceImpl implements NewsTimelineService{

    private final NewsRepository newsRepository;

    @Override
    public List<NewsTimelineItemDto> getNewsTimeline() {
        return newsRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private NewsTimelineItemDto toDto(NewsArticles news) {
        NewsTimelineItemDto dto = new NewsTimelineItemDto();

        dto.setId(news.getId());
        dto.setImpact(news.getImpact());
        dto.setTitle(news.getTitle());
        dto.setSummary(news.getSummary());
        dto.setUrl(news.getUrl());
        dto.setCreatedAt(news.getCreatedAt());
        dto.setRegion(news.getRegion());
        dto.setTags(news.getTags());

        return dto;
    }
}
