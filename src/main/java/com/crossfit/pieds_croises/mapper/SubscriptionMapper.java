package com.crossfit.pieds_croises.mapper;

import com.crossfit.pieds_croises.dto.SubscriptionDto;
import com.crossfit.pieds_croises.model.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")

public interface SubscriptionMapper {
  @Mapping(source = "box.id", target = "boxId")
  SubscriptionDto convertToSubscriptionDto(Subscription subscription);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userSubscriptions", ignore = true)
  @Mapping(target = "box", ignore = true)
  Subscription convertToSubscriptionEntity(SubscriptionDto subscriptionDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "box", ignore = true)
  @Mapping(target = "userSubscriptions", ignore = true)
  void updateSubscriptionFromDto(SubscriptionDto subscriptionDto, @MappingTarget Subscription subscription);
}
