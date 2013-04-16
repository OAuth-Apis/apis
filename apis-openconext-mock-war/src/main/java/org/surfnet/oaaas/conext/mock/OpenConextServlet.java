package org.surfnet.oaaas.conext.mock;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OpenConextServlet extends HttpServlet {

  private String callBackUrl;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    callBackUrl = config.getInitParameter("call-back-url");
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.sendRedirect(callBackUrl);
  }
}
