<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:if test="${not empty applePaySettings}">
    <c:set var="hasAvailablePaymentMethods" value="true" scope="session"/>
    <spring:theme var="paymentMethodName" code="worldpay.paymentMethod.applepay" />

    <form:radiobutton id="paymentMethod_applePay" path="paymentMethod" cssClass="available-true" value="APPLEPAY-SSL"/>
    <label class="available-true" for="paymentMethod_applePay" style="display: none;">
        <img src="${media.url}" title="${paymentMethodName}" alt="${paymentMethodName}" />
        <span>${paymentMethodName}</span>
    </label>
    <c:set var="deliveryAddress" value="${cartData.deliveryAddress}" />

    <script>
        window.applePaySettings = {
            merchantId: '${applePaySettings.merchantId}',
            paymentRequest: {
                merchantCapabilities: [
                    <c:forEach var="capability" items="${applePaySettings.merchantCapabilities}" varStatus="status">
                    '${capability}'<c:if test="${not status.last}">,</c:if>
                    </c:forEach>
                ],
                supportedNetworks: [
                    <c:forEach var="network" items="${applePaySettings.supportedNetworks}" varStatus="status">
                    '${network}'<c:if test="${not status.last}">,</c:if>
                    </c:forEach>
                ],
                countryCode: '${applePaySettings.countryCode}',
                currencyCode: '${cartData.totalPrice.currencyIso}',
                total: {
                    label: '${applePaySettings.merchantName}',
                    amount: '${cartData.totalPrice.value}',
                    type: 'final'
                },
                shippingContact: {
                    givenName: '${ycommerce:encodeJavaScript(deliveryAddress.firstName)}',
                    familyName: '${ycommerce:encodeJavaScript(deliveryAddress.lastName)}',
                    addressLines: [
                        '${ycommerce:encodeJavaScript(deliveryAddress.line1)}',
                        '${ycommerce:encodeJavaScript(deliveryAddress.line2)}'
                    ],
                    locality: '${ycommerce:encodeJavaScript(deliveryAddress.town)}',
                    postalCode: '${ycommerce:encodeJavaScript(deliveryAddress.postalCode)}',
                    administrativeArea: '${ycommerce:encodeJavaScript(deliveryAddress.region.name)}',
                    country: '${ycommerce:encodeJavaScript(deliveryAddress.country.name)}',
                    countryCode: '${ycommerce:encodeJavaScript(deliveryAddress.country.isocode)}',
                    emailAddress: '${ycommerce:encodeJavaScript(deliveryAddress.email)}',
                    phoneNumber: '${ycommerce:encodeJavaScript(deliveryAddress.phone)}'
                },
                requiredBillingContactFields: [
                    'postalAddress'
                ]
            }
        };
    </script>

</c:if>
