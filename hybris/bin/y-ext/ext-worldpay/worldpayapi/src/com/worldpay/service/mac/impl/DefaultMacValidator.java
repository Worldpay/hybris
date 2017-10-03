package com.worldpay.service.mac.impl;

import com.worldpay.exception.WorldpayMacValidationException;
import com.worldpay.service.mac.MacValidator;
import com.worldpay.service.model.RedirectReference;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * {@inheritDoc}
 */
public class DefaultMacValidator implements MacValidator {

    private static final String HASHER_ALGORITHM_MD5 = "MD5";
    private static final char[] HEX_ARRAY = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateResponse(String orderKey, String worldpayMac, String paymentAmount, String currency, String status, String macSecret) throws WorldpayMacValidationException {
        try {
            if (worldpayMac == null || worldpayMac.length() == 0) {
                throw new WorldpayMacValidationException("No mac found in the response url provided by Worldpay");
            }
            String hashString = buildHashString(macSecret, orderKey, paymentAmount, currency, status);

            MessageDigest digest = MessageDigest.getInstance(HASHER_ALGORITHM_MD5);
            byte[] digestedMac = digest.digest(hashString.getBytes("UTF-8"));
            String macHex = convertToHex(digestedMac);

            return worldpayMac.equalsIgnoreCase(macHex);
        } catch (NoSuchAlgorithmException|UnsupportedEncodingException e) {
            throw new WorldpayMacValidationException("Unable to validate mac as hash algorithm incorrectly specified", e);
        }
            }

    private String buildHashString(String macSecret, String orderKey, String paymentAmount, String currency, String status) {
        return orderKey + paymentAmount + currency + status + macSecret;
    }

    private String convertToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
