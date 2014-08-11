/**
 * 
 */
package com.pperez.photoService.rest;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Philip Perez Aug 10, 2014 BaseService.java
 */
public class BaseService {
    private final Logger logger = LoggerFactory.getLogger(BaseService.class);

    // location to store file uploaded
    protected static final String UPLOAD_DIRECTORY = "upload";
    
    // endpoints
    protected static final String FILE_ENDPOINT = "/photoService/file/";
    protected static final String THUMBNAIL_ENDPOINT = "/photoService/thumbnail/";

    // Media types where "Content-Disposition" header should be "inline"
    protected static final String[] inlineMediaTypes = { "image/png", "image/jpeg", "image/gif", "image/jpg" };

    @Context
    protected ServletContext servletContext;
    
    @Context
    protected HttpServletRequest servletRequest;
    
    @Context
    protected UriInfo uriInfo;

    @OPTIONS
    public Response returnOptions() {
        logger.debug("returnOptions()");
        return defaultOkResponse().build();
    }
    
    @HEAD
    public Response doHead() {
        logger.debug("doHead()");
        return defaultOkResponse().build();
    }

    // constructs the directory path to store upload file
    // this path is relative to application's directory
    protected String uploadDirectory() {
        String uploadPath = "";

        if (servletContext != null) {
            uploadPath = servletContext.getRealPath("") + File.separator + UPLOAD_DIRECTORY;
        } else {
            logger.debug("Servlet context is null.");
        }

        return uploadPath;
    }
    
    protected String pathForUploadedFile(String fileName) {
        return uploadDirectory() + File.separator + fileName;
    }

    protected Response.ResponseBuilder responseWithEntity(Object entity, MediaType type) {
        return defaultOkResponse()
                .entity(entity)
                .type(type);
    }
    
    protected Response.ResponseBuilder defaultOkResponse() {
        return Response.ok()
                .header("Pragma", "no-cache")
                .header("Cache-Control", "no-store, no-cache, must-revalidate")
                // Prevent IE from MIME sniffing the content
                .header("X-Content-Type-Options", "nosniff")
                // Allow cross domain resource sharing
                .header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "OPTIONS,HEAD,GET,POST,PUT,PATCH,DELETE")
                .header("Access-Control-Allow-Headers", "Content-Type,Content-Disposition,Content-Range");
    }

    protected boolean inlineType(String type) {
        logger.debug("Inline type? " + type);
        boolean flag = false;

        if (type != null) {
            for (String inlineType : inlineMediaTypes) {
                if (type.equalsIgnoreCase(inlineType)) {
                    logger.debug("Inline type detected: " + type);
                    flag = true;
                    break;
                }
            }
        }

        return flag;
    }
}
