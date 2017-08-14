package com.alpar.szabados.hibernate.server;

import com.alpar.szabados.hibernate.server.resources.ActivityResource;
import com.alpar.szabados.hibernate.server.resources.UserResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        registerEndpoints();
    }

    private void registerEndpoints() {
        register(UserResource.class);
        register(ActivityResource.class);
    }
}