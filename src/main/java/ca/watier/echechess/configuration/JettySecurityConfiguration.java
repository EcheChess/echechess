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

import ca.watier.echechess.common.utils.KeystoreGenerator;
import ca.watier.echechess.components.HttpToHttpsJettyConfiguration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import static ca.watier.echechess.common.utils.KeystoreGenerator.PRNG;
import static ca.watier.echechess.common.utils.KeystoreGenerator.PROVIDER_NAME;
import static org.bouncycastle.asn1.x500.style.BCStyle.GIVENNAME;
import static org.bouncycastle.asn1.x500.style.BCStyle.O;


@Configuration
public class JettySecurityConfiguration {
    private static final int SECURE_PORT = 8443;
    private static final int WEB_PORT = 8080;
    private static final Map<ASN1ObjectIdentifier, String> CERT_USER_INFOS;
    private static final KeystoreGenerator.KeystorePasswordHolder CURRENT_KEYSTORE_HOLDER;
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JettySecurityConfiguration.class);

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
    public JettyServerCustomizer securityServerCustomizer() {
        return server -> {
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
        };
    }


    @Bean
    public HttpToHttpsJettyConfiguration httpToHttpsJettyConfiguration() {
        return new HttpToHttpsJettyConfiguration();
    }
}
