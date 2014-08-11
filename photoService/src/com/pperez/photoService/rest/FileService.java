/**
 * 
 */
package com.pperez.photoService.rest;

import java.io.File;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PathParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.file.DefaultMediaTypePredictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pperez.photoService.ServiceConstants;
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
        logger.debug("getFile()");
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

            returnResponse = responseWithEntity(fileOutput, inlineType(type.toString()) ? type : MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    .header(ServiceConstants.HTTPHeader.CONTENT_DISPOSITION, (inlineType(type.toString()) ? "inline" : "attachment") + "; filename=" + file.getName())
                    .header(ServiceConstants.HTTPHeader.CONTENT_LENGTH, file.length())
                    .lastModified(new Date(file.lastModified()))
                    .build();
            
            logger.debug("Headers: " + returnResponse.getHeaders());
        }

        return returnResponse;
    }
    
    @OPTIONS
    @Path("{filename}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response returnFileOptions() {
        logger.debug("returnFileOptions()");
        return returnOptions();
    }
    
    @GET
    public Response getFiles() throws WebApplicationException {
        logger.debug("getFiles()");
        File uploadDir = new File(uploadDirectory());
        JsonArrayBuilder fileArrayBuilder = Json.createArrayBuilder();
        
        if (uploadDir.exists() && uploadDir.isDirectory()) { 
            for (File file : uploadDir.listFiles()) {
                String hostPath = uriInfo.getAbsolutePath() + "/";

                JsonObjectBuilder fileItem = Json.createObjectBuilder()
                        .add(ServiceConstants.FileListJSON.NAME, file.getName())
                        .add(ServiceConstants.FileListJSON.SIZE, file.length())
                        .add(ServiceConstants.FileListJSON.URL,  hostPath + file.getName())
                        .add(ServiceConstants.FileListJSON.THUMBNAIL_URL, uriInfo.getBaseUri() + THUMBNAIL_ENDPOINT + file.getName())
                        .add(ServiceConstants.FileListJSON.DELETE_URL, hostPath + file.getName())
                        .add(ServiceConstants.FileListJSON.DELETE_TYPE, ServiceConstants.FileListJSON.DELETE_METHOD);

                fileArrayBuilder.add(fileItem);
            } 
        }
        else {
            logger.debug("Upload directory not found");
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        
        return responseWithEntity(fileArrayBuilder.build(), null).build();
    }
    
    @DELETE
    @Path("{filename}")
    public Response deleteFile(@PathParam("filename") String fileName) throws WebApplicationException {
        logger.debug("deleteFile()");
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
