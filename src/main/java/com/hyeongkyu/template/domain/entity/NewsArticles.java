package com.hyeongkyu.template.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "news_articles")
@Builder
@AllArgsConstructor
public class NewsArticles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 영향도 (ex. 높은 영향, 중간 영향, ...)
    @Column(name = "impact")
    private String impact;

    // 뉴스 제목
    @Column(name = "title")
    private String title;

    // 뉴스 ai 요약
    @Column(name = "summary")
    private String summary;

    // 뉴스 출처
    @Column(name = "url")
    private String url;

    // 뉴스 작성시간
    @Column(name = "time")
    private String time;

    // 관련 지역 (ex. 국내, 미국, 중국, ...)
    @Column(name = "region")
    private String region;

    // 뉴스 관련 태그들
    @ElementCollection
    @CollectionTable(name = "news_tags", joinColumns = @JoinColumn(name = "news_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
}
