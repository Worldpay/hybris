package com.worldpay.service.mac.impl;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayMacValidationException;
import com.worldpay.service.mac.MacValidator;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Implementation of the Mac validator supporting the MD5 algorithm.
 */
public class MD5MacValidator implements MacValidator {

    /**
     * {@inheritDoc}
     * <p>
     * Implementation for MD5 Mac Validation
     */
    @Override
    public boolean validateResponse(final String orderKey, final String worldpayMac, final String paymentAmount,
                                    final String currency, final AuthorisedStatus status, final String macSecret)
            throws WorldpayMacValidationException {
        if (isBlank(worldpayMac)) {
            throw new WorldpayMacValidationException("No mac found in the response url provided by Worldpay");
        }
        try {
            final String hashString = StringUtils.join(asList(orderKey, paymentAmount, currency, status != null ? status.name() : null, macSecret), "");

            final MessageDigest digester = MessageDigest.getInstance("MD5");
            final byte[] digestedMac = digester.digest(hashString.getBytes(StandardCharsets.UTF_8.name()));

            return worldpayMac.equalsIgnoreCase(Hex.encodeHexString(digestedMac));
        } catch (final NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new WorldpayMacValidationException("Unable to validate mac as hash algorithm incorrectly specified", e);
        }
    }
}
