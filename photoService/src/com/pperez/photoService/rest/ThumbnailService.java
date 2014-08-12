/**
 * 
 */
package com.pperez.photoService.rest;

import java.io.File;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
 * @version Aug 10, 2014 
 * <p>ThumbnailService.java</p>
 */

@Path("thumbnail")
public class ThumbnailService extends BaseService {
    private final Logger logger = LoggerFactory.getLogger(ThumbnailService.class);
    
    @GET
    @Path("{filename}")
    public Response getThumbnail(@PathParam("filename") String fileName) {
        logger.debug("getThumbnail()");
        logger.debug("File name is " + fileName);
        
        FileStreamingOutput fileOutput = null;
        Response returnResponse = null;

        if (fileName != null && fileName.length() > 0) {
            File file = new File(pathForThumbnail(fileName));

            if (file.exists() && !file.isDirectory()) {
                fileOutput = new FileStreamingOutput(file);
            } else {
                logger.debug("File not found: " + pathForThumbnail(fileName));
                throw new WebApplicationException(Status.NOT_FOUND);
            }

            MediaType type = DefaultMediaTypePredictor.CommonMediaTypes.getMediaTypeFromFileName(fileName);

            returnResponse = responseWithEntity(fileOutput, type)
                    .header(ServiceConstants.HTTPHeader.CONTENT_DISPOSITION, "inline; filename=" + file.getName())
                    .header(ServiceConstants.HTTPHeader.CONTENT_LENGTH, file.length())
                    .lastModified(new Date(file.lastModified()))
                    .build();
            
            logger.debug("Headers: " + returnResponse.getHeaders());
        } else {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        return returnResponse;
    }
    
    @OPTIONS
    @Path("{filename}")
    public Response returnThumbnailOptions() {
        logger.debug("returnThumbnailOptions()");
        return returnOptions();
    }

}
