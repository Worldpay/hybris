<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="wp-order" tagdir="/WEB-INF/tags/addons/worldpayb2baddon/responsive/order" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<spring:url value="${url}" var="quoteOrderDecisionURL" htmlEscape="false" />

<c:set var="isCardPayment" value="${orderData.paymentInfo != null}" />

<c:if test="${orderData.status eq 'APPROVED_QUOTE'}">
	<wp-order:worldpayAcceptQuoteOrderDecisionForm quoteOrderDecisionURL="${quoteOrderDecisionURL}" orderCode="${orderData.code}"
												   decisionLabelKey="text.quotes.acceptAndOrderButton.displayName"
												   modalTitleLabelKey="text.quotes.enterSecurityCode"
												   decisionButtonCSSClass="btn btn-primary btn-block"
												   isCardPayment="${isCardPayment}" />
</c:if>