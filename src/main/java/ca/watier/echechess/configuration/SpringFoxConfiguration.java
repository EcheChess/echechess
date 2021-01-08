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

package ca.watier.echechess.configuration;

import ca.watier.echechess.common.enums.CasePosition;
import ca.watier.echechess.models.CasePositionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpSession;
import java.util.List;

@Configuration
@EnableSwagger2
public class SpringFoxConfiguration {
    public static final AuthorizationScope READ_SCOPE = new AuthorizationScope("read", null);

    @Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(HttpSession.class)
                .directModelSubstitute(CasePosition.class, CasePositionModel.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("ca.watier"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContextes());
    }


    @Bean
    public SecurityConfiguration swaggerSecurityConfiguration() {
        return SecurityConfigurationBuilder
                .builder()
                .clientId("clientId")
                .clientSecret("secret")
                .useBasicAuthenticationWithAccessCodeGrant(true)
                .scopeSeparator(",")
                .build();
    }

    private List<SecurityScheme> securitySchemes() {
        GrantType grantType = new ResourceOwnerPasswordCredentialsGrant("/oauth/token");
        return List.of(
                new OAuthBuilder()
                        .name("oauth2")
                        .scopes(List.of(READ_SCOPE))
                        .grantTypes(List.of(grantType))
                        .build());
    }

    private List<SecurityContext> securityContextes() {
        return List.of(SecurityContext.builder()
                .securityReferences(securityReferences())
                .operationSelector(operationContext -> true)
                .build());
    }

    private List<SecurityReference> securityReferences() {
        AuthorizationScope[] authorizationScopes = {
                READ_SCOPE
        };

        return List.of(new SecurityReference("oauth2", authorizationScopes));
    }

}
