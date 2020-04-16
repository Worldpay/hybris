<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/responsive/checkout/multi" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

     <jsp:attribute name="pageScripts">
        <script type="text/javascript" src="https://payments.worldpay.com/resources/cse/js/worldpay-cse-1.0.2.min.js"></script>
        <script>
            $(document).ready(function () {
                $("#worldpayBillingAddressForm").attr("data-worldpay", "payment-form");
                Worldpay.setPublicKey("${csePublicKey}");
                ACC.worldpayCSE.originEventDomain3DSFlex = "${originEventDomain3DSFlex}";
            });
        </script>
    </jsp:attribute>

    <jsp:body>
        <div class="row">
            <div class="col-sm-6">
                <div class="checkout-headline">
                    <span class="glyphicon glyphicon-lock"></span>
                    <spring:theme code="checkout.multi.secure.checkout"/>
                </div>
                <multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
                    <ycommerce:testId code="checkoutStepThree">
                        <div class="checkout-paymentmethod">
                            <div class="checkout-indent">
                                <div class="headline"><spring:theme code="checkout.multi.paymentMethod"/></div>

                                <c:url value="/checkout/multi/worldpay/cse/place-order" var="addCseDataUrl"/>
                                <form:form id="worldpayCsePaymentForm" modelAttribute="csePaymentForm" method="post"
                                           action="${addCseDataUrl}" class="create_update_payment_form">

                                    <wp-multi-checkout:worldpayCSECardDetails/>
                                    <wp-multi-checkout:termsAndConditions/>

                                </form:form>

                            </div>
                        </div>
                        <div class="form-actions">
                            <button class="btn btn-primary btn-block submit_cseDetails checkout-next" tabindex="20">
                                <spring:theme code="checkout.multi.paymentMethod.continue" />
                            </button>
                        </div>
                    </ycommerce:testId>
                </multi-checkout:checkoutSteps>
            </div>

            <div class="col-sm-6 hidden-xs">
                <multi-checkout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="false" showTaxEstimate="false" showTax="true"/>
            </div>
            <div class="col-sm-12 col-lg-12">
                <cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
                    <cms:component component="${feature}"/>
                </cms:pageSlot>
            </div>
        </div>
    </jsp:body>
</template:page>
