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


import ca.watier.echechess.components.UndertowHttpToHttpsCustomizerImpl;
import ca.watier.keystore.generator.KeystoreGenerator;
import ca.watier.keystore.generator.exceptions.GenerationException;
import ca.watier.keystore.generator.models.KeystorePasswordHolder;
import io.undertow.Undertow;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.bouncycastle.asn1.x500.style.BCStyle.GIVENNAME;
import static org.bouncycastle.asn1.x500.style.BCStyle.O;

@Configuration
public class UndertowConfiguration implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {
    private static final KeystorePasswordHolder CURRENT_KEYSTORE_HOLDER = Objects.requireNonNull(buildKeystore());
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(UndertowConfiguration.class);
    public static final String LOCALHOST = "127.0.0.1";

    private final boolean redirectHttpToHttps;
    private final int securePort;
    private final int httpPort;

    public UndertowConfiguration(@Value("${app.transport.http.redirect-to-https:true}") boolean redirectHttpToHttps,
                                 @Value("${app.transport.https.port:8443}") int httpsPort,
                                 @Value("${app.transport.http.port:8080}") int httpPort) {
        this.redirectHttpToHttps = redirectHttpToHttps;
        this.securePort = httpsPort;
        this.httpPort = httpPort;
    }

    private static KeystorePasswordHolder buildKeystore() {
        Map<ASN1ObjectIdentifier, String> objectIdentifierHashMap = new HashMap<>();
        objectIdentifierHashMap.put(GIVENNAME, "Yannick Watier");
        objectIdentifierHashMap.put(O, "Doi9t");
        try {
            return KeystoreGenerator.createEcPkcs12Keystore(
                    "SHA512withECDSA",
                    "secp384r1",
                    null,
                    36,
                    objectIdentifierHashMap);
        } catch (GenerationException e) {
            return null;
        }
    }

    @Override
    public void customize(UndertowServletWebServerFactory factory) {
        if(redirectHttpToHttps) {
            factory.addDeploymentInfoCustomizers(undertowHttpToHttpsCustomizer());
        }

        factory.setPort(httpPort);
    }

    @Bean
    public UndertowDeploymentInfoCustomizer undertowHttpToHttpsCustomizer() {
        return new UndertowHttpToHttpsCustomizerImpl(securePort);
    }

    @Bean
    public UndertowServletWebServerFactory servletWebServerFactory() {
        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();

        factory.addBuilderCustomizers(new UndertowBuilderCustomizer() {
            @Override
            public void customize(Undertow.Builder builder) {
                try {
                    SSLContext sslContext = buildSslContextFromKeystore();

                    builder.addHttpsListener(securePort, LOCALHOST, sslContext);
                } catch (NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException | KeyStoreException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        });

        return factory;
    }

    private SSLContext buildSslContextFromKeystore() throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
        KeyStore keyStore = CURRENT_KEYSTORE_HOLDER.getKeyStore();
        String password = CURRENT_KEYSTORE_HOLDER.getPassword();

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        return sslContext;
    }
}
