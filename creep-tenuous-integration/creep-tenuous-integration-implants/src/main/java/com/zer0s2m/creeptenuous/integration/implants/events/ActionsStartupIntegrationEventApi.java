package com.zer0s2m.creeptenuous.integration.implants.events;

import com.zer0s2m.creeptenuous.integration.core.ActionsStartupIntegrationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Class for the launch of actions at the start of the
 * integration of an internal or third-party service
 * <p>Use with overflowing events such as:</p>
 * <ul>
 *     <li><a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/event/ContextRefreshedEvent.html">ContextRefreshedEvent</a></li>
 *     <li><a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/event/ContextStartedEvent.html">ContextStartedEvent</a></li>
 * </ul>
 */
@Component
@PropertySource("classpath:application.properties")
public class ActionsStartupIntegrationEventApi implements ActionsStartupIntegrationEvent {

    private final Logger logger = LogManager.getLogger();

    @Value("${integration.implants.enabled:false}")
    private String isIntegration;

    @Value("${integration.implants.host:localhost}")
    private String host;

    @Value("${integration.implants.port:9191}")
    private String port;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Calling a method while listening for context
     * <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/event/package-use.html#org.springframework.context.event">events</a>
     */
    @Override
    @EventListener(ContextRefreshedEvent.class)
    public void startup() {
        if (isIntegration.trim().equals("true")) {
            logger.info("Starting the integration of the internal service [CreepTenuousImplants]");
            check();
        }
    }

    /**
     * Service Availability Check
     */
    private void check() {
        final String url = "http://" + host + ":" + port;
        try {
            restTemplate.getForObject(url, String.class);
        } catch (RestClientException exception) {
            if ((exception.getClass().equals(ResourceAccessException.class))) {
                logger.error("Service is unavailable [CreepTenuousImplants]" + "[" + exception.getMessage() + "]");
            } else {
                logger.info("Service available [CreepTenuousImplants]");
            }
        }
    }

}
