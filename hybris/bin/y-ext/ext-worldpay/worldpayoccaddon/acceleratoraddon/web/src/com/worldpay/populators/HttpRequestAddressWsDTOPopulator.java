package com.worldpay.populators;

import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.CountryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.RegionWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.util.YSanitizer;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * Populates {@link AddressWsDTO} instance based on http request parameters:<br>
 * <ul>
 * <li>id</li>
 * <li>titleCode</li>
 * <li>title</li>
 * <li>firstName</li>
 * <li>lastName</li>
 * <li>line1</li>
 * <li>line2</li>
 * <li>town</li>
 * <li>postalCode</li>
 * <li>country.isocode</li>
 * <li>region.isocode</li>
 * <li>shippingAddress</li>
 * <li>visibleInAddressBook</li>
 * <li>formattedAddress</li>
 * <li>email</li>
 * <li>phone</li>
 * </ul>
 * <p>
 * You can set a parameter prefix.. I.e 'billingAddress'. Then the populator would search parameters with the prefix,
 * i.e : 'billingAddress.firstName', etc..
 */
@Component("httpRequestAddressWsDTOPopulator")
@Scope("prototype")
public class HttpRequestAddressWsDTOPopulator extends AbstractHttpRequestWsDTOPopulator implements Populator<HttpServletRequest, AddressWsDTO> {

    protected static final String ADDRESS_ID = "id";
    protected static final String TITLE = "title";
    protected static final String TITLE_CODE = "titleCode";
    protected static final String FIRST_NAME = "firstName";
    protected static final String LAST_NAME = "lastName";
    protected static final String LINE1 = "line1";
    protected static final String LINE2 = "line2";
    protected static final String TOWN = "town";
    protected static final String POSTCODE = "postalCode";
    protected static final String COUNTRY = "country.isocode";
    protected static final String REGION = "region.isocode";
    protected static final String FORMATTED_ADDRESS = "formattedAddress";
    protected static final String EMAIL = "email";
    protected static final String PHONE = "phone";
    protected static final String COMPANY_NAME = "companyName";
    protected static final String VISIBLE_IN_ADDRESS_BOOK = "visibleInAddressBook";
    protected static final String SHIPPING_ADDRESS = "shippingAddress";

    private String addressPrefix;

    @Resource(name = "i18NFacade")
    private I18NFacade i18NFacade;

    @Override
    public void populate(final HttpServletRequest request, final AddressWsDTO addressData) {
        Assert.notNull(request, "Parameter request cannot be null.");
        Assert.notNull(addressData, "Parameter addressData cannot be null.");

        addressData.setId(updateStringValueFromRequest(request, ADDRESS_ID, addressData.getId()));
        addressData.setTitleCode(updateStringValueFromRequest(request, TITLE_CODE, addressData.getTitleCode()));
        addressData.setTitle(updateStringValueFromRequest(request, TITLE, addressData.getTitle()));
        addressData.setFirstName(updateStringValueFromRequest(request, FIRST_NAME, addressData.getFirstName()));
        addressData.setLastName(updateStringValueFromRequest(request, LAST_NAME, addressData.getLastName()));
        addressData.setLine1(updateStringValueFromRequest(request, LINE1, addressData.getLine1()));
        addressData.setLine2(updateStringValueFromRequest(request, LINE2, addressData.getLine2()));
        addressData.setTown(updateStringValueFromRequest(request, TOWN, addressData.getTown()));
        addressData.setPostalCode(updateStringValueFromRequest(request, POSTCODE, addressData.getPostalCode()));
        addressData.setCompanyName(updateStringValueFromRequest(request, COMPANY_NAME, addressData.getCompanyName()));
        addressData.setPhone(updateStringValueFromRequest(request, PHONE, addressData.getPhone()));
        addressData.setEmail(updateStringValueFromRequest(request, EMAIL, addressData.getEmail()));
        addressData.setFormattedAddress(updateStringValueFromRequest(request, FORMATTED_ADDRESS, addressData.getFormattedAddress()));

        addressData.setCountry(updateCountryFromRequest(request, addressData.getCountry()));
        addressData.setRegion(updateRegionFromRequest(request, addressData.getRegion()));
        addressData.setShippingAddress(updateBooleanValueFromRequest(request, SHIPPING_ADDRESS, addressData.getShippingAddress() != null ? addressData.getShippingAddress() : false));
        addressData.setVisibleInAddressBook(updateBooleanValueFromRequest(request, VISIBLE_IN_ADDRESS_BOOK, addressData.getVisibleInAddressBook() != null ? addressData.getVisibleInAddressBook() : false));
    }

