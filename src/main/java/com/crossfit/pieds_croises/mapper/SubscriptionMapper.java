package com.crossfit.pieds_croises.mapper;

import com.crossfit.pieds_croises.dto.SubscriptionCreateDto;
import com.crossfit.pieds_croises.dto.SubscriptionDto;
import com.crossfit.pieds_croises.model.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")

public interface SubscriptionMapper {
    SubscriptionDto convertToSubscriptionDto(Subscription subscription);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userSubscriptions", ignore = true)
    Subscription convertToSubscriptionEntity(SubscriptionCreateDto subscriptionCreateDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userSubscriptions", ignore = true)
    void updateSubscriptionFromDto(SubscriptionDto subscriptionDto, @MappingTarget Subscription subscription);
}
