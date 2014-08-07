package org.surfnet.oaaas.cas;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bourges on 05/08/14.
 */
public class PostCasAuthenticationFilter implements Filter {

    public static String POST_CAS_AUTHENTICATION_INFO = "casUser";
    public static String REDIRECT_URL = "redirectURL";

    private List<String> admins = new ArrayList<String>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String adminList = filterConfig.getInitParameter("admins");
        if (adminList != null) {
            admins = Arrays.asList(adminList.split(","));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        CasUser casUser = new CasUser();
        String uid = httpServletRequest.getRemoteUser();
        casUser.setUid(uid);
        if (admins.contains(uid)) {
            casUser.setAdmin(true);
        }
        httpServletRequest.getSession().setAttribute(POST_CAS_AUTHENTICATION_INFO, casUser);
        String uri = (String) httpServletRequest.getSession().getAttribute(REDIRECT_URL);
        httpResponse.sendRedirect(uri);
    }

    @Override
    public void destroy() {

    }

    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }

}
