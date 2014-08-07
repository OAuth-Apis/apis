package org.surfnet.oaaas.config;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.surfnet.oaaas.cas.PostCasAuthenticationFilter;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bourges on 06/08/14.
 */
@Configuration
@PropertySource("classpath:apis.application.properties")
public class CasSpringConfiguration {

    @Value("${cas.serverName}")
    private String serverName;

    @Value("${cas.serverUrlPrefix}")
    private String serverUrlPrefix;

    @Value("${cas.adminList}")
    private String adminList;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Filter casAuthenticationFilter() {
        final AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setCasServerLoginUrl(serverUrlPrefix + "/login");
        authenticationFilter.setServerName(serverName);
        return authenticationFilter;
    }

    @Bean
    public Filter casValidationFilter() {
        final Cas20ProxyReceivingTicketValidationFilter casValidationFilter = new Cas20ProxyReceivingTicketValidationFilter();
        final Cas20ServiceTicketValidator ticketValidator = new Cas20ServiceTicketValidator(serverUrlPrefix);
        casValidationFilter.setTicketValidator(ticketValidator);
        casValidationFilter.setServerName(serverName);
        return casValidationFilter;
    }

    @Bean
    public Filter postCASAuthenticationFilter() {
        final PostCasAuthenticationFilter postCasAuthenticationFilter = new PostCasAuthenticationFilter();
        if (adminList != null) {
            List<String> admins = Arrays.asList(adminList.split(","));
            postCasAuthenticationFilter.setAdmins(admins);
        }
        return postCasAuthenticationFilter;
    }


}
