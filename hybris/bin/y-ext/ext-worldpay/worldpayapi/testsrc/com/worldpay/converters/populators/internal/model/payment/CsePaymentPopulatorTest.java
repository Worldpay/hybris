package com.worldpay.converters.populators.internal.model.payment;

import com.worldpay.internal.model.CSEDATA;
import com.worldpay.data.Address;
import com.worldpay.data.payment.Cse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CsePaymentPopulatorTest {

    private static final String ENCRYPTED_DATA = "encryptedData";

    @InjectMocks
    private CsePaymentPopulator testObj;

    @Mock
    private Converter<Address, com.worldpay.internal.model.Address> internalAddressConverterMock;

    @Mock
    private Cse sourceMock;
    @Mock
    private Address addressMock;
    @Mock
    private com.worldpay.internal.model.Address internalAddressMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new CSEDATA());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenAddressIsNull_ShouldNotPopulateCardAddress() {
        when(sourceMock.getAddress()).thenReturn(null);

        final CSEDATA targetMock = new CSEDATA();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getCardAddress()).isNull();
    }

    @Test
    public void populate_ShouldPopulateCsePayment() {
        when(sourceMock.getAddress()).thenReturn(addressMock);
        when(sourceMock.getEncryptedData()).thenReturn(ENCRYPTED_DATA);
        when(internalAddressConverterMock.convert(addressMock)).thenReturn(internalAddressMock);

        final CSEDATA targetMock = new CSEDATA();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getCardAddress().getAddress()).isEqualTo(internalAddressMock);
        assertThat(targetMock.getEncryptedData().getvalue()).isEqualTo(ENCRYPTED_DATA);
    }
}
