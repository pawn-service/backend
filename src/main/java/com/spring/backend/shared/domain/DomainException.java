package com.spring.backend.shared.domain;

/**
 * Base exception for all domain-layer errors. Subclass per bounded context. Infrastructure maps
 * these to HTTP status codes.
 */
public abstract class DomainException extends RuntimeException {

  protected DomainException(String message) {
    super(message);
  }
}
