package de.hybris.platform.hac.controller;

import com.worldpay.support.WorldpaySupportEmailService;
import com.worldpay.support.WorldpaySupportService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.mockito.Answers;
import org.mockito.Mock;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;


/**
 *
 */
@Controller
@RequestMapping("/worldpayhac/**")
public class WorldpayHACController
{
	protected static final String WORLDPAY_SUPPORT_EMAIL_CONFIG_KEY = "worldpay.support.email.address";

	@Resource
	private WorldpaySupportEmailService worldpaySupportEmailService;
	@Resource
	private WorldpaySupportService worldpaySupportService;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ConfigurationService configurationServiceMock;

	@RequestMapping(value = "/supportemail", method = RequestMethod.GET)
	public String supportEmail(final Model model)
	{
		model.addAttribute("body", worldpaySupportEmailService.createEmailBody());
		return "supportEmail";
	}

	@RequestMapping(value = "/sendemail", method = RequestMethod.GET)
	public String sendEmail(final RedirectAttributes attributes)
	{
		worldpaySupportService.sendSupportEmail();

		String message = "Worldpay support email was sent to: " + configurationServiceMock.getConfiguration().getString(WORLDPAY_SUPPORT_EMAIL_CONFIG_KEY);

		attributes.addFlashAttribute("send", true);
		attributes.addFlashAttribute("message", message);

		return "redirect:/worldpayhac/supportemail/";
	}
}
