<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="showDeliveryAddress" required="true" type="java.lang.Boolean" %>
<%@ attribute name="showPaymentInfo" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showTax" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showTaxEstimate" required="false" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/responsive/checkout/multi" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<spring:url value="/checkout/multi/worldpay/summary/placeOrder" var="placeOrderUrl" htmlEscape="false"/>
<spring:url value="/checkout/multi/termsAndConditions" var="getTermsAndConditionsUrl" htmlEscape="false"/>

<div class="checkout-summary-headline hidden-xs">
    <spring:theme code="checkout.multi.order.summary" />
</div>
<div class="checkout-order-summary">
    <ycommerce:testId code="orderSummary">
        <multi-checkout:deliveryCartItems cartData="${cartData}" showDeliveryAddress="${showDeliveryAddress}"/>

        <c:forEach items="${cartData.pickupOrderGroups}" var="groupData" varStatus="status">
            <multi-checkout:pickupCartItems cartData="${cartData}" groupData="${groupData}" showHead="true"/>
        </c:forEach>

        <order:appliedVouchers order="${cartData}" />

        <multi-checkout:paymentInfo cartData="${cartData}" paymentInfo="${cartData.paymentInfo}" showPaymentInfo="${showPaymentInfo}"/>


        <multi-checkout:orderTotals cartData="${cartData}" showTaxEstimate="${showTaxEstimate}" showTax="${showTax}"/>
    </ycommerce:testId>
</div>

<div class="visible-xs clearfix">
    <form:form action="${placeOrderUrl}" id="placeOrderForm1" modelAttribute="placeOrderForm" class="place-order-form col-xs-12">
        <wp-multi-checkout:securityCode/>
        <wp-multi-checkout:termsAndConditions/>

        <button id="placeOrder" type="submit" class="btn btn-primary btn-place-order btn-block worldpayPlaceOrderWithSecurityCode">
            <spring:theme code="checkout.summary.placeOrder" />
        </button>
    </form:form>
</div>
