<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>

<div class="form-group hidden" id="achForm">
    <formElement:formSelectBox idKey="achForm.accountType" labelKey="worldpay.apm.payment.method.accountType" path="achForm.accountType" mandatory="true" skipBlank="false" skipBlankMessageKey="accountType.pleaseSelect" items="${achAccountTypes}" itemValue="left" itemLabel="right" selectedValue="${achAccountTypes[0]}" tabindex="${tabindex + 1}" selectCSSClass="form-control"/>
    <formElement:formInputBox idKey="achForm.accountNumber" labelKey="worldpay.apm.payment.method.accountNumber" path="achForm.accountNumber" inputCSS="form-control" mandatory="true" tabindex="${tabindex + 2}"/>
    <formElement:formInputBox idKey="achForm.routingNumber" labelKey="worldpay.apm.payment.method.routingNumber" path="achForm.routingNumber" inputCSS="form-control" mandatory="true" tabindex="${tabindex + 3}"/>
    <formElement:formInputBox idKey="achForm.checkNumber" labelKey="worldpay.apm.payment.method.checkNumber" path="achForm.checkNumber" inputCSS="form-control" mandatory="true" tabindex="${tabindex + 4}"/>
    <formElement:formInputBox idKey="achForm.companyName" labelKey="worldpay.apm.payment.method.companyName" path="achForm.companyName" inputCSS="form-control" mandatory="true" tabindex="${tabindex + 5}"/>
    <formElement:formInputBox idKey="achForm.customIdentifier" labelKey="worldpay.apm.payment.method.customIdentifier" path="achForm.customIdentifier" inputCSS="form-control" mandatory="true" tabindex="${tabindex + 6}"/>
</div>

<div class="clear"></div>
