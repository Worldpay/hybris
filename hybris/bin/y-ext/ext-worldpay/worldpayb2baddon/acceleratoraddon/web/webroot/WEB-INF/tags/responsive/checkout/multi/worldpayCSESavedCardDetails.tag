<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div id="cardDetailsFieldSet">
    <fieldset class="cardForm">
        <input type="hidden" id="number" name="number" value="${not empty bin ? bin : subscriptionId}">
        <input type="hidden" id="encryptedData" name="cseToken">
        <input type="hidden" id="threeDSReferenceId" name="referenceId">
        <input type="hidden" id="windowSizePreference" name="windowSizePreference">
    </fieldset>

    <iframe src="DDCIframe" width="1" height="1" hidden="true" id="DDCIframe"></iframe>
</div>
