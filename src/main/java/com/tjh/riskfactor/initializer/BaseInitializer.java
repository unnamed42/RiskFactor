package com.tjh.riskfactor.initializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public abstract class BaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${debug}")
    private boolean debug;

    private boolean done = false;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(!debug || done)
            return;
        initialize();
        done = true;
    }

    protected abstract void initialize();
}
