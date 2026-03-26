package com.etree.harness.common.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link AuditorAware} used by Spring Data JPA to provide
 * the current auditor (user) for auditing fields. The default implementation
 * currently returns an empty Optional.
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * Return the current auditor's username or an empty Optional when unknown.
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.empty();
    }
}
