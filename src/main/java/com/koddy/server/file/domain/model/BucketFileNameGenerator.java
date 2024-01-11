package com.koddy.server.file.domain.model;

@FunctionalInterface
public interface BucketFileNameGenerator {
    String get(final String fileName);
}
