<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<form id="collectionForm" name="devicedata" method="POST" action="${threeDSecureDDCUrl}">
    <input type="hidden" id="cardNumber" name="Bin" value="" />
    <input type="hidden" name="JWT" value="${jwt3DSecureFlexDDC}"/>
    <input type="submit">
</form>

<script type="text/javascript">
    var ACC = window.parent.ACC;
    const parent = window.parent.document;
    const iframe = parent.querySelector('#DDCIframe').contentWindow.document;

    const submit_cseDetails = parent.querySelector('.submit_cseDetails')

    if (submit_cseDetails) {
      submit_cseDetails.addEventListener('click', function() {
        iframe.querySelector('#collectionForm #cardNumber').value = parent.querySelector('#number')
        ACC.worldpayCSE.encryptCardDetails();
      });
    }

    const btn_place_order = parent.querySelector('.btn-place-order');

    if (btn_place_order) {
      btn_place_order.addEventListener('click', function() {
        iframe.querySelector('#collectionForm #cardNumber').value = parent.querySelector('#number')
        iframe.querySelector("#collectionform").submit();
      });
    }
</script>
