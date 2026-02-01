package com.hyeongkyu.template.repository;

import com.hyeongkyu.template.domain.entity.UserStocks;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * packageName   : com.hyeongkyu.template.repository
 * Author        : AI Assistant
 * Date          : 2026. 2. 1.
 * Description   : 사용자 보유 주식 Repository
 */

@Repository
public interface UserStocksRepository extends JpaRepository<UserStocks, Long> {

    // 특정 계좌의 보유 주식 조회
    List<UserStocks> findByAccountNumber(String accountNumber);

    // 특정 주식을 보유한 모든 계좌 조회
    List<UserStocks> findByStockId(Long stockId);

}
