package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Date;
import com.worldpay.data.UserAccount;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UserAccountPopulatorTest {

    private static final String EMAIL = "email";
    private static final String NUMBER = "number";
    private static final String PHONE = "101010101";
    private static final String NAME = "name";

    @InjectMocks
    private UserAccountPopulator testObj;

    @Mock
    private Converter<Date, com.worldpay.internal.model.Date> internalDateConverterMock;

    @Mock
    private Date dateMock;
    @Mock
    private UserAccount sourceMock;
    @Mock
    private com.worldpay.internal.model.Date intDateMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_whenSourceIsNull_shouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.UserAccount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_whenTargetIsNull_shouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_whenSourceAndTargetAreNotNull_shouldPopulateUserAccount() {
        when(sourceMock.getUserAccountCreatedDate()).thenReturn(dateMock);
        when(sourceMock.getUserAccountEmailAddress()).thenReturn(EMAIL);
        when(sourceMock.getUserAccountNumber()).thenReturn(NUMBER);
        when(sourceMock.getUserAccountPhoneNumber()).thenReturn(PHONE);
        when(sourceMock.getUserAccountUserName()).thenReturn(NAME);

        when(internalDateConverterMock.convert(dateMock)).thenReturn(intDateMock);

        final com.worldpay.internal.model.UserAccount target = new com.worldpay.internal.model.UserAccount();
        testObj.populate(sourceMock, target);

        assertThat(target.getUserAccountCreatedDate().getDate()).isEqualTo(intDateMock);
        assertThat(target.getUserAccountEmailAddress()).isEqualTo(EMAIL);
        assertThat(target.getUserAccountNumber()).isEqualTo(NUMBER);
        assertThat(target.getUserAccountPhoneNumber()).isEqualTo(PHONE);
        assertThat(target.getUserAccountUserName()).isEqualTo(NAME);
    }

    @Test
    public void populate_whenDateIsNull_shouldNotPopulateDate() {
        when(sourceMock.getUserAccountCreatedDate()).thenReturn(null);

        final com.worldpay.internal.model.UserAccount target = new com.worldpay.internal.model.UserAccount();
        testObj.populate(sourceMock, target);

        assertThat(target.getUserAccountCreatedDate()).isNull();
    }
}
