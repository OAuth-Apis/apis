package org.surfnet.oaaas.cas;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by bourges on 05/08/14.
 */
public class PostCasAuthenticationFilter implements Filter {

    public static String POST_CAS_AUTHENTICATION_INFO = "casUser";
    public static String REDIRECT_URL = "redirectURL";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpServletRequest.getSession().setAttribute(POST_CAS_AUTHENTICATION_INFO, httpServletRequest.getRemoteUser());
        String uri = (String) httpServletRequest.getSession().getAttribute(REDIRECT_URL);
        httpResponse.sendRedirect(uri);
    }

    @Override
    public void destroy() {

    }
}
