package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.dto.SubscriptionDto;
import com.crossfit.pieds_croises.exception.DuplicateResourceException;
import com.crossfit.pieds_croises.exception.ResourceNotFoundException;
import com.crossfit.pieds_croises.mapper.SubscriptionMapper;
import com.crossfit.pieds_croises.model.Box;
import com.crossfit.pieds_croises.model.Subscription;
import com.crossfit.pieds_croises.repository.BoxRepository;
import com.crossfit.pieds_croises.repository.SubscriptionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SubscriptionService {
  private final SubscriptionRepository subscriptionRepository;
  private final BoxRepository boxRepository;
  private final SubscriptionMapper subscriptionMapper;

  public SubscriptionDto addSubscription(SubscriptionDto subscriptionDto) {
    if (subscriptionDto.getBoxId() == null) {
      throw new IllegalArgumentException("Box id is required");
    } else {
      Long boxId = subscriptionDto.getBoxId();
      Box box = boxRepository.findById(boxId).orElseThrow(() -> new ResourceNotFoundException("Box not found"));

      if (subscriptionRepository.existsByName(subscriptionDto.getName())) {
        throw new DuplicateResourceException("A subscription with this name already exists");
      }

      if (subscriptionRepository.existsByPrice(subscriptionDto.getPrice())) {
        throw new DuplicateResourceException("A subscription with this price already exists");
      }

      Subscription subscription = subscriptionMapper.convertToSubscriptionEntity(subscriptionDto);
      subscription.setBox(box);

      Subscription savedSubscription = subscriptionRepository.save(subscription);
      return subscriptionMapper.convertToSubscriptionDto(savedSubscription);
    }
  }

  public SubscriptionDto getSubscriptionById(Long id) {
    Subscription subscription = subscriptionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
    return subscriptionMapper.convertToSubscriptionDto(subscription);
  }

  public SubscriptionDto updateSubscription(Long id, SubscriptionDto subscriptionDto) {
    Subscription subscription = subscriptionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

    if (subscriptionRepository.existsByName(subscriptionDto.getName())) {
      throw new DuplicateResourceException("A subscription with this name already exists");
    }
    if (subscriptionRepository.existsByPrice(subscriptionDto.getPrice())) {
      throw new DuplicateResourceException("A subscription with this price already exists");
    }

    subscriptionMapper.updateSubscriptionFromDto(subscriptionDto, subscription);
    Subscription updatedSubscription = subscriptionRepository.save(subscription);
    return subscriptionMapper.convertToSubscriptionDto(updatedSubscription);
  }

  public void deleteSubscription(Long id) {
    Subscription subscription = subscriptionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
    subscriptionRepository.delete(subscription);
  }

  public List<SubscriptionDto> getAllSubscriptionsByBoxId(Long boxId) {
    Box box = boxRepository.findById(boxId)
        .orElseThrow(() -> new ResourceNotFoundException("Box not found"));
    List<Subscription> subscriptions = subscriptionRepository.findByBox(box);
    return subscriptions.stream()
        .map(subscriptionMapper::convertToSubscriptionDto)
        .collect(Collectors.toList());
  }
}
