package com.etree.harness.common.logging;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    /**
     * Registers the {@link CorrelationIdFilter} so it runs early in the filter
     * chain for all incoming requests.
     *
     * @param filter the correlation id filter bean
     * @return a configured filter registration
     */
    @Bean
    public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilterRegistration(CorrelationIdFilter filter) {
        FilterRegistrationBean<CorrelationIdFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(filter);
        reg.setOrder(Integer.MIN_VALUE + 10);
        reg.addUrlPatterns("/*");
        return reg;
    }

}
