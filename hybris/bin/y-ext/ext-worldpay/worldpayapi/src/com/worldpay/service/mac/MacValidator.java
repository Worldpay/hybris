package com.worldpay.service.mac;

import com.worldpay.exception.WorldpayMacValidationException;
import com.worldpay.service.model.RedirectReference;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class to do the validation of a mac code.
 * <p/>
 * <p>Ensures that the redirect url being provided by Worldpay and that is returned in the {@link RedirectReference} has not been tampered with after being
 * sent out by Worldpay and before being received. The mac is a digest of the other information in the url and so can be validated by using the same
 * routine to generate a mac code and then validating against the one supplied by Worldpay</p>
 */
@SuppressWarnings("PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal")
public class MacValidator {

    private static final String HASHER_ALGORITHM_MD5 = "MD5";
    private static final char[] HEX_ARRAY = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static final MacValidator INSTANCE = new MacValidator();

    // private constructor to ensure the class is not instantiated.
    private MacValidator() {
    }

    /**
     * Public method to return the instance of the MacValidator. Ensures there is only one instance of this class
     *
     * @return the MacValidator instance
     */
    public static MacValidator getInstance() {
        return INSTANCE;
    }

    /**
     * Validate the mac code returned in the response url is valid for the rest of the details that have been returned.
     *
     * @param orderKey      OrderKey parameter returned in url
     * @param worldpayMac   Worldpay Mac code returned in url
     * @param paymentAmount Payment Amount returned in url
     * @param currency      Payment Currency returned in url
     * @param status        Payment Status returned in url
     * @param macSecret     Secret seed that has been agreed between the merchant and Worldpay
     * @return true or false depending whether the mac code is valid or not
     * @throws WorldpayMacValidationException if there has been an issue trying to validate the mac code
     */
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
