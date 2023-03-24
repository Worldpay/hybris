package com.worldpay.core.services.impl;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.data.PaymentReply;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.AddressService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.Collection;

import static com.worldpay.service.model.payment.PaymentType.*;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@UnitTest
@RunWith(Parameterized.class)
public class DefaultWorldpayPaymentInfoServiceCreditCardTypeTest {

    private static final String WORLDPAY_CREDIT_CARD_MAPPINGS = "worldpay.creditCard.mappings.";

    @InjectMocks
    private DefaultWorldpayPaymentInfoService testObj;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ModelService modelServiceMock;
    @Mock
    private EnumerationService enumerationServiceMock;
    @Mock
    private APMConfigurationLookupService apmConfigurationLookupServiceMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Mock
    private AddressService addressServiceMock;
    @Mock
    private CommerceCheckoutService commerceCheckoutServiceMock;
    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private WorldpayServicesWrapper worldpayServicesWrapperMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;

    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoModelMock;

    @Parameter()
    public String paymentMethodCode;

    @Parameter(1)
    public String mappedValue;

    @Parameter(2)
    public CreditCardType creditCardType;


    @Before
    public void setUp() {
        worldpayServicesWrapperMock = new WorldpayServicesWrapper(modelServiceMock, sessionServiceMock, enumerationServiceMock, configurationServiceMock);
        testObj = new DefaultWorldpayPaymentInfoService(apmConfigurationLookupServiceMock, worldpayMerchantInfoServiceMock, commerceCheckoutServiceMock, addressServiceMock, worldpayServicesWrapperMock);
    }

    @Parameters(name = "{index}: ({0}, {1}) => Expected:{2}")
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[]{UATP.getMethodCode(), "uatp", CreditCardType.UATP},
                new Object[]{AMERICAN_EXPRESS.getMethodCode(), "amex", CreditCardType.AMEX},
                new Object[]{JCB.getMethodCode(), "jcb", CreditCardType.JCB},
                new Object[]{MAESTRO.getMethodCode(), "maestro", CreditCardType.MAESTRO},
                new Object[]{SWITCH.getMethodCode(), "switch", CreditCardType.SWITCH},
                new Object[]{VISA.getMethodCode(), "visa", CreditCardType.VISA},
                new Object[]{GE_CAPITAL.getMethodCode(), "ge_capital", CreditCardType.GE_CAPITAL},
                new Object[]{MASTERCARD.getMethodCode(), "master", CreditCardType.MASTER},
                new Object[]{MASTERCARD.getMethodCode(), "mastercard_eurocard", CreditCardType.MASTERCARD_EUROCARD},
                new Object[]{DINERS.getMethodCode(), "diners", CreditCardType.DINERS},
                new Object[]{DISCOVER.getMethodCode(), "discover", CreditCardType.DISCOVER},
                new Object[]{DANKORT.getMethodCode(), "dankort", CreditCardType.DANKORT},
                new Object[]{CARTE_BLEUE.getMethodCode(), "cartebleue", CreditCardType.CARTEBLEUE},
                new Object[]{CARTE_BANCAIRE.getMethodCode(), "cb", CreditCardType.CB},
                new Object[]{AURORE.getMethodCode(), "aurore", CreditCardType.AURORE},
                new Object[]{AIRPLUS.getMethodCode(), "airplus", CreditCardType.AIRPLUS},
                new Object[]{CARD_SSL.getMethodCode(), "card", CreditCardType.CARD}
        );
    }

    @Test
    public void shouldSetCorrectPaymentType() {
        when(paymentReplyMock.getPaymentMethodCode()).thenReturn(paymentMethodCode);
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CREDIT_CARD_MAPPINGS + paymentMethodCode)).thenReturn(mappedValue);
        when(enumerationServiceMock.getEnumerationValue(CreditCardType.class.getSimpleName(), mappedValue)).thenReturn(creditCardType);

        testObj.setCreditCardType(creditCardPaymentInfoModelMock, paymentReplyMock);

        verify(creditCardPaymentInfoModelMock).setType(creditCardType);
        verify(modelServiceMock).save(creditCardPaymentInfoModelMock);
    }
}
