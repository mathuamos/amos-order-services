package com.jagaad.jagaadorderservices.configs;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;




@TestConfiguration
public class SecurityConfigTest {

    @Bean
    public WebSecurityConfigurerAdapter webSecurityConfigurerAdapter() {
        return new WebSecurityConfigurerAdapter() {
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http.csrf().disable().authorizeRequests().anyRequest().permitAll().and().httpBasic().disable();
            }
        };
    }
}