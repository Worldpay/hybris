<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="hasAvailablePaymentMethods" value="${hasAvailablePaymentMethods or isAvailable}" scope="session"/>

<form:radiobutton id="paymentMethod_${apmConfiguration.code}" path="paymentMethod" disabled="${!isAvailable}"
                  value="${apmConfiguration.code}" cssClass="available-${isAvailable}" data-isBank="${apmConfiguration.bank}"/>
<label class="available-${isAvailable} col-xs-6 col-sm-6 col-md-4 col-lg-2" for="paymentMethod_${apmConfiguration.code}">
    <img src="${media.url}" title="${apmConfiguration.name}" alt="${apmConfiguration.name}"/>
    <span>${apmConfiguration.name}</span>
</label>
