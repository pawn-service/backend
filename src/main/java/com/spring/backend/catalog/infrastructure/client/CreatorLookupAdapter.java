package com.spring.backend.catalog.infrastructure.client;

import com.spring.backend.catalog.application.port.out.CreatorLookupPort;
import com.spring.backend.entity.UserEntity;
import com.spring.backend.repository.UserRepository;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adapts the legacy UserRepository to the catalog's CreatorLookupPort.
 *
 * <p>Temporary: when an identity bounded context is created, this adapter will call its application
 * API instead of the legacy repository directly.
 */
@Component
@RequiredArgsConstructor
public class CreatorLookupAdapter implements CreatorLookupPort {

  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  private final UserRepository userRepository;

  @Override
  public Optional<CreatorInfo> findById(Long userId) {
    return userRepository.findById(userId).map(CreatorLookupAdapter::toCreatorInfo);
  }

  @Override
  public Map<Long, CreatorInfo> findByIds(Set<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return Map.of();
    }
    return userRepository.findAllById(userIds).stream()
        .map(CreatorLookupAdapter::toCreatorInfo)
        .collect(Collectors.toMap(CreatorInfo::id, Function.identity()));
  }

  private static CreatorInfo toCreatorInfo(UserEntity entity) {
    return new CreatorInfo(
        entity.getId(),
        entity.getEmail(),
        entity.getName(),
        entity.getUsername(),
        entity.getAge(),
        entity.getBirthDate() != null ? entity.getBirthDate().format(DATE_FMT) : null,
        entity.getPhone(),
        entity.getCardId(),
        entity.getAddress(),
        entity.getGender(),
        entity.getRole() != null ? entity.getRole().name() : null,
        entity.getAvatar());
  }
}
