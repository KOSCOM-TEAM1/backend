package com.hyeongkyu.template.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class NewsTimelineItemDto {
    private Long id;

    private String impact;

    private String title;

    private String summary;

    private String url;

    private String time;

    private String region;

    private List<String> tags;
}
