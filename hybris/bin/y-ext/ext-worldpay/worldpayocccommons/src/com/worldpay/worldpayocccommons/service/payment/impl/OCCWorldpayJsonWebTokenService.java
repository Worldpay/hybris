package com.worldpay.worldpayocccommons.service.payment.impl;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.payment.WorldpayJsonWebTokenService;
import com.worldpay.service.payment.impl.DefaultWorldpayJsonWebTokenService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        final SecretKey signingKey = retrieveSigningKey(worldpayMerchantConfigData);

        String jwt = null;
        if (Objects.nonNull(signingKey)) {
            final Map<String, String> payload = Map.of(ACS_URL, directResponseData.getIssuerURL(),
                PAYLOAD, directResponseData.getIssuerPayload(),
                TRANSACTION_ID, directResponseData.getTransactionId3DS());
            final String alg = worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getAlg();
            jwt = Jwts.builder()
                    .header().type(JWT).and()
                    .issuedAt(callSuperGetAt())
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

    protected Date callSuperGetAt() {
        return getIssuedAt();
    }

}
