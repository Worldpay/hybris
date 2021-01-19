package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.facades.WorldpayCartFacade;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.order.impl.WorldpayCheckoutFacadeDecorator;
import com.worldpay.forms.PaymentDetailsForm;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessage;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.util.AddressDataUtil;
import de.hybris.platform.cms2.data.PagePreviewCriteriaData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.WORLDPAY_PAYMENT_AND_BILLING_CHECKOUT_STEP_CMS_PAGE_LABEL;
import static com.worldpay.controllers.pages.checkout.steps.WorldpayChoosePaymentMethodCheckoutStepController.*;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages.ERROR_MESSAGES_HOLDER;
import static java.util.Collections.singletonList;
import static java.util.Locale.UK;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayChoosePaymentMethodCheckoutStepControllerTest {

    private static final String TITLE = "title";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String LINE_1 = "line1";
    private static final String LINE_2 = "line2";
    private static final String TOWN = "town";
    private static final String POSTAL_CODE = "postalCode";
    private static final String COUNTRY_ISO_CODE = "countryIsoCode";
    private static final String REGION_ISO_CODE = "regionIsoCode";
    private static final String DECLINE_MESSAGE = "declineMessage";
    private static final String PAGE_TITLE = "pageTitle";
    private static final String ERROR_PAGE = "errorPage";
    private static final String CHECKOUT_TERMS_AND_CONDITIONS = "/checkout/multi/termsAndConditions";
    private static final String SELECTED_PAYMENT_METHOD_ID = "selectedPaymentMethodId";
    private static final String NEXT_PAGE = "nextPage";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String ISO_CODE_ADDRESS_FORM = "isoCodeAddressForm";
    private static final String EMAIL = "email";
    private static final String PAYMENT_METHOD_ID = "paymentMethodId";
    private static final String PHONE_NUMBER = "+44 (0) 123-123-123";
    private static final String PAYMENT_STATUS = "paymentStatus";
    private static final String SAVED_CARD_SELECTED_ATTRIBUTE = "savedCardSelected";

    @Spy
    @InjectMocks
    private WorldpayChoosePaymentMethodCheckoutStepController testObj;

    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private UserFacade userFacadeMock;
    @Mock
    private CheckoutFlowFacade checkoutFlowFacadeMock;
    @Mock
    private SiteConfigService siteConfigServiceMock;
    @Mock
    private CMSPageService cmsPageServiceMock;
    @Mock
    private CMSPreviewService cmsPreviewServiceMock;
    @Mock
    private WorldpayCartFacade worldpayCartFacadeMock;

    @Mock
    private HttpServletRequest httpServletRequestMock;
    @Mock
    private AddressForm addressFormMock;
    @Mock
    private I18NFacade i18NFacadeMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private AddressData addressDataMock;
    @Mock
    private RegionData regionDataMock;
    @Mock(answer = RETURNS_DEEP_STUBS, name = "checkoutFacade")
    private WorldpayCheckoutFacadeDecorator checkoutFacadeMock;
    @Mock
    private MessageSource themeSourceMock;
    @Mock
    private I18NService i18NServiceMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private Model modelMock;
    @Mock
    private RedirectAttributes redirectAttrsMock;
    @Mock
    private CartData cartDataMock;
    @Mock
    private ResourceBreadcrumbBuilder resourceBreadcrumbBuilderMock;
    @Mock
    private Breadcrumb breadCrumbMock;
    @Mock
    private CCPaymentInfoData ccPaymentInfoMock;
    @Mock
    private CheckoutStep checkoutStepMock;
    @Mock
    private PageTitleResolver pageTitleResolverMock;
    @Mock
    private ContentPageModel contentPageModelMock;
    @Mock
    private PageTemplateModel masterTemplateModelMock;
    @Mock
    private PaymentDetailsForm paymentDetailsFormMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private CheckoutCustomerStrategy checkoutCustomerStrategy;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private ObjectError objectErrorMock;
    @Mock
    private AddressDataUtil addressDataUtilMock;
    @Mock
    private PagePreviewCriteriaData pagePreviewCriteriaDataMock;
    @Mock
    private CartFacade cartFacadeMock;

    private List<RegionData> regionDataList;

    @Before
    public void setUp() throws CMSItemNotFoundException {
        regionDataList = singletonList(regionDataMock);
        doReturn(checkoutStepMock).when(testObj).getCheckoutStep();
        when(cmsPreviewServiceMock.getPagePreviewCriteria()).thenReturn(pagePreviewCriteriaDataMock);
        when(cmsPageServiceMock.getPageForLabelOrId(WORLDPAY_PAYMENT_AND_BILLING_CHECKOUT_STEP_CMS_PAGE_LABEL, pagePreviewCriteriaDataMock)).thenReturn(contentPageModelMock);
        when(pageTitleResolverMock.resolveContentPageTitle(anyString())).thenReturn(PAGE_TITLE);
        when(resourceBreadcrumbBuilderMock.getBreadcrumbs(anyString())).thenReturn(singletonList(breadCrumbMock));
        when(siteConfigServiceMock.getBoolean(anyString(), eq(true))).thenReturn(true);
        when(checkoutFlowFacadeMock.hasNoPaymentInfo()).thenReturn(false);
        when(cmsPageServiceMock.getFrontendTemplateName(masterTemplateModelMock)).thenReturn(ERROR_PAGE);
        when(userFacadeMock.getCCPaymentInfos(true)).thenReturn(singletonList(ccPaymentInfoMock));
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cartDataMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(i18NFacadeMock.getRegionsForCountryIso(ISO_CODE_ADDRESS_FORM)).thenReturn(regionDataList);
        when(addressFormMock.getCountryIso()).thenReturn(ISO_CODE_ADDRESS_FORM);
        when(checkoutCustomerStrategy.getCurrentUserForCheckout().getContactEmail()).thenReturn(EMAIL);
        when(paymentDetailsFormMock.getBillingAddress()).thenReturn(addressFormMock);
        when(addressDataUtilMock.convertToAddressData(addressFormMock)).thenReturn(addressDataMock);
    }

    @Test
    public void testPopulateAddressDataWithoutRegion() {

        when(addressDataUtilMock.convertToAddressData(addressFormMock)).thenReturn(addressDataMock);

        final AddressData result = testObj.populateAddressData(addressFormMock);

        assertThat(result).isEqualTo(addressDataMock);
    }

    @Test
    public void testPopulateAddressFormWithRegionDataNull() {
        when(addressDataMock.getRegion()).thenReturn(null);

        when(addressDataMock.getTitle()).thenReturn(TITLE);
        when(addressDataMock.getFirstName()).thenReturn(FIRST_NAME);
        when(addressDataMock.getLastName()).thenReturn(LAST_NAME);
        when(addressDataMock.getLine1()).thenReturn(LINE_1);
        when(addressDataMock.getLine2()).thenReturn(LINE_2);
        when(addressDataMock.getTown()).thenReturn(TOWN);
        when(addressDataMock.getPostalCode()).thenReturn(POSTAL_CODE);
        when(addressDataMock.getPhone()).thenReturn(PHONE_NUMBER);
        when(addressDataMock.getCountry().getIsocode()).thenReturn(COUNTRY_ISO_CODE);

        testObj.populateAddressForm(addressDataMock, addressFormMock);

        verify(addressFormMock).setTitleCode(TITLE);
        verify(addressFormMock).setFirstName(FIRST_NAME);
        verify(addressFormMock).setLastName(LAST_NAME);
        verify(addressFormMock).setLine1(LINE_1);
        verify(addressFormMock).setLine2(LINE_2);
        verify(addressFormMock).setTownCity(TOWN);
        verify(addressFormMock).setPostcode(POSTAL_CODE);
        verify(addressFormMock).setCountryIso(COUNTRY_ISO_CODE);
        verify(addressFormMock).setPhone(PHONE_NUMBER);
        verify(addressFormMock, never()).setRegionIso(anyString());
    }

    @Test
    public void testPopulateAddressFormWithRegionDataEmptyString() {
        when(addressDataMock.getRegion().getIsocodeShort()).thenReturn(StringUtils.EMPTY);

        when(addressDataMock.getTitle()).thenReturn(TITLE);
        when(addressDataMock.getFirstName()).thenReturn(FIRST_NAME);
        when(addressDataMock.getLastName()).thenReturn(LAST_NAME);
        when(addressDataMock.getLine1()).thenReturn(LINE_1);
        when(addressDataMock.getLine2()).thenReturn(LINE_2);
        when(addressDataMock.getTown()).thenReturn(TOWN);
        when(addressDataMock.getPostalCode()).thenReturn(POSTAL_CODE);
        when(addressDataMock.getPhone()).thenReturn(PHONE_NUMBER);
        when(addressDataMock.getCountry().getIsocode()).thenReturn(COUNTRY_ISO_CODE);

        testObj.populateAddressForm(addressDataMock, addressFormMock);

        verify(addressFormMock).setTitleCode(TITLE);
        verify(addressFormMock).setFirstName(FIRST_NAME);
        verify(addressFormMock).setLastName(LAST_NAME);
        verify(addressFormMock).setLine1(LINE_1);
        verify(addressFormMock).setLine2(LINE_2);
        verify(addressFormMock).setTownCity(TOWN);
        verify(addressFormMock).setPostcode(POSTAL_CODE);
        verify(addressFormMock).setCountryIso(COUNTRY_ISO_CODE);
        verify(addressFormMock).setPhone(PHONE_NUMBER);
        verify(addressFormMock, never()).setRegionIso(anyString());
    }

    @Test
    public void testPopulateAddressFormWithRegionData() {

        when(addressDataMock.getRegion()).thenReturn(regionDataMock);
        when(addressDataMock.getRegion().getIsocodeShort()).thenReturn(REGION_ISO_CODE);

        when(addressDataMock.getTitle()).thenReturn(TITLE);
        when(addressDataMock.getFirstName()).thenReturn(FIRST_NAME);
        when(addressDataMock.getLastName()).thenReturn(LAST_NAME);
        when(addressDataMock.getLine1()).thenReturn(LINE_1);
        when(addressDataMock.getLine2()).thenReturn(LINE_2);
        when(addressDataMock.getTown()).thenReturn(TOWN);
        when(addressDataMock.getPhone()).thenReturn(PHONE_NUMBER);
        when(addressDataMock.getPostalCode()).thenReturn(POSTAL_CODE);
        when(addressDataMock.getCountry().getIsocode()).thenReturn(COUNTRY_ISO_CODE);

        testObj.populateAddressForm(addressDataMock, addressFormMock);

        verify(addressFormMock).setTitleCode(TITLE);
        verify(addressFormMock).setFirstName(FIRST_NAME);
        verify(addressFormMock).setLastName(LAST_NAME);
        verify(addressFormMock).setLine1(LINE_1);
        verify(addressFormMock).setLine2(LINE_2);
        verify(addressFormMock).setTownCity(TOWN);
        verify(addressFormMock).setPostcode(POSTAL_CODE);
        verify(addressFormMock).setCountryIso(COUNTRY_ISO_CODE);
        verify(addressFormMock).setPhone(PHONE_NUMBER);
        verify(addressFormMock).setRegionIso(REGION_ISO_CODE);
    }

    @Test
    public void testGetDeclineMessageShouldReturnEmptyStringWhenDeclineCodeIsZERO() {
        when(checkoutFacadeMock.getCheckoutCart().getWorldpayDeclineCode()).thenReturn("0");

        final String result = testObj.getDeclineMessage();

        assertTrue(StringUtils.isEmpty(result));
    }

    @Test
    public void testGetDeclineMessageShouldReturnEmptyStringWhenDeclineCodeIsNull() {
        when(checkoutFacadeMock.getCheckoutCart().getWorldpayDeclineCode()).thenReturn(null);

        final String result = testObj.getDeclineMessage();

        assertTrue(StringUtils.isEmpty(result));
    }

    @Test
    public void testGetDeclineMessageShouldReturnDeclineMessageWhenDeclineCodeIsNotZERO() {
        when(checkoutFacadeMock.getCheckoutCart().getWorldpayDeclineCode()).thenReturn("1");
        when(i18NServiceMock.getCurrentLocale()).thenReturn(UK);
        when(themeSourceMock.getMessage(anyString(), eq(null), eq(UK))).thenReturn(DECLINE_MESSAGE);

        final String result = testObj.getDeclineMessage();

        assertEquals(DECLINE_MESSAGE, result);
    }

    @Test
    public void shouldReturnTheTermsAndConditionsUrl() {
        when(httpServletRequestMock.getContextPath()).thenReturn("");

        final String termsAndConditions = testObj.getTermsAndConditionsUrl(httpServletRequestMock);

        assertEquals(CHECKOUT_TERMS_AND_CONDITIONS, termsAndConditions);
    }

    @Test
    public void shouldSetupAndRedirectToChoosePaymentPage() throws CMSItemNotFoundException {
        when(modelMock.asMap().get(PAYMENT_STATUS_PARAMETER_NAME)).thenReturn(null);

        testObj.enterStep(modelMock, redirectAttrsMock);

        verify(testObj).setupAddPaymentPage(modelMock);
        verify(modelMock).addAttribute(eq(PAYMENT_DETAILS_FORM), any(PaymentDetailsForm.class));
        verify(modelMock, never()).addAttribute(eq(ERROR_MESSAGES_HOLDER), any());
    }

    @Test
    public void shouldAddErrorWhenThereIsAPaymentStatusInTheModel() throws CMSItemNotFoundException {
        when(modelMock.asMap().get(PAYMENT_STATUS_PARAMETER_NAME)).thenReturn(PAYMENT_STATUS);

        testObj.enterStep(modelMock, redirectAttrsMock);

        verify(modelMock).addAttribute(eq(ERROR_MESSAGES_HOLDER), any());
    }

    @Test
    public void doSelectPaymentMethod_WhenPaymentIdValidAndPopulated_shouldRedirectToNextStep() throws CMSItemNotFoundException {
        when(checkoutStepMock.nextStep()).thenReturn(NEXT_PAGE);

        final String result = testObj.doSelectPaymentMethod(SELECTED_PAYMENT_METHOD_ID, modelMock, redirectAttrsMock);

        verify(sessionServiceMock).setAttribute(SAVED_CARD_SELECTED_ATTRIBUTE, true);
        verify(checkoutFacadeMock).setPaymentDetails(SELECTED_PAYMENT_METHOD_ID);
        verify(worldpayCartFacadeMock).setBillingAddressFromPaymentInfo();
        assertEquals(NEXT_PAGE, result);
    }

    @Test
    public void doSelectPaymentMethod_WhenPaymentIdNotValid_shouldRedirectToNextStepAndNotSetPaymentMethod() throws CMSItemNotFoundException {
        when(checkoutStepMock.nextStep()).thenReturn(NEXT_PAGE);

        testObj.doSelectPaymentMethod("", modelMock, redirectAttrsMock);

        verify(checkoutFacadeMock, never()).setPaymentDetails(any());
        verify(testObj).enterStep(modelMock, redirectAttrsMock);
    }

    @Test
    public void shouldAddRegionsAndSetupPageOnError() throws CMSItemNotFoundException {
        testObj.handleFormErrors(modelMock, paymentDetailsFormMock);

        verify(modelMock).addAttribute(REGIONS, regionDataList);
        verify(testObj).setupAddPaymentPage(modelMock);
    }

    @Test
    public void shouldNotSaveDeliveryAddress() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(true);
        when(cartDataMock.getDeliveryAddress()).thenReturn(addressDataMock);

        testObj.handleAndSaveAddresses(paymentDetailsFormMock);

        verify(userFacadeMock, never()).addAddress(addressDataMock);
        verify(worldpayPaymentCheckoutFacadeMock).setBillingDetails(addressDataMock);
    }

    @Test
    public void shouldSaveBillingAddress() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(false);

        testObj.handleAndSaveAddresses(paymentDetailsFormMock);

        verify(userFacadeMock).addAddress(any(AddressData.class));
        verify(testObj).populateAddressData(addressFormMock);
    }

    @Test
    public void shouldSaveAddressForAnonymous() {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(false);
        when(userFacadeMock.isAnonymousUser()).thenReturn(true);

        testObj.handleAndSaveAddresses(paymentDetailsFormMock);

        verify(userFacadeMock).addAddress(any(AddressData.class));
        verify(testObj).populateAddressData(addressFormMock);
    }

    @Test
    public void shouldAddGlobalError() {
        when(bindingResultMock.hasGlobalErrors()).thenReturn(true);
        when(bindingResultMock.getGlobalErrors()).thenReturn(singletonList(objectErrorMock));

        final boolean result = testObj.addGlobalErrors(modelMock, bindingResultMock);

        verify(modelMock).addAttribute(eq(ERROR_MESSAGES_HOLDER), any(GlobalMessage.class));
        assertTrue(result);
    }

    @Test
    public void shouldAddError() {
        when(bindingResultMock.hasErrors()).thenReturn(true);

        final boolean result = testObj.addGlobalErrors(modelMock, bindingResultMock);

        verify(modelMock).addAttribute(eq(ERROR_MESSAGES_HOLDER), any(GlobalMessage.class));
        assertTrue(result);
    }

    @Test
    public void shouldRemovePaymentInfo() {

        testObj.remove(PAYMENT_METHOD_ID, redirectAttrsMock);

        verify(userFacadeMock).removeCCPaymentInfo(PAYMENT_METHOD_ID);
    }
}
