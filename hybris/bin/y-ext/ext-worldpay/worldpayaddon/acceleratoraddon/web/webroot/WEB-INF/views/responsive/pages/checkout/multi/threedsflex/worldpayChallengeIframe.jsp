<%@ page trimDirectiveWhitespaces="true" %>
<form name="challengeForm" method="POST" id="challengeForm">
    <input type="hidden" name="JWT" id="JWT"/>
</form>

<script>
    window.onload = function () {
        document.getElementById('challengeForm').action = parent.window.document.getElementById("challengeUrl").getAttribute("data-challengeUrl");
        document.getElementById('JWT').value = parent.window.document.getElementById("challengeJWT").getAttribute("data-jwtchallenge");
        document.getElementById('challengeForm').submit();
    }
</script>
