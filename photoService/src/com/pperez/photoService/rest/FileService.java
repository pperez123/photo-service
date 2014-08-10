/**
 * 
 */
package com.pperez.photoService.rest;

import java.io.File;

import javax.servlet.ServletContext;
import javax.websocket.server.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pperez.photoService.servlet.UploadService;

/**
 * @author Philip 
 * Perez Aug 9, 2014 
 * FileService.java
 */
@Path("/file/{filename}")
public class FileService {
    final Logger logger = LoggerFactory.getLogger(UploadService.class);

    // location to store file uploaded
    private static final String UPLOAD_DIRECTORY = "upload";
    @Context
    ServletContext servletContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getFile(@PathParam("filename") String fileName) {
        String uploadDirectory = uploadDirectory();

        if (uploadDirectory != null) {
            String filePath = uploadDirectory + File.separator + fileName;
            File file = new File(filePath);

            if (file.exists()) {

            }
        }

        return null;
    }

    @OPTIONS
    public String returnOptions() {

        return null;
    }

    // constructs the directory path to store upload file
    // this path is relative to application's directory
    protected String uploadDirectory() {
        String uploadPath = null;

        if (servletContext != null) {
            uploadPath = servletContext.getRealPath("") + File.separator + UPLOAD_DIRECTORY;
        }
        else {
            logger.debug("Servlet context is null.");
        }

        return uploadPath;
    }
}
