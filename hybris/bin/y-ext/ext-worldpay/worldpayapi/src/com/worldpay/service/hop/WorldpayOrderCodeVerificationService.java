package com.worldpay.service.hop;

import java.security.GeneralSecurityException;

/**
 * Service that provides order code encryption and verification
 */
public interface WorldpayOrderCodeVerificationService {

    /**
     * Returns the encrypted order code
     *
     * @param order that will be processed in Limonetik
     * @return Hashed code of the order
     * @throws GeneralSecurityException When encrypting the order code is not possible
     */
    String getEncryptedOrderCode(String order) throws GeneralSecurityException;

    /**
     * Given an encrypted order code, determines if such order code is valid
     *
     * @param encryptedOrderCode Encrypted order code
     * @return true if the code is valid, false otherwise
     */
    boolean isValidEncryptedOrderCode(String encryptedOrderCode);

}
