package com.crossfit.pieds_croises.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionDto {
    private Long id;
    private String name;
    private int price;
    private Integer sessionPerWeek;
    private short duration;
    private String terminationConditions;
    private int freezeDaysAllowed;
}
