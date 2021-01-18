package com.worldpay.service.hop.impl;

import com.google.common.collect.ImmutableMap;
import com.worldpay.service.hop.WorldpayOrderCodeVerificationService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * Default implementation of {@link WorldpayOrderCodeVerificationService}
 */
public class DefaultWorldpayOrderCodeVerificationService implements WorldpayOrderCodeVerificationService {

    protected static final String AES = "AES";
    protected static final String ATTR_ORDER_CODE = "worldpayOrderCode";
    protected static final String CIPHER_TYPE = "AES/GCM/NoPadding";
    protected static final String SECRET_KEY_INSTANCE = "PBKDF2WithHmacSHA1";
    protected static final String SECRET_KEY = "worldpay.orderCode.encryption.key";

    private static final Logger LOG = LogManager.getLogger(DefaultWorldpayOrderCodeVerificationService.class);

    protected final GenericDao<AbstractOrderModel> abstractOrderGenericDao;
    protected final ConfigurationService configurationService;

    public DefaultWorldpayOrderCodeVerificationService(final GenericDao<AbstractOrderModel> abstractOrderGenericDao,
                                                       final ConfigurationService configurationService) {
        this.abstractOrderGenericDao = abstractOrderGenericDao;
        this.configurationService = configurationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEncryptedOrderCode(final String orderCode) throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), getParameterSpec());
        return Base64.getEncoder().encodeToString(cipher.doFinal(orderCode.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidEncryptedOrderCode(final String encryptedOrderCode) {
        final String orderCode = decrypt(encryptedOrderCode);
        if (Objects.isNull(orderCode)) {
            return false;
        }

        final List<AbstractOrderModel> results = abstractOrderGenericDao.find(ImmutableMap.of(
            ATTR_ORDER_CODE, orderCode
        ));

        return !results.isEmpty();
    }

    /**
     * Decrypts the given string
     *
     * @param encryptedText
     * @return the plain text
     */
    protected String decrypt(final String encryptedText) {
        try {
            final Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), getParameterSpec());
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
        } catch (final Exception e) {
            LOG.warn("Could not decrypt text. Reason:", e);
        }
        return null;
    }

    /**
     * Gets the secret key for encryption/decryption
     *
     * @return the secret key
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    protected SecretKeySpec getSecretKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        final String secretKeyString = configurationService.getConfiguration().getString(SECRET_KEY);
        final KeySpec spec = new PBEKeySpec(secretKeyString.toCharArray(), new byte[12], 65536, 128);
        final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(SECRET_KEY_INSTANCE);
        final byte[] key = secretKeyFactory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(key, AES);
    }

    private GCMParameterSpec getParameterSpec() {
        return new GCMParameterSpec(128, new byte[12]);
    }

}
