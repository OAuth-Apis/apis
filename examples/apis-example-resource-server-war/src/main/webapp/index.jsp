<%@page import="org.surfnet.oaaas.model.VerifyTokenResponse"%>
<%= ((VerifyTokenResponse) request.getAttribute("VERIFY_TOKEN_RESPONSE")).getPrincipal() %>