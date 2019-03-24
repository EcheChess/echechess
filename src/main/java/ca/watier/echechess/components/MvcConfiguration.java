/*
 *    Copyright 2014 - 2018 Yannick Watier
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

package ca.watier.echechess.components;

import ca.watier.echechess.common.interfaces.WebSocketService;
import ca.watier.echechess.server.UiSessionHandlerInterceptor;
import ca.watier.echechess.services.UiSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import java.util.concurrent.TimeUnit;

/**
 * Created by yannick on 6/12/2017.
 */

@EnableWebMvc
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    private static final CacheControl CACHE_CONTROL_TWO_HOURS = CacheControl.maxAge(2, TimeUnit.HOURS).cachePublic();
    private final UiSessionService uiSessionService;
    private final WebSocketService webSocketService;

    @Autowired
    public MvcConfiguration(UiSessionService uiSessionService, WebSocketService webSocketService) {
        this.uiSessionService = uiSessionService;
        this.webSocketService = webSocketService;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor()).addPathPatterns("/api/game/**");
    }

    @Bean
    public UiSessionHandlerInterceptor interceptor() {
        return new UiSessionHandlerInterceptor(uiSessionService, webSocketService);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/scripts/**").addResourceLocations("/static/scripts/").setCacheControl(CACHE_CONTROL_TWO_HOURS);
        registry.addResourceHandler("/images/**").addResourceLocations("/static/images/").setCacheControl(CACHE_CONTROL_TWO_HOURS);
        registry.addResourceHandler("/style/**").addResourceLocations("/static/style/").setCacheControl(CACHE_CONTROL_TWO_HOURS);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver =
                new InternalResourceViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
}
