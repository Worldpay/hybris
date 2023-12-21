<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${(isEnabled eq true) and (not empty script) and (not empty sessionId)}">
    <script>
      const script = window.document.querySelector('script#sig-api');
      if (!script) {
        let node = window.document.createElement('script');
        node.src = "${script}";
        node.id = "sig-api";
        node.type = 'text/javascript';
        node.defer = true;
        node.setAttribute("data-order-session-id", "${sessionId}");
        window.document.getElementsByTagName('head')[0].appendChild(node);
      }
    </script>
</c:if>
