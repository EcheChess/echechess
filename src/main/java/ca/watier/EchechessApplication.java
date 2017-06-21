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

package ca.watier;

import ca.watier.sessions.Player;
import ca.watier.utils.EcKeystoreGenerator;
import ca.watier.utils.EcKeystoreGenerator.KeystorePasswordHolder;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyStore;
import java.util.UUID;

import static ca.watier.utils.CacheConstants.CACHE_UI_SESSION_EXPIRY;
import static ca.watier.utils.EcKeystoreGenerator.PRNG;
import static ca.watier.utils.EcKeystoreGenerator.PROVIDER_NAME;
import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;

@SpringBootApplication
@EnableAutoConfiguration
public class EchechessApplication {
    private static final int SECURE_PORT = 8443;
    private static final int WEB_PORT = 8080;
    private static final KeystorePasswordHolder CURRENT_KEYSTORE_HOLDER = EcKeystoreGenerator.createKeystore();
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(EchechessApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EchechessApplication.class, args);
    }

    @Bean
    public CacheConfigurationBuilder<UUID, Player> uuidPlayerCacheConfiguration() {
        return newCacheConfigurationBuilder(UUID.class, Player.class, ResourcePoolsBuilder.heap(100))
                .withExpiry(CACHE_UI_SESSION_EXPIRY);
    }

    @Configuration
    public class HttpToHttpsJettyCustomizer implements EmbeddedServletContainerCustomizer {
        @Override
        public void customize(ConfigurableEmbeddedServletContainer container) {
            JettyEmbeddedServletContainerFactory containerFactory = (JettyEmbeddedServletContainerFactory) container;
            //Add a plain HTTP connector and a WebAppContext config to force redirect from http->https
            containerFactory.addConfigurations(new HttpToHttpsJettyConfiguration());
            containerFactory.addServerCustomizers(server -> {
                HttpConfiguration httpsConfig = new HttpConfiguration();
                httpsConfig.addCustomizer(new SecureRequestCustomizer());
                httpsConfig.setSecureScheme("https");
                httpsConfig.setSecurePort(SECURE_PORT);

                HttpConfiguration httpConfig = new HttpConfiguration();
                httpConfig.addCustomizer(new SecureRequestCustomizer());
                httpConfig.setSecurePort(SECURE_PORT);

                SslContextFactory sslContextFactory = new SslContextFactory();
                sslContextFactory.setKeyStoreProvider(PROVIDER_NAME);
                sslContextFactory.setSecureRandomAlgorithm(PRNG);
                sslContextFactory.setIncludeProtocols("TLSv1.2");

                if (CURRENT_KEYSTORE_HOLDER == null) {
                    LOGGER.error("INVALID KEYSTORE HOLDER (NULL)");
                    System.exit(1);
                }

                KeyStore keyStore = CURRENT_KEYSTORE_HOLDER.getKeyStore();

                if (keyStore == null) {
                    LOGGER.error("INVALID KEYSTORE (NULL)");
                    System.exit(1);
                }

                sslContextFactory.setKeyStore(keyStore);
                sslContextFactory.setKeyStorePassword(CURRENT_KEYSTORE_HOLDER.getPassword());

                sslContextFactory.setIncludeCipherSuites(
                        "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                        "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256"
                );

                ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
                http.setPort(WEB_PORT);

                ServerConnector https = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()), new HttpConnectionFactory(httpsConfig));
                https.setPort(SECURE_PORT);
                https.setIdleTimeout(500000);

                server.setConnectors(new Connector[]{https, http});
            });
        }
    }

    private class HttpToHttpsJettyConfiguration extends AbstractConfiguration {
        // http://wiki.eclipse.org/Jetty/Howto/Configure_SSL#Redirecting_http_requests_to_https
        @Override
        public void configure(WebAppContext context) throws Exception {
            Constraint constraint = new Constraint();
            constraint.setDataConstraint(Constraint.DC_CONFIDENTIAL);

            ConstraintMapping constraintMapping = new ConstraintMapping();
            constraintMapping.setPathSpec("/*");
            constraintMapping.setConstraint(constraint);

            ConstraintSecurityHandler constraintSecurityHandler = new ConstraintSecurityHandler();
            constraintSecurityHandler.addConstraintMapping(constraintMapping);

            context.setSecurityHandler(constraintSecurityHandler);
        }
    }

}
