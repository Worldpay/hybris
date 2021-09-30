package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Session;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SessionPopulatorTest {

    private static final String SHOPPER_IP_ADDRESS = "shopperIPAddress";
    private static final String ID = "ID";

    @InjectMocks
    private SessionPopulator testObj;

    @Mock
    private Session sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.Session());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenGetShopperIPAddressIsNull_ShouldNotPopulateShopperIPAddress() {
        when(sourceMock.getShopperIPAddress()).thenReturn(null);

        final com.worldpay.internal.model.Session target = new com.worldpay.internal.model.Session();
        testObj.populate(sourceMock, target);

        assertThat(target.getShopperIPAddress()).isNull();
    }

    @Test
    public void populate_WhenGetIdIsNull_ShouldNotPopulateId() {
        when(sourceMock.getId()).thenReturn(null);

        final com.worldpay.internal.model.Session target = new com.worldpay.internal.model.Session();
        testObj.populate(sourceMock, target);

        assertThat(target.getId()).isNull();
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.getShopperIPAddress()).thenReturn(SHOPPER_IP_ADDRESS);
        when(sourceMock.getId()).thenReturn(ID);

        final com.worldpay.internal.model.Session target = new com.worldpay.internal.model.Session();
        testObj.populate(sourceMock, target);

        assertThat(target.getShopperIPAddress()).isEqualTo(SHOPPER_IP_ADDRESS);
        assertThat(target.getId()).isEqualTo(ID);
    }
}
