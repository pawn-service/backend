package com.spring.backend.catalog.application.port.out;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Port to look up user info from the identity context. Catalog only needs display data (name,
 * username, etc.) — never passwords, tokens, or security details.
 */
public interface CreatorLookupPort {

  Optional<CreatorInfo> findById(Long userId);

  /** Bulk lookup to avoid N+1 queries on list/search endpoints. */
  Map<Long, CreatorInfo> findByIds(Set<Long> userIds);

  /** Minimal user info needed by catalog for display purposes. */
  record CreatorInfo(
      Long id,
      String email,
      String name,
      String username,
      Integer age,
      String birthDate,
      String phone,
      String cardId,
      String address,
      String gender,
      String role,
      String avatar) {}
}
