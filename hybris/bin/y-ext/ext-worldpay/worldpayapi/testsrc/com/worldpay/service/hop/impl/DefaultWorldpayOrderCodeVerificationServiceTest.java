package com.worldpay.service.hop.impl;

import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class DefaultWorldpayOrderCodeVerificationServiceTest {

    private static final String AES = "AES";
    private static final String ORDER_CODE = "orderCode";
    private static final String ATTR_ORDER_CODE = "worldpayOrderCode";
    private static final String SECRET_KEY_VALUE = "secretKey";
    private static final String ENCRYPTED_ORDER_ID = "encryptedOrderId";

    @Spy
    @InjectMocks
    private DefaultWorldpayOrderCodeVerificationService testObj;

    @Mock
    private WorldpayMerchantStrategy worldpayMerchantStrategyMock;
    @Mock
    private WorldpayMerchantConfigurationModel worldpayMerchantConfigurationMock;
    @Mock
    private GenericDao<AbstractOrderModel> abstractOrderGenericDaoMock;

    @Mock
    private OrderModel orderMock;

    @Test
    void isValidEncryptedOrderId_whenErrorOccursWhileProcessingEncryption_shouldReturnFalseAndNotSaveOrder() throws GeneralSecurityException {
        doThrow(InvalidKeySpecException.class).when(testObj).getSecretKey();

        final boolean result = testObj.isValidEncryptedOrderCode(ENCRYPTED_ORDER_ID);

        assertThat(result).isFalse();
    }

    @Test
    void isValidEncryptedOrderId_whenOrderCodeExists_shouldReturnTrue() {
        doReturn(ORDER_CODE).when(testObj).decrypt(ENCRYPTED_ORDER_ID);
        when(abstractOrderGenericDaoMock.find(ImmutableMap.of(
                ATTR_ORDER_CODE, ORDER_CODE))).thenReturn(Collections.singletonList(orderMock));

        final boolean result = testObj.isValidEncryptedOrderCode(ENCRYPTED_ORDER_ID);

        assertThat(result).isTrue();
    }

    @Test
    void isValidEncryptedOrderId_whenOrderDoesNotExist_shouldReturnFalse() {
        doReturn(ORDER_CODE).when(testObj).decrypt(ENCRYPTED_ORDER_ID);

        final boolean result = testObj.isValidEncryptedOrderCode(ENCRYPTED_ORDER_ID);

        assertThat(result).isFalse();
    }

    @Test
    void getSecretSecretKey_WhenMerchantConfigurationHasOrderCodeEncryptionKey_ShouldReturnAESSecretKey() throws GeneralSecurityException {
        when(worldpayMerchantStrategyMock.getMerchant()).thenReturn(worldpayMerchantConfigurationMock);
        when(worldpayMerchantConfigurationMock.getOrderCodeEncryptionKey()).thenReturn(SECRET_KEY_VALUE);

        final SecretKeySpec result = testObj.getSecretKey();

        assertThat(result.getAlgorithm()).isEqualTo(AES);
    }

    @Test
    void getSecretKey_WhenOrderCodeEncryptionKeyIsMissing_ShouldThrowException() {
        when(worldpayMerchantStrategyMock.getMerchant()).thenReturn(worldpayMerchantConfigurationMock);
        when(worldpayMerchantConfigurationMock.getOrderCodeEncryptionKey()).thenReturn(null);

        assertThrows(InvalidKeySpecException.class, () -> testObj.getSecretKey());
    }

    @Test
    void getSecretKey_WhenOrderCodeEncryptionKeyIsBlank_ShouldThrowException() {
        when(worldpayMerchantStrategyMock.getMerchant()).thenReturn(worldpayMerchantConfigurationMock);
        when(worldpayMerchantConfigurationMock.getOrderCodeEncryptionKey()).thenReturn(SECRET_KEY_VALUE);
        when(worldpayMerchantConfigurationMock.getOrderCodeEncryptionKey()).thenReturn(" ");

        assertThrows(InvalidKeySpecException.class, () -> testObj.getSecretKey());
    }

    @Test
    void decrypt_WhenEncryptedTextWasCreatedWithMerchantKey_ShouldReturnPlainText() throws GeneralSecurityException {
        when(worldpayMerchantStrategyMock.getMerchant()).thenReturn(worldpayMerchantConfigurationMock);
        when(worldpayMerchantConfigurationMock.getOrderCodeEncryptionKey()).thenReturn(SECRET_KEY_VALUE);

        final String plainText = "testText";

        final String encryptedText = testObj.getEncryptedOrderCode(plainText);
        final String decryptedText = testObj.decrypt(encryptedText);

        assertThat(encryptedText).isNotEqualTo(plainText);
        assertThat(decryptedText).isEqualTo(plainText);
    }
}
