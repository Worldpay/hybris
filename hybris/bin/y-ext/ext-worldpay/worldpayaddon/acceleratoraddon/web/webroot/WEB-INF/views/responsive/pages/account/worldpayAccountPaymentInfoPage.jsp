<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wp-account" tagdir="/WEB-INF/tags/addons/worldpayaddon/responsive/account" %>


<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="noBorder" value=""/>
<c:if test="${not empty paymentInfoData}">
    <c:set var="noBorder" value="no-border"/>
</c:if>

<div class="account-section-header ${noBorder}">
    <spring:theme code="text.account.paymentDetails" />
</div>
<c:choose>
    <c:when test="${not empty paymentInfoData}">
        <div class="account-paymentdetails account-list">
            <div class="account-cards card-select">
                <div class="row">
                    <c:forEach items="${paymentInfoData}" var="paymentInfo">
                        <c:choose>
                            <c:when test="${paymentInfo.isAPM}">
                                <wp-account:worldpayAPMPaymentInfo paymentInfo="${paymentInfo}"/>
                            </c:when>
                            <c:otherwise>
                                <wp-account:worldpayCreditCardPaymentInfo paymentInfo="${paymentInfo}"/>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </div>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="account-section-content content-empty">
            <spring:theme code="text.account.paymentDetails.noPaymentInformation" />
        </div>
    </c:otherwise>
</c:choose>
