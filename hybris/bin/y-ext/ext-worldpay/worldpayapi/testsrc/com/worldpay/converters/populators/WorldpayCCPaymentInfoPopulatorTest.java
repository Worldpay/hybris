package com.worldpay.converters.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayCCPaymentInfoPopulatorTest {

    private static final String BIN = "bin";
    @InjectMocks
    private WorldpayCCPaymentInfoPopulator testObj;

    @Mock
    private CreditCardPaymentInfoModel source;
    @Mock
    private CCPaymentInfoData target;


    @Test
    public void populate_ShouldPopulateBinAttribute() {
        when(source.getBin()).thenReturn(BIN);

        testObj.populate(source, target);

        verify(target).setBin(BIN);
    }
}
