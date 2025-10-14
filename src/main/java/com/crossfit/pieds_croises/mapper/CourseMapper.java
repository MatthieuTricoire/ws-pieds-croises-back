package com.crossfit.pieds_croises.mapper;

import com.crossfit.pieds_croises.dto.CourseCreateDTO;
import com.crossfit.pieds_croises.dto.CourseDTO;
import com.crossfit.pieds_croises.dto.CourseUpdateDTO;
import com.crossfit.pieds_croises.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(
            target = "userCoursesInfo",
            expression = "java(course.getUserCourses() != null ? " +
                    "course.getUserCourses().stream()" +
                    ".map(uc -> new com.crossfit.pieds_croises.dto.UserCourseDTO(uc.getUser().getId(), uc.getStatus()))" +
                    ".toList() : java.util.Collections.emptyList())"
    )
    @Mapping(
            target = "coachName",
            expression = "java(course.getCoach() != null ? course.getCoach().getFirstname() + \" \" + course.getCoach().getLastname() : null)"
    )
    @Mapping(
            target = "coachId",
            expression = "java(course.getCoach() != null ? course.getCoach().getId() : null)"
    )
    CourseDTO convertToDto(Course course);

    @Mapping(target = "status", expression = "java(Course.Status.OPEN)")
    @Mapping(target = "userCourses", ignore = true) // remplacé "users"
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "coach", ignore = true)
    Course convertToEntity(CourseCreateDTO courseCreateDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "coach", ignore = true)
    @Mapping(target = "userCourses", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateFromDTO(CourseUpdateDTO dto, @MappingTarget Course course);
}
