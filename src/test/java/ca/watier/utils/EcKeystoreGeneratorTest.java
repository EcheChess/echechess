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

package ca.watier.utils;

import ca.watier.echesscommon.utils.EcKeystoreGenerator;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.junit.Test;

import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by yannick on 6/20/2017.
 */
public class EcKeystoreGeneratorTest {

    @Test
    public void createKeystore() throws Exception {
        EcKeystoreGenerator.KeystorePasswordHolder keystoreHolder = EcKeystoreGenerator.createKeystore();
        assertThat(keystoreHolder).isNotNull();

        KeyStore keyStore = keystoreHolder.getKeyStore();
        String password = keystoreHolder.getPassword();

        assertThat(keyStore).isNotNull();
        assertThat(password).isNotNull();

        Certificate cert = keyStore.getCertificate(EcKeystoreGenerator.ALIAS);
        assertThat(cert).isNotNull();

        PublicKey publicKey = cert.getPublicKey();
        assertThat(publicKey.getAlgorithm()).isNotNull().isEqualTo("EC");
        assertThat(cert.getType()).isNotNull().isEqualTo("X.509");

        try {
            cert.verify(publicKey);
        } catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException ex) {
            fail(ex.getMessage());
        }

        X509Certificate x509Cert = (X509Certificate) cert;

        try {
            x509Cert.checkValidity(); //Valid 3 months
        } catch (CertificateExpiredException | CertificateNotYetValidException ex) {
            fail(ex.getMessage());
        }

        DateTime notBefore = new DateTime(x509Cert.getNotBefore());
        DateTime notAfter = new DateTime(x509Cert.getNotAfter());

        assertEquals(3, Months.monthsBetween(notBefore, notAfter).getMonths()); //Valid 3 months
    }
}