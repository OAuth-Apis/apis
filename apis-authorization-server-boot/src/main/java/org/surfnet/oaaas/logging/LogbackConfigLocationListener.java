package org.surfnet.oaaas.logging;

import ch.qos.logback.classic.BasicConfigurator;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.URL;

public class LogbackConfigLocationListener implements ServletContextListener {

  private static final Logger LOG = LoggerFactory.getLogger(LogbackConfigLocationListener.class);

  public static final String CONFIG_FILE = "/apis-logback.xml";

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
        URL logbackConfigLocation = LogbackConfigLocationListener.class.getResource(CONFIG_FILE);

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        if (logbackConfigLocation == null) {
          BasicConfigurator.configureDefaultContext();
          LOG.info("No context-specific configuration file found, will use Logback's default configuration");
        } else {
          LOG.debug("Found logback configuration file at {}", logbackConfigLocation);
          JoranConfigurator configurator = new JoranConfigurator();
          configurator.setContext(lc);
          // the context was probably already configured by default configuration rules
          lc.reset();
          try {
            configurator.doConfigure(logbackConfigLocation);
          } catch (JoranException je) {
            // StatusPrinter will handle this
          }

        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
    } catch (Exception ex) {
      //Failed to load the custom log file, we log an error and use the default log config.
      LOG.error("Unable to initialize context", ex);
    }

  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
  }
}
