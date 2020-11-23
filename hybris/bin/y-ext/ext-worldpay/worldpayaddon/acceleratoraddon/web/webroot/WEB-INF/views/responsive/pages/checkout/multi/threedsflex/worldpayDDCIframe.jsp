<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<form id="collectionForm" name="devicedata" method="POST" action="${threeDSecureDDCUrl}">
    <input type="hidden" id="cardNumber" name="Bin" value=""/>
    <input type="hidden" name="JWT" value="${jwt3DSecureFlexDDC}"/>
    <input type="submit">
</form>

<script type="text/javascript">
  var $ = window.parent.$;
  var ACC = window.parent.ACC;

  $('.submit_cseDetails').on('click', function () {
    document.getElementById('cardNumber').value = $('#number').val();
    ACC.worldpayCSE.encryptCardDetails();
    document.querySelector("#collectionform").submit();
  });
</script>
