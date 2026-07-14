package com.ctdecomerce.store.config;

import com.stripe.Stripe;
import com.stripe.StripeClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    @Value("${stripe.api.key}")
    private String stipeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stipeApiKey;
    }

    @Bean
    public StripeClient stripeClient() {
        return new StripeClient(stipeApiKey);
    }
}
