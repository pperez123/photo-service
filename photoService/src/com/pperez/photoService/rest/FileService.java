/**
 * 
 */
package com.pperez.photoService.rest;

import java.io.File;

import javax.servlet.ServletContext;
import javax.websocket.server.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.file.DefaultMediaTypePredictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pperez.photoService.rest.util.FileStreamingOutput;
import com.pperez.photoService.servlet.UploadService;

/**
 * @author Philip
 *         Perez Aug 9, 2014
 *         FileService.java
 */
@Path("/file/{filename}")
public class FileService {
    final Logger logger = LoggerFactory.getLogger(UploadService.class);

    // location to store file uploaded
    private static final String UPLOAD_DIRECTORY = "upload";
    
    @Context
    ServletContext servletContext;

    @GET
    public Response getFile(@PathParam("filename") String fileName) throws WebApplicationException {
        String uploadDirectory = uploadDirectory();
        FileStreamingOutput fileOutput = null;
        
        if (uploadDirectory != null) {
            String filePath = uploadDirectory + File.separator + fileName;
            File file = new File(filePath);
            
            if (file.exists()) {
                fileOutput = new FileStreamingOutput(file);
            } else {
                throw new WebApplicationException(Status.NOT_FOUND);
            }
        }

        return responseWithEntity(fileOutput, DefaultMediaTypePredictor.CommonMediaTypes.
                getMediaTypeFromFileName(fileName)).build();
    }

    @OPTIONS
    @HEAD
    public Response returnOptions() {
        Response response = Response.ok()
            .header("Pragma", "no-cache")
            .header("Cache-Control", "no-store, no-cache, must-revalidate")
            .header("X-Content-Type-Options", "nosniff") // Prevent IE from MIME sniffing the content
            .header("Access-Control-Allow-Origin", "*") // Allow cross domain resource sharing
            .header("Access-Control-Allow-Credentials", "true")
            .header("Access-Control-Allow-Methods", "OPTIONS,HEAD,GET,POST,PUT,PATCH,DELETE")
            .header("Access-Control-Allow-Headers", "Content-Type,Content-Disposition,Content-Range")
            .build();
        return response;
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
    
    protected Response.ResponseBuilder responseWithEntity(Object entity, MediaType type) {
        return Response.ok(entity, type)
                .header("Pragma", "no-cache")
                .header("Cache-Control", "no-store, no-cache, must-revalidate")
                .header("X-Content-Type-Options", "nosniff"); // Prevent IE from MIME sniffing the content
    }
}
