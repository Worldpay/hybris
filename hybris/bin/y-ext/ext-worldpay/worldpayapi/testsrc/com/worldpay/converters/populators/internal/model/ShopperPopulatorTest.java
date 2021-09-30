package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Browser;
import com.worldpay.data.Session;
import com.worldpay.data.Shopper;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ShopperPopulatorTest {

    private static final String EMAIL = "email";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperID";

    @InjectMocks
    private ShopperPopulator testObj;

    @Mock
    private Converter<Browser, com.worldpay.internal.model.Browser> internalBrowserConverterMock;
    @Mock
    private Converter<Session, com.worldpay.internal.model.Session> internalSessionConverterMock;

    @Mock
    private Shopper sourceMock;
    @Mock
    private Browser browserMock;
    @Mock
    private com.worldpay.internal.model.Browser intBrowserMock;
    @Mock
    private Session sessionMock;
    @Mock
    private com.worldpay.internal.model.Session intSessionMock;

    @Before
    public void setUp() {
        testObj = new ShopperPopulator(internalBrowserConverterMock, internalSessionConverterMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.Shopper());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenGetShopperEmailAddressIsNull_ShouldNotPopulateShopperEmailAddress() {
        when(sourceMock.getShopperEmailAddress()).thenReturn(null);

        final com.worldpay.internal.model.Shopper target = new com.worldpay.internal.model.Shopper();
        testObj.populate(sourceMock, target);

        assertThat(target.getShopperEmailAddress()).isNull();
    }

    @Test
    public void populate_WhenGetAuthenticatedShopperIDIsNull_ShouldNotPopulateAuthenticatedShopperID() {
        when(sourceMock.getAuthenticatedShopperID()).thenReturn(null);

        final com.worldpay.internal.model.Shopper target = new com.worldpay.internal.model.Shopper();
        testObj.populate(sourceMock, target);

        assertThat(target.getAuthenticatedShopperID()).isNull();
    }

    @Test
    public void populate_WhenGetBrowserIsNull_ShouldNotPopulateBrowser() {
        when(sourceMock.getBrowser()).thenReturn(null);

        final com.worldpay.internal.model.Shopper target = new com.worldpay.internal.model.Shopper();
        testObj.populate(sourceMock, target);

        assertThat(target.getBrowser()).isNull();
    }

    @Test
    public void populate_WhenGetSessionIsNull_ShouldNotPopulateSession() {
        when(sourceMock.getSession()).thenReturn(null);

        final com.worldpay.internal.model.Shopper target = new com.worldpay.internal.model.Shopper();
        testObj.populate(sourceMock, target);

        assertThat(target.getSession()).isNull();
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.getShopperEmailAddress()).thenReturn(EMAIL);
        when(sourceMock.getAuthenticatedShopperID()).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(sourceMock.getBrowser()).thenReturn(browserMock);
        when(internalBrowserConverterMock.convert(browserMock)).thenReturn(intBrowserMock);
        when(sourceMock.getSession()).thenReturn(sessionMock);
        when(internalSessionConverterMock.convert(sessionMock)).thenReturn(intSessionMock);

        final com.worldpay.internal.model.Shopper target = new com.worldpay.internal.model.Shopper();
        testObj.populate(sourceMock, target);

        assertThat(target.getShopperEmailAddress()).isEqualTo(EMAIL);
        assertThat(target.getAuthenticatedShopperID().getvalue()).isEqualTo(AUTHENTICATED_SHOPPER_ID);
        assertThat(target.getBrowser()).isEqualTo(intBrowserMock);
        assertThat(target.getSession()).isEqualTo(intSessionMock);
    }
}
