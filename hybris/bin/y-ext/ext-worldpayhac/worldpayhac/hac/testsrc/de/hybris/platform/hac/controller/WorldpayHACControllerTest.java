package de.hybris.platform.hac.controller;

import com.worldpay.support.WorldpaySupportEmailService;
import com.worldpay.support.WorldpaySupportService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayHACControllerTest {
    private static final String BODY_CONTENT = "bodyContent";
    private static final String BODY = "body";
    private static final String SEND = "send";
    private static final String MESSAGE = "message";
    private static final String WORLDPAY_SUPPORT_EMAIL_CONFIG_KEY = "worldpay.support.email.address";
    private static final String CONFIG_KEY = "configKey";
    private static final String WORLDPAY_SUPPORT_EMAIL_WAS_SENT_TO = "Worldpay support email was sent to: ";

    @InjectMocks
    private WorldpayHACController testObj;

    @Mock
    private WorldpaySupportEmailService worldpaySupportEmailServiceMock;
    @Mock
    private WorldpaySupportService worldpaySupportServiceMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;

    @Mock
    private Model modelMock;
    @Mock
    private RedirectAttributes redirectAttributesMock;

    @Test
    public void supportEmailShouldCallCreateEmailBody() throws Exception {
        Mockito.when(worldpaySupportEmailServiceMock.createEmailBody()).thenReturn(BODY_CONTENT);

        testObj.supportEmail(modelMock);

        Mockito.verify(worldpaySupportEmailServiceMock).createEmailBody();
        Mockito.verify(modelMock).addAttribute(BODY, BODY_CONTENT);
    }

    @Test
    public void sendEmailShouldCallSendSupportEmail() throws Exception {

        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_SUPPORT_EMAIL_CONFIG_KEY)).thenReturn(CONFIG_KEY);
        testObj.sendEmail(redirectAttributesMock);

        Mockito.verify(worldpaySupportServiceMock).sendSupportEmail();
        Mockito.verify(redirectAttributesMock).addFlashAttribute(SEND, true);
        Mockito.verify(redirectAttributesMock).addFlashAttribute(MESSAGE, WORLDPAY_SUPPORT_EMAIL_WAS_SENT_TO + CONFIG_KEY);
    }

}
