/**
 * 
 */
package com.pperez.photoService.rest;

import java.io.File;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.websocket.server.PathParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.file.DefaultMediaTypePredictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pperez.photoService.rest.util.FileStreamingOutput;

/**
 * @author Philip Perez 
 * Aug 9, 2014 
 * FileService.java
 */
@Path("/file/{filename}")
public class FileService extends BaseService {
    private final Logger logger = LoggerFactory.getLogger(FileService.class);

    @GET
    public Response getFile(@PathParam("filename") String fileName) throws WebApplicationException {
        logger.debug("File name is " + fileName);
        
        String uploadDirectory = uploadDirectory();
        FileStreamingOutput fileOutput = null;
        Response returnResponse = Response.status(Status.BAD_REQUEST).build();

        if (fileName != null) {
            File file = new File(pathForUploadedFile(fileName));

            if (file.exists()) {
                fileOutput = new FileStreamingOutput(file);
            } else {
                logger.debug("File not found: " + pathForUploadedFile(fileName));
                throw new WebApplicationException(Status.NOT_FOUND);
            }

            MediaType type = DefaultMediaTypePredictor.CommonMediaTypes.getMediaTypeFromFileName(fileName);
            type = inlineType(type.getType()) ? MediaType.APPLICATION_OCTET_STREAM_TYPE : type;

            returnResponse = responseWithEntity(fileOutput, type)
                    .header("Content-Disposition", inlineType(type.getType()) ? "inline" : "attachment" + ", filename=" + fileName)
                    .header("Content-Length", file.length())
                    .lastModified(new Date(file.lastModified())).build();
        } else {
            File uploadDir = new File(uploadDirectory);

            if (uploadDir.exists() && uploadDir.isDirectory()) {
                JsonArrayBuilder fileArrayBuilder = Json.createArrayBuilder();
                
                for (File file : uploadDir.listFiles()) {
                    String hostName = "http://" + servletRequest.getServerName();

                    if (servletRequest.getServerPort() != 80) {
                        hostName += ":" + servletRequest.getServerPort();
                    }

                    JsonObjectBuilder fileItem = Json.createObjectBuilder()
                            .add("name", file.getName())
                            .add("size", file.length())
                            .add("url", hostName + FILE_ENDPOINT + file.getName())
                            .add("thumbnail_url", hostName + THUMBNAIL_ENDPOINT + file.getName())
                            .add("delete_url", hostName + FILE_ENDPOINT + file.getName()).add("delete_type", "DELETE");

                    fileArrayBuilder.add(fileItem);
                }
                
                returnResponse = responseWithEntity(fileArrayBuilder.build(), MediaType.APPLICATION_JSON_TYPE).build();
            }
            else {
                logger.debug("Upload directory not found");
                throw new WebApplicationException(Status.NOT_FOUND);
            }
        }

        return returnResponse;
    }
    
    @DELETE
    public Response deleteFile(@PathParam("filename") String fileName) throws WebApplicationException {
        Response returnResponse = Response.status(Status.BAD_REQUEST).build();
        
        if (fileName != null) {
            File file = new File(pathForUploadedFile(fileName));
            
            if (file.exists()) {
                try {
                    if (file.delete()) {
                        returnResponse = Response.ok().build();
                    }
                } catch (SecurityException ex) {
                    logger.warn(ex.getMessage());
                    throw new WebApplicationException(ex.getMessage());
                }
            } else {
                logger.warn("No such file: " + pathForUploadedFile(fileName));
                throw new WebApplicationException(Status.NOT_FOUND);
            }
        }
        
        return returnResponse;
    }
}
