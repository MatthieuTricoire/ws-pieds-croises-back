package com.crossfit.pieds_croises.dto;

import com.crossfit.pieds_croises.model.Course;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDatetime;
    private short duration;
    private Integer personLimit;
    private Course.Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> usersId;
    private String coachName;
    private Long coachId;
}
