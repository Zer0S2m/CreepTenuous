package com.zer0s2m.creeptenuous.integration.implants.events;

import com.zer0s2m.creeptenuous.integration.core.ActionsStartupIntegrationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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
public class ActionsStartupIntegrationEventApi implements ActionsStartupIntegrationEvent {

    private final Logger logger = LogManager.getLogger();

    /**
     * Calling a method while listening for context
     * <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/event/package-use.html#org.springframework.context.event">events</a>
     */
    @Override
    @EventListener(ContextRefreshedEvent.class)
    public void startup() {
        logger.info("Starting the integration of the internal service CreepTenuousImplants");
    }

}
