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
            target = "usersId",
            expression = "java(course.getUsers() != null ? course.getUsers().stream().map(com.crossfit.pieds_croises.model.User::getId).toList() : java.util.Collections.emptyList())")
    @Mapping(
            target = "coachName",
            expression = "java(course.getCoach() != null ? course.getCoach().getFirstname() + \" \" + course.getCoach().getLastname() : null)")
    @Mapping(
            target = "coachId",
            expression = "java(course.getCoach() != null ? course.getCoach().getId() : null)")
    CourseDTO convertToDto(Course course);

    @Mapping(target = "status", expression = "java(Course.Status.OPEN)")
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "coach", ignore = true)
    Course convertToEntity(CourseCreateDTO courseCreateDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "coach", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateFromDTO(CourseUpdateDTO dto, @MappingTarget Course course);
}
