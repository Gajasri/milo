/*
 * Copyright (c) 2017 Kevin Herron
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.html.
 */

package org.eclipse.milo.opcua.stack.core.util;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SelfSignedCertificateBuilder {

    public static final String SA_SHA1_RSA = "SHA1withRSA";
    public static final String SA_SHA256_RSA = "SHA256withRSA";
    public static final String SA_SHA256_ECDSA = "SHA256withECDSA";

    private Period validityPeriod = Period.ofYears(3);

    private String commonName = "";
    private String organization = "";
    private String organizationalUnit = "";
    private String localityName = "";
    private String stateName = "";
    private String countryCode = "";

    private String applicationUri = "";
    private List<String> dnsNames = new ArrayList<>();
    private List<String> ipAddresses = new ArrayList<>();
    private String signatureAlgorithm = SA_SHA256_RSA;

    private final KeyPair keyPair;
    private final SelfSignedCertificateGenerator generator;

    public SelfSignedCertificateBuilder(KeyPair keyPair) {
        this(keyPair, new SelfSignedCertificateGenerator());
    }

    public SelfSignedCertificateBuilder(KeyPair keyPair, SelfSignedCertificateGenerator generator) {
        this.keyPair = keyPair;
        this.generator = generator;

        if (keyPair.getPublic() instanceof RSAPublicKey) {
            signatureAlgorithm = SA_SHA256_RSA;
        } else if (keyPair.getPublic() instanceof ECPublicKey) {
            signatureAlgorithm = SA_SHA256_ECDSA;
        }
    }

    public SelfSignedCertificateBuilder setValidityPeriod(Period validityPeriod) {
        this.validityPeriod = validityPeriod;
        return this;
    }

    public SelfSignedCertificateBuilder setCommonName(String commonName) {
        this.commonName = commonName;
        return this;
    }

    public SelfSignedCertificateBuilder setOrganization(String organization) {
        this.organization = organization;
        return this;
    }

    public SelfSignedCertificateBuilder setOrganizationalUnit(String organizationalUnit) {
        this.organizationalUnit = organizationalUnit;
        return this;
    }

    public SelfSignedCertificateBuilder setLocalityName(String localityName) {
        this.localityName = localityName;
        return this;
    }

    public SelfSignedCertificateBuilder setStateName(String stateName) {
        this.stateName = stateName;
        return this;
    }

    public SelfSignedCertificateBuilder setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public SelfSignedCertificateBuilder setApplicationUri(String applicationUri) {
        this.applicationUri = applicationUri;
        return this;
    }

    public SelfSignedCertificateBuilder addDnsName(String dnsName) {
        dnsNames.add(dnsName);
        return this;
    }

    public SelfSignedCertificateBuilder addIpAddress(String ipAddress) {
        ipAddresses.add(ipAddress);
        return this;
    }

    public SelfSignedCertificateBuilder setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
        return this;
    }

    public X509Certificate build() throws Exception {
        // Calculate start and end date based on validity period
        LocalDate now = LocalDate.now();
        LocalDate expiration = now.plus(validityPeriod);

        Date notBefore = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date notAfter = Date.from(expiration.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return generator.generateSelfSigned(
            keyPair,
            notBefore,
            notAfter,
            commonName,
            organization,
            organizationalUnit,
            localityName,
            stateName,
            countryCode,
            applicationUri,
            dnsNames,
            ipAddresses,
            signatureAlgorithm
        );
    }

}
