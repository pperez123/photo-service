/**
 * 
 */
package com.pperez.photoService.rest;

import java.io.File;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.PathParam;
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
@Path("/file")
public class FileService extends BaseService {
    private final Logger logger = LoggerFactory.getLogger(FileService.class);

    @GET
    @Path("{filename}")
    public Response getFile(@PathParam("filename") String fileName) throws WebApplicationException {
        logger.debug("File name is " + fileName);
        
        FileStreamingOutput fileOutput = null;
        Response returnResponse = Response.status(Status.BAD_REQUEST).build();

        if (fileName != null && fileName.length() > 0) {
            File file = new File(pathForUploadedFile(fileName));

            if (file.exists() && !file.isDirectory()) {
                fileOutput = new FileStreamingOutput(file);
            } else {
                logger.debug("File not found: " + pathForUploadedFile(fileName));
                throw new WebApplicationException(Status.NOT_FOUND);
            }

            MediaType type = DefaultMediaTypePredictor.CommonMediaTypes.getMediaTypeFromFileName(fileName);

            returnResponse = responseWithEntity(fileOutput, inlineType(type.toString()) ? MediaType.APPLICATION_OCTET_STREAM_TYPE : type)
                    .header("Content-Disposition", (inlineType(type.toString()) ? "inline" : "attachment") + ",filename=" + fileName)
                    .header("Content-Length", file.length())
                    .lastModified(new Date(file.lastModified())).build();
            
            logger.debug("Headers: " + returnResponse.getHeaders());
        }

        return returnResponse;
    }
    
    @GET
    public Response getFiles() throws WebApplicationException {
        File uploadDir = new File(uploadDirectory());
        JsonArrayBuilder fileArrayBuilder = Json.createArrayBuilder();
        
        if (uploadDir.exists() && uploadDir.isDirectory()) { 
            for (File file : uploadDir.listFiles()) {
                String hostName = "http://" + servletRequest.getServerName();

                if (servletRequest.getServerPort() != 80) {
                    hostName += ":" + servletRequest.getServerPort();
                }

                JsonObjectBuilder fileItem = Json.createObjectBuilder()
                        .add("name", file.getName())
                        .add("size", file.length())
                        .add("url", uriInfo.getAbsolutePath() + file.getName())
                        .add("thumbnail_url", hostName + THUMBNAIL_ENDPOINT + file.getName())
                        .add("delete_url", hostName + FILE_ENDPOINT + file.getName())
                        .add("delete_type", "DELETE");

                fileArrayBuilder.add(fileItem);
            } 
        }
        else {
            logger.debug("Upload directory not found");
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        
        return responseWithEntity(fileArrayBuilder.build(), MediaType.APPLICATION_JSON_TYPE).build();
    }
    
    @DELETE
    @Path("{filename}")
    public Response deleteFile(@PathParam("filename") String fileName) throws WebApplicationException {
        Response returnResponse = Response.status(Status.BAD_REQUEST).build();
        
        if (fileName != null) {
            File file = new File(pathForUploadedFile(fileName));
            
            if (file.exists() && !file.isDirectory()) {
                try {
                    if (file.delete()) {
                        returnResponse = defaultOkResponse().build();
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
