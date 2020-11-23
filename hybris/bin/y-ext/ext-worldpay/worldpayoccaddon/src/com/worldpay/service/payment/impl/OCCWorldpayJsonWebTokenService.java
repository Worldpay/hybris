package com.worldpay.service.payment.impl;

import com.google.common.collect.ImmutableMap;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.payment.WorldpayJsonWebTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.Key;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class OCCWorldpayJsonWebTokenService extends DefaultWorldpayJsonWebTokenService implements WorldpayJsonWebTokenService {

    private static final Logger LOG = LogManager.getLogger(OCCWorldpayJsonWebTokenService.class);

    /**
     * Default constructor
     *
     * @param worldpayUrlService - the service to set
     */
    public OCCWorldpayJsonWebTokenService(final WorldpayUrlService worldpayUrlService) {
        super(worldpayUrlService);
    }

    @Override
    public String createJsonWebTokenFor3DSecureFlexChallengeIframe(final WorldpayMerchantConfigData worldpayMerchantConfigData, final DirectResponseData directResponseData) throws WorldpayConfigurationException {
        final Key signingKey = retrieveSigningKey(worldpayMerchantConfigData);

        String jwt = null;
        if (Objects.nonNull(signingKey)) {
            final Map<String, String> payload = ImmutableMap.of(ACS_URL, directResponseData.getIssuerURL(),
                PAYLOAD, directResponseData.getIssuerPayload(),
                TRANSACTION_ID, directResponseData.getTransactionId3DS());
            final String alg = worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getAlg();
            jwt = Jwts.builder()
                .setHeaderParam(TYP, JWT)
                .setHeaderParam(ALG, alg)
                .setIssuedAt(getIssuedAt())
                .setIssuer(worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getIss())
                .claim(Claims.ID, UUID.randomUUID())
                .claim(ORG_UNIT_ID, worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getOrgUnitId())
                .claim(RETURN_URL, worldpayUrlService.getFullThreeDSecureFlexFlowReturnUrl())
                .claim(PAYLOAD, payload)
                .claim(OBJECTIFY_PAYLOAD, true)
                .signWith(signingKey, SignatureAlgorithm.forName(alg))
                .compact();
        }

        LOG.debug("Challenge JWT: [{}]", jwt);

        return jwt;
    }
}
