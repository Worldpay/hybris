package com.worldpay.service.interaction.impl;

import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import de.hybris.platform.assistedserviceservices.utils.AssistedServiceSession;
import de.hybris.platform.core.model.user.UserModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayDynamicInteractionResolverServiceTest {

    @InjectMocks
    private DefaultWorldpayDynamicInteractionResolverService testObj;

    @Mock
    private AssistedServiceService assistedServiceServiceMock;
    @Mock
    private AssistedServiceSession assistedServiceSessionMock;
    @Mock
    private UserModel userModelMock;

    @Test
    public void resolveIterationTypeForEcommerce() {
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = new WorldpayAdditionalInfoData();
        final DynamicInteractionType result = testObj.resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoData);

        assertThat(result).isEqualTo(DynamicInteractionType.ECOMMERCE);
    }

    @Test
    public void resolveIterationTypeForMOTO() {
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = new WorldpayAdditionalInfoData();
        when(assistedServiceServiceMock.getAsmSession()).thenReturn(assistedServiceSessionMock);
        when(assistedServiceSessionMock.getAgent()).thenReturn(userModelMock);

        final DynamicInteractionType result = testObj.resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoData);

        assertThat(result).isEqualTo(DynamicInteractionType.MOTO);
    }

    @Test
    public void resolveIterationTypeForReplenishment() {
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = new WorldpayAdditionalInfoData();
        worldpayAdditionalInfoData.setReplenishmentOrder(true);
        final DynamicInteractionType result = testObj.resolveInteractionTypeForDirectIntegration(worldpayAdditionalInfoData);

        assertThat(result).isEqualTo(DynamicInteractionType.CONT_AUTH);
    }

}
