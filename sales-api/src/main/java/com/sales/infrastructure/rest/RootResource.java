package com.sales.infrastructure.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Map;

@Path("/")
public class RootResource {

    @ConfigProperty(name = "app.name", defaultValue = "Sales API")
    String appName;

    @ConfigProperty(name = "mp.openapi.extensions.smallrye.info.version", defaultValue = "1.0.0")
    String version;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response root() {
        return Response.ok(Map.of(
            "name", appName,
            "version", version,
            "status", "running",
            "links", Map.of(
                "swagger", "/q/swagger-ui",
                "openapi", "/q/openapi",
                "health", "/q/health",
                "api", "/api/v1"
            )
        )).build();
    }

    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Response health() {
        return Response.ok(Map.of(
            "status", "UP",
            "service", appName
        )).build();
    }
}
