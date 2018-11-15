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

import ca.watier.echechess.components.AuthFailureHandlerImpl;
import ca.watier.echechess.components.AuthSuccessHandlerImpl;
import ca.watier.echechess.components.CustomCsrfTokenFilter;
import ca.watier.echechess.components.UserDetailsServiceImpl;
import ca.watier.echechess.repositories.UserRepository;
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
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;


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
                .antMatchers("/", "/favicon.ico").permitAll()
                .anyRequest().authenticated()
                .and()
                .authenticationProvider(daoAuthenticationProvider(passwordEncoder))
                .addFilterAfter(customCsrfTokenFilter(), CsrfFilter.class)
                .formLogin()
                .loginPage("/")
                .loginProcessingUrl("/api/v1/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(authSuccessHandler())
                .failureHandler(authFailureHandler());
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder) {
        final DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }


    @Bean
    public CustomCsrfTokenFilter customCsrfTokenFilter() {
        return new CustomCsrfTokenFilter();
    }

    @Bean
    public AuthenticationSuccessHandler authSuccessHandler() {
        return new AuthSuccessHandlerImpl();
    }

    @Bean
    public AuthenticationFailureHandler authFailureHandler() {
        return new AuthFailureHandlerImpl();
    }
}
