package com.hyeongkyu.template.controller;

import com.hyeongkyu.template.domain.dto.response.UserDto;
import com.hyeongkyu.template.global.common.ResponseDto;
import com.hyeongkyu.template.global.constants.Constants;
import com.hyeongkyu.template.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName   : com.hyeongkyu.template.controller
 * Author        : yu-mi
 * Data          : 2025. 2. 1.
 * Description   : 사용자 관련 API
 */

@RestController
@RequestMapping(Constants.API_PREFIX + "/member")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(description = "회원정보(프로필 사진, 이름)와 설정정보(취침시간, 기상시간)를 가져온다.")
    @GetMapping("/allInfo")
    public ResponseDto<?> memberInfo(@RequestHeader(value = "id", defaultValue = "1") Long userId)
        throws NotFoundException {
        UserDto memberInfo = userService.selectUserInfo(userId);
        return ResponseDto.ok(memberInfo);
    }


}
