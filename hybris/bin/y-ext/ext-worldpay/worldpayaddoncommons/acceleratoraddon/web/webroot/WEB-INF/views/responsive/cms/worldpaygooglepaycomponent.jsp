<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty googlePaySettings}">
    <c:set var="hasAvailablePaymentMethods" value="true" scope="session"/>
    <spring:theme var="paymentMethodName" code="worldpay.paymentMethod.googlepay" />

    <form:radiobutton id="paymentMethod_googlePay" path="paymentMethod" cssClass="available-true" value="PAYWITHGOOGLE-SSL"/>
    <label class="available-true" for="paymentMethod_googlePay" style="display: none;">
        <img src="${media.url}" title="${paymentMethodName}" alt="${paymentMethodName}" />
        <span>${paymentMethodName}</span>
    </label>

    <script>
        window.googlePaySettings = {
            baseCardPaymentMethod: {
                type: '${googlePaySettings.cardType}',
                parameters: {
                    allowedAuthMethods: [
                        <c:forEach var="authMethod" items="${googlePaySettings.allowedAuthMethods}" varStatus="status">
                        '${authMethod}'<c:if test="${not status.last}">,</c:if>
                        </c:forEach>
                    ],
                    allowedCardNetworks: [
                        <c:forEach var="cardNetwork" items="${googlePaySettings.allowedCardNetworks}" varStatus="status" >
                        '${cardNetwork}'<c:if test="${not status.last}">,</c:if>
                        </c:forEach>
                    ],
                    billingAddressRequired: true,
                    billingAddressParameters: {
                        format: 'FULL'
                    }
                }
            },
            clientSettings: {
                environment: '${googlePaySettings.environment}'
            },
            gateway: 'worldpay',
            gatewayMerchantId: '${googlePaySettings.gatewayMerchantId}',
            merchantName: '${googlePaySettings.merchantName}',
            merchantId: '${googlePaySettings.merchantId}',
            transactionInfo: {
                currencyCode: '${cartData.totalPrice.currencyIso}',
                totalPrice: '${cartData.totalPrice.value}',
                totalPriceStatus: 'FINAL'
            }
        };
    </script>

</c:if>
