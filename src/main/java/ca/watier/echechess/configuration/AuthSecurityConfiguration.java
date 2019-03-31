/*
 *    Copyright 2014 - 2017 Yannick Watier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ca.watier.echechess.configuration;

import ca.watier.echechess.components.UserDetailsServiceImpl;
import ca.watier.echechess.repositories.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


@Configuration
public class AuthSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthSecurityConfiguration(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(userRepository);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/favicon.ico", "/images/**", "/style/**", "/scripts/**", "/websocket/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .authenticationProvider(daoAuthenticationProvider(passwordEncoder))
                .formLogin()
                .loginPage("/")
                .loginProcessingUrl("/api/v1/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .and()
                .csrf()
                .requireCsrfProtectionMatcher(requireCsrfProtectionMatcher());
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder) {
        final DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    @Bean
    public RequestMatcher requireCsrfProtectionMatcher() {
        return (HttpServletRequest httpServletRequest) -> {
            if (Objects.isNull(httpServletRequest)) {
                throw new IllegalStateException("The HttpServletRequest is null!");
            }

            String url = httpServletRequest.getRequestURI();
            String method = httpServletRequest.getMethod();

            if (isResource(url, method)) {
                return false;
            }

            switch (url) {
                case "/":
                case "/favicon.ico":
                    return false;
                case "/api/v1/user":
                    return !"PUT".equals(method);
                default:
                    return true;
            }
        };
    }

    private boolean isResource(String url, String method) {
        return "GET".equalsIgnoreCase(method) && StringUtils.startsWithAny(url, "/script", "/images", "/style");
    }
}