    protected I18NFacade getI18NFacade() {
        return i18NFacade;
    }

    public void setAddressPrefix(final String addressPrefix) {
        this.addressPrefix = addressPrefix;
    }

    protected CountryWsDTO updateCountryFromRequest(final HttpServletRequest request, final CountryWsDTO defaultValue) {
        final String countryIsoCode = getRequestParameterValue(request, COUNTRY);
        if (StringUtils.isNotBlank(countryIsoCode)) {
            final CountryData countryDataFromFacade;
            try {
                countryDataFromFacade = getI18NFacade().getCountryForIsocode(countryIsoCode);
            } catch (final UnknownIdentifierException e) {
                throw new RequestParameterException("No country with the code " + YSanitizer.sanitize(countryIsoCode) + " found",
                        RequestParameterException.UNKNOWN_IDENTIFIER, COUNTRY, e);
            }
            if (countryDataFromFacade != null) {
                CountryWsDTO countryWsDTO = new CountryWsDTO();
                countryWsDTO.setIsocode(countryDataFromFacade.getIsocode());
                countryWsDTO.setName(countryDataFromFacade.getName());
                return countryWsDTO;
            } else {
                throw new RequestParameterException("No country with the code " + YSanitizer.sanitize(countryIsoCode) + " found",
                        RequestParameterException.UNKNOWN_IDENTIFIER, COUNTRY);
            }
        }
        return defaultValue;
    }

    protected RegionWsDTO updateRegionFromRequest(final HttpServletRequest request, final RegionWsDTO defaultValue) {
        final String countryIsoCode = getRequestParameterValue(request, COUNTRY);
        final String regionIsoCode = getRequestParameterValue(request, REGION);
        if (StringUtils.isNotBlank(countryIsoCode) && StringUtils.isNotBlank(regionIsoCode)) {
            final RegionData regionDataFromFacade;
            try {
                regionDataFromFacade = getI18NFacade().getRegion(countryIsoCode, regionIsoCode);
            } catch (final UnknownIdentifierException ex) {
                throw new RequestParameterException("No region with the code " + YSanitizer.sanitize(regionIsoCode) + " found.",
                        RequestParameterException.UNKNOWN_IDENTIFIER, REGION, ex);
            }
            if (regionDataFromFacade != null) {
                RegionWsDTO regionWsDTO = new RegionWsDTO();
                regionWsDTO.setIsocode(regionDataFromFacade.getIsocode());
                regionWsDTO.setName(regionDataFromFacade.getName());
                regionWsDTO.setCountryIso(regionDataFromFacade.getCountryIso());
                regionWsDTO.setIsocodeShort(regionDataFromFacade.getIsocodeShort());
                return regionWsDTO;
            } else {
                throw new RequestParameterException("No region with the code " + YSanitizer.sanitize(regionIsoCode) + " found.",
                        RequestParameterException.UNKNOWN_IDENTIFIER, REGION);
            }
        }
        return defaultValue;
    }

    @Override
    protected String getRequestParameterValue(final HttpServletRequest request, final String paramName) {
        if (addressPrefix == null) {
            return request.getParameter(paramName);
        }
        return request.getParameter(addressPrefix + '.' + paramName);
    }
}
