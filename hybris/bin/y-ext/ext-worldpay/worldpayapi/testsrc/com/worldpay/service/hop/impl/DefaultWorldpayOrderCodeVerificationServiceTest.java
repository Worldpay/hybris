package com.worldpay.service.hop.impl;

import com.google.common.collect.ImmutableMap;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayOrderCodeVerificationServiceTest {

    private static final String AES = "AES";
    private static final String ORDER_CODE = "orderCode";
    private static final String ATTR_ORDER_CODE = "worldpayOrderCode";
    private static final String SECRET_KEY_VALUE = "secretKey";
    private static final String ENCRYPTED_ORDER_ID = "encryptedOrderId";
    private static final String SECRET_KEY = "worldpay.orderCode.encryption.key";

    @Spy
    @InjectMocks
    private DefaultWorldpayOrderCodeVerificationService testObj;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Mock
    private GenericDao<AbstractOrderModel> abstractOrderGenericDaoMock;

    @Mock
    private OrderModel orderMock;

    @Before
    public void setUp() {
        when(configurationServiceMock.getConfiguration().getString(SECRET_KEY)).thenReturn(SECRET_KEY_VALUE);
    }

    @Test
    public void isValidEncryptedOrderId_whenErrorOccursWhileProcessingEncryption_shouldReturnFalseAndNotSaveOrder() throws GeneralSecurityException {
        doThrow(Exception.class).when(testObj).getSecretKey();

        final boolean result = testObj.isValidEncryptedOrderCode(ENCRYPTED_ORDER_ID);

        assertThat(result).isFalse();
    }

    @Test
    public void isValidEncryptedOrderId_whenOrderCodeExists_shouldReturnTrue() {
        doReturn(ORDER_CODE).when(testObj).decrypt(ENCRYPTED_ORDER_ID);
        when(abstractOrderGenericDaoMock.find(ImmutableMap.of(
            ATTR_ORDER_CODE, ORDER_CODE))).thenReturn(Collections.singletonList(orderMock));

        final boolean result = testObj.isValidEncryptedOrderCode(ENCRYPTED_ORDER_ID);

        assertThat(result).isTrue();
    }

    @Test
    public void isValidEncryptedOrderId_whenOrderDoesNotExist_shouldReturnFalse() {
        doReturn(ORDER_CODE).when(testObj).decrypt(ENCRYPTED_ORDER_ID);

        final boolean result = testObj.isValidEncryptedOrderCode(ENCRYPTED_ORDER_ID);

        assertThat(result).isFalse();
    }

    @Test
    public void getSecretKey_shouldReturnAESSecretKey() throws GeneralSecurityException {
        final SecretKeySpec result = testObj.getSecretKey();

        assertThat(result.getAlgorithm()).isEqualTo(AES);
    }

    @Test
    public void ensureDecryptionIsPossible() throws GeneralSecurityException {
        final String plainText = "testText";

        final String encryptedText = testObj.getEncryptedOrderCode(plainText);
        final String decryptedText = testObj.decrypt(encryptedText);

        assertThat(encryptedText).isNotEqualTo(plainText);
        assertThat(decryptedText).isEqualTo(plainText);
    }
}
