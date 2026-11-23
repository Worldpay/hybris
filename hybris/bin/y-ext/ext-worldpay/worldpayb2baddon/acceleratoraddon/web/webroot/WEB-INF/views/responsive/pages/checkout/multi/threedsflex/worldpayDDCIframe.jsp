<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<form id="collectionForm" name="devicedata" method="POST" action="${threeDSecureDDCUrl}">
    <input type="hidden" id="cardNumber" name="Bin" value=""/>
    <input type="hidden" name="JWT" value="${jwt3DSecureFlexDDC}"/>
    <input type="submit">
</form>

<script type="text/javascript">
    window.submitWorldpayDdcCollectionForm = function (cardNumber) {
        document.querySelector('#cardNumber').value = cardNumber.replace(/\D/g, '').substring(0, 6);
        document.querySelector('#collectionForm').submit();
    };
</script>
