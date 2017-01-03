<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="quoteOrderDecisionURL" required="true" type="java.lang.String"%>
<%@ attribute name="orderCode" required="true" type="java.lang.String"%>
<%@ attribute name="modalTitleLabelKey" required="false" type="java.lang.String"%>
<%@ attribute name="modalInputLabelKey" required="false" type="java.lang.String"%>
<%@ attribute name="decisionLabelKey" required="true" type="java.lang.String"%>
<%@ attribute name="decisionButtonCSSClass" required="false" type="java.lang.String"%>
<%@ attribute name="isCardPayment" required="true" type="java.lang.Boolean"%>
<%@ attribute name="mobileText" required="false" type="java.lang.String"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="securityCodeMaxChars" value="5" />

<form:form method="post" cssClass="acceptQuoteOrderDecisionForm" commandName="acceptQuoteOrderDecisionForm" action="${quoteOrderDecisionURL}">
    <input type="hidden" name="orderCode" value="${orderCode}" />
        <button class="${decisionButtonCSSClass} ${isCardPayment ? ' payByCard' : ''}"
                data-modal-title-label="<spring:theme code='${modalTitleLabelKey}' />"
                data-modal-input-label="<spring:theme code='${modalInputLabelKey}' />">
            <span><spring:theme code="${decisionLabelKey}" /></span>
            <c:if test="${not empty mobileText}">
                <span><spring:theme code="${mobileText}" /></span>
            </c:if>
        </button>
    <c:choose>
        <c:when test="${isCardPayment}">
            <div style="display:none">
                <div class="quoteAcceptModal comment-modal">
                    <div class="headline">&nbsp;</div>

                    <input name="securityCode" maxlength="${securityCodeMaxChars}"></input>

                    <div class="modal-actions">
                        <div class="row">
                            <div class="col-xs-12 col-sm-6 col-sm-push-6">
                                <button type="submit" class="btn btn-primary btn-block submitQuoteAcceptButton">
                                    <spring:theme code="text.quotes.submitButton.displayName"/>
                                </button>
                            </div>
                            <div class="col-xs-12 col-sm-6 col-sm-pull-6">
                                <button type="button" class="btn btn-default btn-block cancelQuoteAcceptButton">
                                    <spring:theme code="text.quotes.cancelButton.displayName"/>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <input type="hidden" name="securityCode" value="" />
        </c:otherwise>
    </c:choose>
</form:form>