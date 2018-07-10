<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:url value="/checkout/multi/termsAndConditions" var="getTermsAndConditionsUrl"/>

<div id="termsAndConditions" class="terms clear">
    <div class="checkbox">
        <label>
            <form:checkbox id="Terms1" path="termsCheck" />
            <spring:theme var="readTermsAndConditions" code="checkout.summary.placeOrder.readTermsAndConditions" arguments="${fn:escapeXml(getTermsAndConditionsUrl)}" htmlEscape="false"/>
            ${ycommerce:sanitizeHTML(readTermsAndConditions)}
        </label>
    </div>
</div>
