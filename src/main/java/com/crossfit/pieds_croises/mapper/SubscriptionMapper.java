package com.crossfit.pieds_croises.mapper;

import com.crossfit.pieds_croises.dto.SubscriptionDto;
import com.crossfit.pieds_croises.model.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface SubscriptionMapper {
    @Mapping(target = "boxId", source = "subscription.box.id")
    SubscriptionDto convertToSubscriptionDto(Subscription subscription);

    @Mapping(target = "id", ignore = true)
    Subscription convertToSubscriptionEntity(SubscriptionDto subscriptionDto);
}
