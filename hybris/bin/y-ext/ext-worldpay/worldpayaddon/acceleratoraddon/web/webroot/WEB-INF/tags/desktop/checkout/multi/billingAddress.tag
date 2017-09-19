<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="wp-address" tagdir="/WEB-INF/tags/addons/worldpayaddon/desktop/address" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:if test="${cartData.deliveryItemsQuantity > 0}">
    <form:checkbox id="wpUseDeliveryAddress" path="useDeliveryAddress"
                   data-firstname="${fn:escapeXml(deliveryAddress.firstName)}"
                   data-lastname="${fn:escapeXml(deliveryAddress.lastName)}"
                   data-line1="${fn:escapeXml(deliveryAddress.line1)}"
                   data-line2="${fn:escapeXml(deliveryAddress.line2)}"
                   data-town="${fn:escapeXml(deliveryAddress.town)}"
                   data-postalcode="${fn:escapeXml(deliveryAddress.postalCode)}"
                   data-countryisocode="${fn:escapeXml(deliveryAddress.country.isocode)}"
                   data-regionisocode="${fn:escapeXml(deliveryAddress.region.isocode)}"
                   data-address-id="${fn:escapeXml(deliveryAddress.id)}"
                   data-phone="${fn:escapeXml(deliveryAddress.phone)}"
                   tabindex="9"/>

    <label for="wpUseDeliveryAddress"><spring:theme code="checkout.multi.sop.useMyDeliveryAddress"/></label>
</c:if>

<wp-address:billAddressFormSelector supportedCountries="${countries}" regions="${regions}" tabindex="10"/>

