package com.hyeongkyu.template.repository;

import com.hyeongkyu.template.domain.entity.UserSetting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * packageName   : com.hyeongkyu.template.repository
 * Author        : yu-mi
 * Data          : 2025. 2. 1.
 * Description   : 회원정보 검색
 */
@Repository
public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {

    Optional<UserSetting> findById(Long id);

}
