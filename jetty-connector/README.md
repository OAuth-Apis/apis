Jetty Connector for proxied configuration
======
This project contains an extension plugin for Jetty that permits to permit a proper Apache (or Nginx) proxying.
This extension permits to implement the configuration described here:
```
   https                 http
 --------->   Apache   -------> Jetty
```

To permit this workflow the request schema is retrieved from the `X-Forwarded-Proto` HTTP header.
This is the standard behavior of Jetty 9, this extension makes it available also in Jetty 8.
