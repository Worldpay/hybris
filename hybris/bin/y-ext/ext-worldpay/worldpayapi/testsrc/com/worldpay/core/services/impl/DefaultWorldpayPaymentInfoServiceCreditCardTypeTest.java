package com.worldpay.core.services.impl;

import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.token.TokenReply;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collection;

import static com.worldpay.service.model.payment.PaymentType.*;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


@UnitTest
@RunWith(Parameterized.class)
public class DefaultWorldpayPaymentInfoServiceCreditCardTypeTest {

    private static final String WORLDPAY_CREDIT_CARD_MAPPINGS = "worldpay.creditCard.mappings.";

    @InjectMocks
    private DefaultWorldpayPaymentInfoService testObj;

    @Mock
    private PaymentReply paymentReplyMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Mock
    private EnumerationService enumerationServiceMock;
    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoModelMock;
    @Mock
    private ModelService modelServiceMock;


    @Parameter()
    public String paymentMethodCode;

    @Parameter(1)
    public String mappedValue;

    @Parameter(2)
    public CreditCardType creditCardType;


    @Before
    public void setUp() {
        initMocks(this);
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
        when(paymentReplyMock.getMethodCode()).thenReturn(paymentMethodCode);
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CREDIT_CARD_MAPPINGS + paymentMethodCode)).thenReturn(mappedValue);
        when(enumerationServiceMock.getEnumerationValue(CreditCardType.class.getSimpleName(), mappedValue)).thenReturn(creditCardType);

        testObj.setCreditCardType(creditCardPaymentInfoModelMock, paymentReplyMock);

        verify(creditCardPaymentInfoModelMock).setType(creditCardType);
        verify(modelServiceMock).save(creditCardPaymentInfoModelMock);
    }
}
