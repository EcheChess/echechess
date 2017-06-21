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

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;

/**
 * Created by yannick on 6/5/2017.
 */
public class EcKeystoreGenerator {
    public static final String PROVIDER_NAME = BouncyCastleProvider.PROVIDER_NAME;
    public static final String PRNG = "SHA1PRNG";
    public static final String ALIAS = "alias";
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(EcKeystoreGenerator.class);
    private static final String KEYPAIR_SIGNING_ALG = "ECDSA";
    private static final String EC_CURVE = "secp384r1";
    private static final short SERIAL_BYTES_LENGTH = 1024;
    private static SecureRandom secureRandom = null;

    static {
        if (Security.getProvider(PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e2) {
            secureRandom = new SecureRandom();
        }
    }

    private EcKeystoreGenerator() {
    }

    public static KeystorePasswordHolder createKeystore() {

        String password = nextPassword();
        KeyStore keyStore;
        KeyPair pair = generateEcdsaKeyPair();

        if (pair == null) {
            return null;
        }

        PublicKey publicKey = pair.getPublic();
        PrivateKey privateKey = pair.getPrivate();

        X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE)
                .addRDN(BCStyle.C, "xx")
                .addRDN(BCStyle.O, "Doi9t Corp.")
                .addRDN(BCStyle.OU, "xx")
                .addRDN(BCStyle.CN, "Root CA")
                .addRDN(BCStyle.E, "xx");

        X500Name xName = nameBuilder.build();
        BigInteger serialNumber = new BigInteger(SERIAL_BYTES_LENGTH, secureRandom);
        DateTime startDate = new DateTime();
        DateTime endDate = startDate.plusMonths(3);
        SubjectPublicKeyInfo infos = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        X509v1CertificateBuilder certBuilder = new X509v1CertificateBuilder(xName, serialNumber, startDate.toDate(), endDate.toDate(), xName, infos);

        ContentSigner signer;
        try {
            signer = new JcaContentSignerBuilder("SHA512withECDSA").build(privateKey);
        } catch (OperatorCreationException e1) {
            LOGGER.error(e1.toString(), e1);
            return null;
        }

        X509CertificateHolder certHolder = certBuilder.build(signer);

        try {
            X509Certificate cert = (new JcaX509CertificateConverter()).getCertificate(certHolder);

            keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null, null);

            keyStore.setKeyEntry(ALIAS, pair.getPrivate(), password.toCharArray(), new java.security.cert.Certificate[]{cert});
        } catch (final Exception e) {
            throw new IllegalStateException("Errors during assembling root CA.", e);
        }


        return new KeystorePasswordHolder(password, keyStore);
    }

    /**
     * Gives a 64 chars password (5 bits * 64 = 320) <br>
     * Thanks to erickson from StackOverflow (https://stackoverflow.com/a/41156)
     *
     * @return
     */
    private static String nextPassword() {
        return new BigInteger(320, secureRandom).toString(32);
    }

    private static KeyPair generateEcdsaKeyPair() {
        KeyPairGenerator keyGenerator = null;

        try {
            keyGenerator = KeyPairGenerator.getInstance(KEYPAIR_SIGNING_ALG, PROVIDER_NAME);
            keyGenerator.initialize(ECNamedCurveTable.getParameterSpec(EC_CURVE), secureRandom);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e1) {
            LOGGER.error(e1.toString(), e1);
        }

        return keyGenerator != null ? keyGenerator.generateKeyPair() : null;
    }

    public static class KeystorePasswordHolder {
        private final String password;
        private final KeyStore keyStore;

        public KeystorePasswordHolder(String password, KeyStore keyStore) {
            this.password = password;
            this.keyStore = keyStore;
        }

        public String getPassword() {
            return password;
        }

        public KeyStore getKeyStore() {
            return keyStore;
        }
    }
}
