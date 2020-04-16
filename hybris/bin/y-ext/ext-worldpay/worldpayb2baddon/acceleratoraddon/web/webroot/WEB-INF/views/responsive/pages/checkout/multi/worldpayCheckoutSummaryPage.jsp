<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="wp-b2b-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayb2baddon/responsive/checkout/multi" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<spring:url value="/checkout/multi/worldpay/summary/placeOrder" var="placeOrderUrl" htmlEscape="false"/>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
    <div class="row">
        <div class="col-sm-6">
            <div class="checkout-headline">
                <span class="glyphicon glyphicon-lock"></span>
                <spring:theme code="checkout.multi.secure.checkout" />
            </div>

            <multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
                <ycommerce:testId code="checkoutStepFour">
                    <div class="checkout-review hidden-xs">
                        <div class="checkout-order-summary">
                            <multi-checkout:orderTotals cartData="${cartData}" showTaxEstimate="${showTaxEstimate}" showTax="${showTax}" subtotalsCssClasses="dark"/>
                        </div>
                    </div>
                    <div class="place-order-form hidden-xs">
                        <form:form action="${placeOrderUrl}" id="placeOrderForm1" modelAttribute="b2bCSEPaymentForm">
                            <wp-b2b-multi-checkout:securityCode/>
                            <wp-b2b-multi-checkout:worldpayCSESavedCardDetails/>
                            <wp-b2b-multi-checkout:termsAndConditions/>
                            <button id="placeOrder" type="submit" class="btn btn-primary btn-block btn-place-order btn-block btn-lg checkoutSummaryButton" disabled="disabled">
                                <spring:theme code="checkout.summary.placeOrder"/>
                            </button>

                            <c:if test="${cartData.quoteData eq null}">
                                <button id="scheduleReplenishment" type="button" class="btn btn-default btn-block scheduleReplenishmentButton checkoutSummaryButton" disabled="disabled">
                                    <spring:theme code="checkout.summary.scheduleReplenishment"/>
                                </button>
                                <wp-b2b-multi-checkout:replenishmentScheduleForm/>
                            </c:if>
                        </form:form>
                    </div>

                </ycommerce:testId>
            </multi-checkout:checkoutSteps>
        </div>

        <div class="col-sm-6">
            <wp-b2b-multi-checkout:checkoutOrderSummary cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="true" showTaxEstimate="true" showTax="true" />
        </div>

        <div class="col-sm-12 col-lg-12">
            <br class="hidden-lg">
            <cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
                <cms:component component="${feature}"/>
            </cms:pageSlot>
        </div>
    </div>
</template:page>
