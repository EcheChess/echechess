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

import ca.watier.echechess.EcheChessApplication;
import ca.watier.echechess.common.utils.KeystoreGenerator;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import static ca.watier.echechess.common.utils.KeystoreGenerator.PRNG;
import static ca.watier.echechess.common.utils.KeystoreGenerator.PROVIDER_NAME;
import static org.bouncycastle.asn1.x500.style.BCStyle.GIVENNAME;
import static org.bouncycastle.asn1.x500.style.BCStyle.O;


@Profile("prod")
@Configuration
public class ProdSecurityConfiguration {
    private static final int SECURE_PORT = 8443;
    private static final int WEB_PORT = 8080;
    private static final Map<ASN1ObjectIdentifier, String> CERT_USER_INFOS;
    private static final KeystoreGenerator.KeystorePasswordHolder CURRENT_KEYSTORE_HOLDER;
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(EcheChessApplication.class);

    static {
        CERT_USER_INFOS = new HashMap<>();
        CERT_USER_INFOS.put(GIVENNAME, "Yannick Watier");
        CERT_USER_INFOS.put(O, "Doi9t");
        CURRENT_KEYSTORE_HOLDER =
                KeystoreGenerator.createEcWithDefaultCurveKeystoreAndPassword(
                        KeystoreGenerator.MAIN_SIGNING_ALG_SHA512_EC,
                        36,
                        CERT_USER_INFOS);
    }

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory() {
        JettyServletWebServerFactory jettyServletWebServerFactory = new JettyServletWebServerFactory();
        jettyServletWebServerFactory.addConfigurations(new HttpToHttpsJettyConfiguration());
        jettyServletWebServerFactory.addServerCustomizers(server -> {
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

        return jettyServletWebServerFactory;
    }


    private class HttpToHttpsJettyConfiguration extends AbstractConfiguration {
        // http://wiki.eclipse.org/Jetty/Howto/Configure_SSL#Redirecting_http_requests_to_https
        @Override
        public void configure(WebAppContext context) {
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
