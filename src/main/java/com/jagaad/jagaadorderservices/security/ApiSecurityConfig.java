package com.payment.gateway.security.config.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;

/**
 * @author patrick
 * @project
 */
@Configuration
@Order(1)
@EnableWebSecurity
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AppBasicAuthenticationProvider appAuthenticationProvider;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/api/**")
                .csrf().disable()
                //enable cors to all to be disabled in future for jeremy purposes
                .cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues()).and()
                .authorizeRequests()
                .antMatchers("/api/v1/m/callback/*", "/api/v1/m/callback/**").permitAll()
                .anyRequest().authenticated()
                .and().httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint);

       /* http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER ).and()
                .antMatcher("/api/**")
                .csrf().disable()
                //enable cors to all to be disabled in future for jeremy purposes
                .cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues()).and()
                .authorizeRequests()
                .antMatchers("/api/v1/m/callback/*", "/api/v1/m/callback/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                //.accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint( authenticationEntryPoint )
                .and()
              .httpBasic().realmName("API");*/

    }

/*    // To enable CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("https://thikaridge.co.ke","http://thikaridge.co.ke")); // www - obligatory
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }*/

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(appAuthenticationProvider);
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }


}

