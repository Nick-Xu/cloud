package io.fathom.cloud.network.api.os.resources;

import java.io.IOException;
import java.net.URI;

import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Provider
@PreMatching
public class StripExtensionFilter implements ContainerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(StripExtensionFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        UriInfo uriInfo = requestContext.getUriInfo();

        String path = uriInfo.getPath();
        if (path.endsWith(".json")) {
            if (!path.startsWith("/")) {
                path = "/" + path;
            }

            if (path.startsWith("/openstack/network/")) {
                path = path.substring(0, path.length() - 5);
                URI uri = uriInfo.getRequestUri();

                URI newUri = UriBuilder.fromUri(uri).replacePath(path).build();
                log.info("Rewriting URI: {} -> {}", uri, newUri);

                requestContext.setRequestUri(newUri);
            }
        }
    }

}
