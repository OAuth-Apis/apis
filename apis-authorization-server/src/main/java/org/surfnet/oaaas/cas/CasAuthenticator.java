package org.surfnet.oaaas.cas;

import org.surfnet.oaaas.auth.AbstractAuthenticator;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by bourges on 05/08/14.
 */
public class CasAuthenticator extends AbstractAuthenticator {
    @Override
    public boolean canCommence(HttpServletRequest request) {
        return getAuthStateValue(request) != null;
    }

    @Override
    public void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain chain, String authStateValue, String returnUri) throws IOException, ServletException {
        CasUser casUser = (CasUser) request.getSession().getAttribute(PostCasAuthenticationFilter.POST_CAS_AUTHENTICATION_INFO);
        if (casUser == null) {
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            request.getSession().setAttribute(PostCasAuthenticationFilter.REDIRECT_URL, uri + "?" + queryString);
            response.sendRedirect("/cas");
            return;
        }
        else {
            AuthenticatedPrincipal principal = new AuthenticatedPrincipal(casUser.getUid());
            principal.setAdminPrincipal(casUser.isAdmin);
            super.setPrincipal(request, principal);
            super.setAuthStateValue(request, authStateValue);
            chain.doFilter(request, response);
        }
    }
}
