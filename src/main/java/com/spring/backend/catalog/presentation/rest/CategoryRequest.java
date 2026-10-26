package com.spring.backend.catalog.presentation.rest;

/** Matches legacy CategoryRequestDto JSON: {"name": "...", "note": "..."}. */
public record CategoryRequest(String name, String note) {}
