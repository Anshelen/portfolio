package dev.shelenkov.portfolio.support;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
public class MockApplicationEventPublisherAutoConfiguration {

    @Bean
    @Primary
    public ApplicationEventPublisher publisher() {
        return new MockApplicationEventPublisher();
    }
}
