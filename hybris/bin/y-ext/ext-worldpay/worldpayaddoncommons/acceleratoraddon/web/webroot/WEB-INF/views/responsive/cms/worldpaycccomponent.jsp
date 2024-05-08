<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="hasAvailablePaymentMethods" value="true" scope="session"/>
<spring:theme var="paymentMethodName" code="worldpay.paymentMethod.creditcard" />

<form:radiobutton id="paymentMethod_CC" path="paymentMethod" cssClass="available-true" value="ONLINE"/>
<label class="available-true col-xs-6 col-sm-6 col-md-4 col-lg-2" for="paymentMethod_CC">
    <img src="${media.url}" title="${paymentMethodName}" alt="${paymentMethodName}" />
    <span>${paymentMethodName}</span>
</label>