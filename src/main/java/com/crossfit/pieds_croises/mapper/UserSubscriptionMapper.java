package com.crossfit.pieds_croises.mapper;

import com.crossfit.pieds_croises.dto.UserSubscriptionDto;
import com.crossfit.pieds_croises.model.UserSubscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface UserSubscriptionMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "subscriptionId", source = "subscription.id")
    UserSubscriptionDto convertToUserSubscriptionDto(UserSubscription userSubscription);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "user.id", source = "userSubscriptionDto.userId")
    @Mapping(target = "subscription.id", source = "userSubscriptionDto.subscriptionId")
    UserSubscription convertToUserSubscriptionEntity(UserSubscriptionDto userSubscriptionDto);

}
