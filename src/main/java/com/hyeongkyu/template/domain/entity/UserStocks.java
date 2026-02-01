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
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * packageName   : com.hyeongkyu.template.domain.entity
 * Author        : yu-mi
 * Data          : 2026. 2. 1.
 * Description   : 주식 계좌 정보
 */

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_stocks")
@Builder
@AllArgsConstructor
public class UserStocks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 주식번호
    @Column(name = "stock_id", nullable = false)
    private Long stockId;

    // 주식 보유량
    @Column(name = "holdings", nullable = false)
    private String holdings;

    // 계좌 번호
    @Column(name = "account_id", nullable = false, unique = true)
    private String accountNumber;

    // 제공자명 (ex. 신한은행, 신한투자증권)
    @Column(name = "broker")
    private String broker;

    // 자산 종류 (ex. 주식, 은행, 기타)
    @Column(name = "type")
    private String type;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
