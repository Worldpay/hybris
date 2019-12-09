package com.worldpay.service.payment.impl;

import com.google.common.collect.ImmutableMap;
import com.worldpay.config.merchant.ThreeDSFlexJsonWebTokenCredentials;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.payment.WorldpayJsonWebTokenService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayJsonWebTokenService implements WorldpayJsonWebTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorldpayJsonWebTokenService.class);

    private static final String TYP = "typ";
    private static final String ALG = "alg";
    private static final String JTI = "jti";
    private static final String ORG_UNIT_ID = "OrgUnitId";
    private static final String JWT = "JWT";
    private static final String RETURN_URL = "ReturnUrl";
    private static final String OBJECTIFY_PAYLOAD = "ObjectifyPayload";
    private static final String PAYLOAD = "Payload";
    private static final String ACS_URL = "ACSUrl";
    private static final String TRANSACTION_ID = "TransactionId";

    private final WorldpayUrlService worldpayUrlService;

    /**
     * Default constructor
     *
     * @param worldpayUrlService - the service to set
     */
    public DefaultWorldpayJsonWebTokenService(final WorldpayUrlService worldpayUrlService) {
        this.worldpayUrlService = worldpayUrlService;
    }

    private static Key retrieveSigningKey(final WorldpayMerchantConfigData worldpayMerchantConfigData) {
        final ThreeDSFlexJsonWebTokenCredentials threeDSFlexJsonWebTokenSettings = worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings();
        if (threeDSFlexJsonWebTokenSettings != null) {
            final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.forName(threeDSFlexJsonWebTokenSettings.getAlg());

            return new SecretKeySpec(threeDSFlexJsonWebTokenSettings.getJwtMacKey().getBytes(),
                    signatureAlgorithm.getJcaName());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createJsonWebTokenForDDC(final WorldpayMerchantConfigData worldpayMerchantConfigData) {
        final Key signingKey = retrieveSigningKey(worldpayMerchantConfigData);

        String jwt = null;
        if (signingKey != null) {
            final String alg = worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getAlg();
            jwt = Jwts.builder()
                    .setHeaderParam(TYP, JWT)
                    .setHeaderParam(ALG, alg)
                    .setIssuedAt(Date.from(Instant.now()))
                    .setIssuer(worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getIss())
                    .claim(JTI, UUID.randomUUID())
                    .claim(ORG_UNIT_ID, worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getOrgUnitId())
                    .signWith(signingKey, SignatureAlgorithm.forName(alg))
                    .compact();
        }

        LOG.info("DDC JWT: [{}]", jwt);

        return jwt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createJsonWebTokenFor3DSecureFlexChallengeIframe(final WorldpayMerchantConfigData worldpayMerchantConfigData, final DirectResponseData directResponseData) throws WorldpayConfigurationException {
        final Key signingKey = retrieveSigningKey(worldpayMerchantConfigData);

        String jwt = null;
        if (signingKey != null) {
            final Map<String, String> payload = ImmutableMap.of(ACS_URL, directResponseData.getIssuerURL(),
                    PAYLOAD, directResponseData.getIssuerPayload(),
                    TRANSACTION_ID, directResponseData.getTransactionId3DS());
            final String alg = worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getAlg();
            jwt = Jwts.builder()
                    .setHeaderParam(TYP, JWT)
                    .setHeaderParam(ALG, alg)
                    .setIssuedAt(Date.from(Instant.now()))
                    .setIssuer(worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getIss())
                    .claim(JTI, UUID.randomUUID())
                    .claim(ORG_UNIT_ID, worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getOrgUnitId())
                    .claim(RETURN_URL, worldpayUrlService.getFullThreeDSecureFlexFlowReturnUrl())
                    .claim(PAYLOAD, payload)
                    .claim(OBJECTIFY_PAYLOAD, true)
                    .signWith(signingKey, SignatureAlgorithm.forName(alg))
                    .compact();
        }

        LOG.info("Challenge JWT: [{}]", jwt);

        return jwt;
    }
}
