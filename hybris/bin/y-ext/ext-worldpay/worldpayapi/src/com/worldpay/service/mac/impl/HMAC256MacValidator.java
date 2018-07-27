package com.worldpay.service.mac.impl;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayMacValidationException;
import com.worldpay.service.mac.MacValidator;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static java.util.Arrays.asList;

/**
 * Implementation of the Mac validator supporting the HMAC256 algorithm.
 */
public class HMAC256MacValidator implements MacValidator {

    private static final String HMAC_SHA_256 = "HmacSHA256";

    /**
     * {@inheritDoc}
     * <p>
     * Implementation for HMAC256
     */
    @Override
    public boolean validateResponse(final String orderKey, final String worldpayMac, final String paymentAmount,
                                    final String paymentCurrency, final AuthorisedStatus paymentStatus, final String macSecret)
            throws WorldpayMacValidationException {
        if (StringUtils.isBlank(worldpayMac) || StringUtils.isBlank(macSecret)) {
            throw new WorldpayMacValidationException("No mac found in the response url provided by Worldpay");
        }
        final String hashString = StringUtils.join(asList(orderKey, paymentAmount, paymentCurrency, paymentStatus.name()), ":");
        try {
            Mac hmacSHA256 = Mac.getInstance(HMAC_SHA_256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(macSecret.getBytes(StandardCharsets.UTF_8.name()), HMAC_SHA_256);
            hmacSHA256.init(secretKeySpec);
            final byte[] hashValue = hmacSHA256.doFinal(hashString.getBytes(StandardCharsets.UTF_8.name()));

            return worldpayMac.equalsIgnoreCase(Hex.encodeHexString(hashValue));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            throw new WorldpayMacValidationException("Unable to validate mac as hash algorithm incorrectly specified", e);
        }
    }
}
