package com.worldpay.service.payment.impl;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.worldpay.config.merchant.ThreeDSFlexJsonWebTokenCredentials;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.payment.WorldpayJsonWebTokenService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayJsonWebTokenService implements WorldpayJsonWebTokenService {

    private static final Logger LOG = LogManager.getLogger(DefaultWorldpayJsonWebTokenService.class);

    protected static final String ORG_UNIT_ID = "OrgUnitId";
    protected static final String JWT = "JWT";
    protected static final String RETURN_URL = "ReturnUrl";
    protected static final String OBJECTIFY_PAYLOAD = "ObjectifyPayload";
    protected static final String PAYLOAD = "Payload";
    protected static final String ACS_URL = "ACSUrl";
    protected static final String TRANSACTION_ID = "TransactionId";

    protected final WorldpayUrlService worldpayUrlService;

    /**
     * Default constructor
     *
     * @param worldpayUrlService - the service to set
     */
    public DefaultWorldpayJsonWebTokenService(final WorldpayUrlService worldpayUrlService) {
        this.worldpayUrlService = worldpayUrlService;
    }

    protected static SecretKey retrieveSigningKey(final WorldpayMerchantConfigData worldpayMerchantConfigData) {
        final ThreeDSFlexJsonWebTokenCredentials threeDSFlexJsonWebTokenSettings = worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings();
        if (threeDSFlexJsonWebTokenSettings != null) {
            return Keys.hmacShaKeyFor(threeDSFlexJsonWebTokenSettings.getJwtMacKey().getBytes());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createJsonWebTokenForDDC(final WorldpayMerchantConfigData worldpayMerchantConfigData) {
        final SecretKey signingKey = retrieveSigningKey(worldpayMerchantConfigData);

        String jwt = null;
        if (signingKey != null) {
            final String alg = worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getAlg();
            jwt = Jwts.builder()
                    .header().type(JWT).and()
                    .issuedAt(getIssuedAt())
                    .issuer(worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getIss())
                    .id(UUID.randomUUID().toString())
                    .claim(ORG_UNIT_ID, worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getOrgUnitId())
                    .signWith(signingKey, (MacAlgorithm) Jwts.SIG.get().get(alg))
                    .compact();
        }

        LOG.debug("DDC JWT: [{}]", jwt);

        return jwt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createJsonWebTokenFor3DSecureFlexChallengeIframe(final WorldpayMerchantConfigData worldpayMerchantConfigData, final DirectResponseData directResponseData) throws WorldpayConfigurationException {
        final SecretKey signingKey = retrieveSigningKey(worldpayMerchantConfigData);

        String jwt = null;
        if (signingKey != null) {
            final Map<String, String> payload = Map.of(ACS_URL, directResponseData.getIssuerURL(),
                    PAYLOAD, directResponseData.getIssuerPayload(),
                    TRANSACTION_ID, directResponseData.getTransactionId3DS());
            final String alg = worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getAlg();
            jwt = Jwts.builder()
                    .header().type(JWT).and()
                    .issuedAt(getIssuedAt())
                    .issuer(worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getIss())
                    .id(UUID.randomUUID().toString())
                    .claim(ORG_UNIT_ID, worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getOrgUnitId())
                    .claim(RETURN_URL, worldpayUrlService.getFullThreeDSecureFlexFlowReturnUrl())
                    .claim(PAYLOAD, payload)
                    .claim(OBJECTIFY_PAYLOAD, true)
                    .signWith(signingKey, (MacAlgorithm) Jwts.SIG.get().get(alg))
                    .compact();
        }

        LOG.debug("Challenge JWT: [{}]", jwt);

        return jwt;
    }

    protected Date getIssuedAt() {
        return Date.from(Instant.now());
    }
}
