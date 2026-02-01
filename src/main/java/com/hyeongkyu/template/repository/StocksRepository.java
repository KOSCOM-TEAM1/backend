package com.hyeongkyu.template.repository;

import com.hyeongkyu.template.domain.entity.Stocks;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * packageName   : com.hyeongkyu.template.repository
 * Author        : AI Assistant
 * Date          : 2026. 2. 1.
 * Description   : 주식 정보 Repository
 */

@Repository
public interface StocksRepository extends JpaRepository<Stocks, Long> {

    // 주식 코드로 조회
    Optional<Stocks> findByCode(String code);

    // 주식 이름으로 조회
    List<Stocks> findByNameContaining(String name);

    // 여러 ID로 조회
    List<Stocks> findByIdIn(List<Long> ids);

}
