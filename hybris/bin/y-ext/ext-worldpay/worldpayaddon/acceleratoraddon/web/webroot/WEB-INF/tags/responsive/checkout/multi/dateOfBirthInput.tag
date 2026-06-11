<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="form-group hidden" id="dobElement">
    <label class="control-label ${fn:escapeXml(labelCSS)}" for="dateOfBirth">
        <spring:theme code="payment.dateOfBirth"/>
    </label>

    <fmt:parseDate value="${currentDate}" pattern="dd/MM/yyyy" var="birthDate"/>
    <fmt:formatDate value="${birthDate}" var="formattedDate" type="date" pattern="dd/MM/yyyy"/>
    <form:input type="date" id="dateOfBirth" cssClass="${fn:escapeXml(inputCSS)} form-control" path="dateOfBirth"
                value="${formattedDate}"/>
    <div class="help-block hidden">
        <span id="error-dateOfBirth"></span>
    </div>

    <form:input type="hidden" id="dobRequired" path="dobRequired" value="true"/>
</div>
