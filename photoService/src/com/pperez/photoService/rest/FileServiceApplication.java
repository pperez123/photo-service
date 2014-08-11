/**
 * 
 */
package com.pperez.photoService.rest;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author pperez
 *
 */
@ApplicationPath("webapi")
public class FileServiceApplication extends ResourceConfig {
    public FileServiceApplication() {
        packages("com.pperez.photoService.rest");
        packages("org.glassfish.jersey.examples.multipart");
        register(MultiPartFeature.class);
    }
}
