<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="showDeliveryAddress" required="true" type="java.lang.Boolean" %>
<%@ attribute name="showPaymentInfo" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showTax" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showTaxEstimate" required="false" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi" %>
<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayb2baddon/responsive/checkout/multi" %>
<%@ taglib prefix="b2b-multi-checkout" tagdir="/WEB-INF/tags/addons/b2bacceleratoraddon/responsive/checkout/multi" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>

<spring:url value="/checkout/multi/worldpay/summary/placeOrder" var="placeOrderUrl"/>
<spring:url value="/checkout/multi/termsAndConditions" var="getTermsAndConditionsUrl"/>


<div class="checkout-order-summary checkout-review-xs">
    <ycommerce:testId code="orderSummary">
        <div class="checkout-summary-headline hidden-xs">
            <spring:theme code="checkout.multi.order.summary"/>
        </div>

        <multi-checkout:deliveryCartItems cartData="${cartData}" showDeliveryAddress="${showDeliveryAddress}" />

        <c:forEach items="${cartData.pickupOrderGroups}" var="groupData" varStatus="status">
            <multi-checkout:pickupCartItems cartData="${cartData}" groupData="${groupData}" showHead="true" />
        </c:forEach>

        <order:appliedVouchers order="${cartData}" />

        <c:if test="${cartData.paymentType.code eq 'CARD'}">
            <multi-checkout:paymentInfo cartData="${cartData}" paymentInfo="${cartData.paymentInfo}" showPaymentInfo="${showPaymentInfo}" />
        </c:if>
        <c:if test="${cartData.paymentType.code eq 'ACCOUNT'}">
            <b2b-multi-checkout:accountPaymentInfo cartData="${cartData}" />
        </c:if>

        <multi-checkout:orderTotals cartData="${cartData}" showTaxEstimate="${showTaxEstimate}" showTax="${showTax}" />
    </ycommerce:testId>
</div>

<div class="place-order-form visible-xs">
    <form:form action="${placeOrderUrl}" id="placeOrderForm1" modelAttribute="placeOrderForm">
        <wp-multi-checkout:securityCode/>
        <div class="checkbox">
            <label> <form:checkbox id="Terms1" path="termsCheck" />
                <spring:theme code="checkout.summary.placeOrder.readTermsAndConditions" arguments="${getTermsAndConditionsUrl}"/>
            </label>
        </div>

        <button id="placeOrder" type="submit" class="btn btn-primary btn-block btn-place-order btn-block btn-lg checkoutSummaryButton" disabled="disabled">
            <spring:theme code="checkout.summary.placeOrder"/>
        </button>
        <button id="scheduleReplenishment" type="button" class="btn btn-default btn-block scheduleReplenishmentButton checkoutSummaryButton" disabled="disabled">
            <spring:theme code="checkout.summary.scheduleReplenishment"/>
        </button>
        <button id="requestQuote" type="button" class="btn btn-default btn-block requestQuoteButton checkoutSummaryButton" disabled="disabled">
            <spring:theme code="checkout.summary.requestQuote"/>
        </button>
    </form:form>
</div>
