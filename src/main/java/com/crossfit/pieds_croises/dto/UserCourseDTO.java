package com.crossfit.pieds_croises.dto;

import com.crossfit.pieds_croises.model.UserCourse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCourseDTO {
    private Long userId;
    private UserCourse.Status status;
}
