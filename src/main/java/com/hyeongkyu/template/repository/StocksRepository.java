package com.hyeongkyu.template.repository;

import com.hyeongkyu.template.domain.dto.response.StockDto;
import com.hyeongkyu.template.domain.entity.Reports;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * packageName   : com.hyeongkyu.template.repository
 * Author        : yu-mi
 * Data          : 2025. 2. 1.
 * Description   : 회원정보 검색
 */
@Repository
public interface StocksRepository extends JpaRepository<Reports, Long> {

    Optional<Reports> findByUserIdAndCreatedAt(Long id, LocalDateTime endTime);

    @Query(value = """
        WITH us AS (
            SELECT code, holdings
            FROM user_stocks
            WHERE user_id = :userId
        ),
        s AS (
            SELECT
                s.code,
                s.name AS name_kr,
                s.name_en,
                s.industry,
                s.is_foreign AS isForeign,
                (SELECT close_price FROM stock_prices sp
                 WHERE sp.code = s.code AND sp.date = :sleepTime) AS sp,
                (SELECT close_price FROM stock_prices sp
                 WHERE sp.code = s.code AND sp.date = :wakeupTime) AS wp
            FROM stocks s
            WHERE code IN (SELECT code FROM us)
        )
        SELECT
            s.name_kr,
            s.name_en,
            s.industry,
            s.isForeign,
            s.sp * us.holdings AS balance,
            ROUND((s.wp - s.sp) / s.sp * 100, 2) AS percent
        FROM s
        JOIN us ON s.code = us.code order by isForeign, wp desc
        """, nativeQuery = true)
    List<StockDto> findUserStocksSummary(
        @Param("userId") Long userId,
        @Param("sleepTime") String sleepTime,
        @Param("wakeupTime") String wakeupTime
    );
}