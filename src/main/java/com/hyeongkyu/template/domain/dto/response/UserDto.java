package com.hyeongkyu.template.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserDto {

    private String name;
    private String pictureUrl;

    private String sleep_hour;
    private String sleep_minute;
    private String wakeup_hour;
    private String wakeup_minute;

}
