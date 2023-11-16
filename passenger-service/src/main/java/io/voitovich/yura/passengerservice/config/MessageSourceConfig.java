package io.voitovich.yura.passengerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {

    @Value("${message.source.basenames}")
    private String messageSourceBasenames;
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasenames(
                messageSourceBasenames
                );
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
