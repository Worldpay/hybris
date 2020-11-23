<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="paymentInfo" required="true" type="de.hybris.platform.commercefacades.order.data.CCPaymentInfoData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="col-xs-12 col-sm-6 col-md-4 card">
    <ul class="pull-left">
        <li>
            <c:if test="${paymentInfo.defaultPaymentInfo}"><strong></c:if>
            ${fn:escapeXml(paymentInfo.apmName)}<c:if test="${paymentInfo.defaultPaymentInfo}" >&nbsp;(<spring:theme code="text.default" />)</c:if>
            <c:if test="${paymentInfo.defaultPaymentInfo}" ></strong></c:if>
            <c:if test="${!empty paymentInfo.cardNumber}"><br>${paymentInfo.cardNumber}</c:if>
        </li>
    </ul>
    <div class="account-cards-actions pull-left">
        <ycommerce:testId code="paymentDetails_deletePayment_button" >
            <a class="action-links removePaymentDetailsButton" href="#" data-payment-id="${fn:escapeXml(paymentInfo.id)}" data-popup-title="<spring:theme code="text.account.paymentDetails.delete.popup.title"/>">
                <span class="glyphicon glyphicon-remove"></span>
            </a>
        </ycommerce:testId>
    </div>
    <c:if test="${not paymentInfo.defaultPaymentInfo}" >
        <c:url value="/my-account/set-default-payment-details" var="setDefaultPaymentActionUrl"/>
        <form:form id="setDefaultPaymentDetails${paymentInfo.id}" action="${setDefaultPaymentActionUrl}" method="post">
            <input type="hidden" name="paymentInfoId" value="${fn:escapeXml(paymentInfo.id)}"/>
            <ycommerce:testId code="paymentDetails_setAsDefault_button" >
                <button type="submit" class="account-set-default-address">
                    <spring:theme code="text.setDefault" />
                </button>
            </ycommerce:testId>
        </form:form>
    </c:if>
</div>

<div class="display-none">
    <div id="popup_confirm_payment_removal_${fn:escapeXml(paymentInfo.id)}" class="account-address-removal-popup">
        <spring:theme code="text.account.paymentDetails.delete.following"/>
        <div class="address">
            <strong>
                ${fn:escapeXml(paymentInfo.apmName)}
            </strong>
        </div>
        <c:url value="/my-account/remove-payment-method" var="removePaymentActionUrl"/>
        <form:form id="removePaymentDetails${paymentInfo.id}" action="${removePaymentActionUrl}" method="post">
            <input type="hidden" name="paymentInfoId" value="${fn:escapeXml(paymentInfo.id)}"/>
            <br />
            <div class="modal-actions">
                <div class="row">
                    <ycommerce:testId code="paymentDetailsDelete_delete_button" >
                        <div class="col-xs-12 col-sm-6 col-sm-push-6">
                            <button type="submit" class="btn btn-default btn-primary btn-block paymentsDeleteBtn">
                                <spring:theme code="text.account.paymentDetails.delete"/>
                            </button>
                        </div>
                    </ycommerce:testId>
                    <div class="col-xs-12 col-sm-6 col-sm-pull-6">
                        <a class="btn btn-default closeColorBox paymentsDeleteBtn btn-block" data-payment-id="${fn:escapeXml(paymentInfo.id)}">
                            <spring:theme code="text.button.cancel" />
                        </a>
                    </div>
                </div>
            </div>
        </form:form>
    </div>
</div>
