package com.worldpay.worldpayocccommons.service.payment.impl;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.payment.WorldpayJsonWebTokenService;
import com.worldpay.service.payment.impl.DefaultWorldpayJsonWebTokenService;
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
            try {
                final Map<String, String> payload = Map.of(ACS_URL, directResponseData.getIssuerURL(),
                        PAYLOAD, directResponseData.getIssuerPayload(),
                        TRANSACTION_ID, directResponseData.getTransactionId3DS());
                final String alg = worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getAlg();
                final JWSAlgorithm jwsAlgorithm = JWSAlgorithm.parse(alg);
                final JWSHeader header = new JWSHeader.Builder(jwsAlgorithm).type(new JOSEObjectType(JWT)).build();
                final JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                        .issueTime(callSuperGetIssuedAt())
                        .issuer(worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getIss())
                        .jwtID(UUID.randomUUID().toString())
                        .claim(ORG_UNIT_ID, worldpayMerchantConfigData.getThreeDSFlexJsonWebTokenSettings().getOrgUnitId())
                        .claim(RETURN_URL, worldpayUrlService.getFullThreeDSecureFlexFlowReturnUrl())
                        .claim(PAYLOAD, payload)
                        .claim(OBJECTIFY_PAYLOAD, true)
                        .build();
                final SignedJWT signedJWT = new SignedJWT(header, claimsSet);
                signedJWT.sign(new MACSigner(signingKey));
                jwt = signedJWT.serialize();
            } catch (final Exception e) {
                LOG.error("Error creating Challenge JWT", e);
            }
        }

        LOG.debug("Challenge JWT: [{}]", jwt);

        return jwt;
    }

    protected Date callSuperGetIssuedAt() {
        return super.getIssuedAt();
    }
}
