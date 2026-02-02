package com.hyeongkyu.template.service;

import com.hyeongkyu.template.domain.dto.response.UserDto;
import com.hyeongkyu.template.domain.entity.User;
import com.hyeongkyu.template.domain.entity.UserSetting;
import com.hyeongkyu.template.repository.UserRepository;
import com.hyeongkyu.template.repository.UserSettingRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserSettingRepository userSettingRepository;

    public UserDto selectUserInfo(Long id) throws NotFoundException {
        User user = selectUser(id);
        UserSetting userSetting = selectUserSetting(id);

        return UserDto.builder()
                      .name(user.getNickname())
                      .pictureUrl(user.getPictureUrl())
                      .sleep_hour(userSetting.getStartTime()
                                             .substring(0, 2))
                      .sleep_minute(userSetting.getStartTime()
                                               .substring(2, 4))
                      .wakeup_hour(userSetting.getEndTime()
                                              .substring(0, 2))
                      .wakeup_minute(userSetting.getEndTime()
                                                .substring(2, 4))
                      .build()
            ;
    }


    private static final DateTimeFormatter DATETIME_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    protected Map<String, String> selectUserTimes(Long id, LocalDateTime bastTime)
        throws NotFoundException {

        UserSetting userSetting = selectUserSetting(id);
        int sleepTime = Integer.parseInt(userSetting.getStartTime());
        int wakeupTime = Integer.parseInt(userSetting.getEndTime());

        Map<String, String> times = new HashMap();

        times.put("sleepTime", bastTime.minusDays(sleepTime > wakeupTime ? 1 : 0)
                                       .withHour(sleepTime / 100)
                                       .withMinute(sleepTime % 100)
                                       .format(DATETIME_FORMAT));
        times.put("wakeupTime", bastTime.withHour(wakeupTime / 100)
                                        .withMinute(wakeupTime % 100)
                                        .format(DATETIME_FORMAT));

        return times;
    }

    protected User selectUser(Long id) throws NotFoundException {
        return userRepository.findById(id)
                             .orElseThrow(
                                 () -> new NotFoundException()
                             );
    }

    protected UserSetting selectUserSetting(Long id) throws NotFoundException {
        return userSettingRepository.findById(id)
                                    .orElseThrow(
                                        () -> new NotFoundException()
                                    );
    }


}
