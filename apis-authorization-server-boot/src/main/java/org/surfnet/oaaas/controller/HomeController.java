package org.surfnet.oaaas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

  @RequestMapping("/")
  public String home() {
    return "redirect:/client/client.html";
  }

  @RequestMapping("/login.html")
  public ModelAndView login() {
    return new ModelAndView("login");
  }
  @RequestMapping("/userconsent.html")
  public ModelAndView userconsent() {
    return new ModelAndView("userconsent");
  }
  @RequestMapping("/userconsent-denied.html")
  public ModelAndView userconsentDenied() {
    return new ModelAndView("userconsent_denied");
  }
}
