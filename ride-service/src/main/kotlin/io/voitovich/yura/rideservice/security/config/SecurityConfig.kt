package io.voitovich.yura.rideservice.security.config

import io.voitovich.yura.rideservice.security.converter.JwtAuthConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    companion object {
        private val AUTH_WHITELIST = arrayOf(
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/actuator/*",
            "/swagger-ui/**"
        )
    }

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
        http.oauth2ResourceServer { httpSecurityOAuth2ResourceServerConfigurer ->
            httpSecurityOAuth2ResourceServerConfigurer
                .jwt { o -> o.jwtAuthenticationConverter(JwtAuthConverter()) }
        }
        http.sessionManagement { httpSecuritySessionManagementConfigurer ->
            httpSecuritySessionManagementConfigurer.sessionCreationPolicy(
                STATELESS
            )
        }
        http.authorizeHttpRequests { o ->
            o.requestMatchers(*AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated()
        }
        return http.build()
    }


}
