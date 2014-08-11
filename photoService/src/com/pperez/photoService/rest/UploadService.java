/**
 * 
 */
package com.pperez.photoService.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pperez.photoService.ServiceConstants;

/**
 * @author Philip Perez
 * @version Aug 10, 2014 
 * <p>UploadService.java</p>
 */

@Path("/upload")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class UploadService extends BaseService {
    private final Logger logger = LoggerFactory.getLogger(UploadService.class);
    
    @POST
    public Response doUpload(@FormDataParam(ServiceConstants.UPLOAD_PARAM) List<FormDataBodyPart> files) throws WebApplicationException {
        JsonArrayBuilder fileArrayBuilder = Json.createArrayBuilder();
        
        for (FormDataBodyPart part : files) {
            FormDataContentDisposition disposition = part.getFormDataContentDisposition();
            
            if (disposition != null) {
                String fileName = disposition.getFileName();
                
                if (fileName != null) {
                    String filePath = pathForUploadedFile(fileName);
                    logger.debug("File path is " + filePath);
                    
                    saveFile(part.getValueAs(InputStream.class), filePath);
                    
                    File file = new File(filePath);
                    String hostPath = uriInfo.getAbsolutePath() + "/";
                    
                    if (file.exists() && file.isFile()) {
                        JsonObjectBuilder fileItem = Json.createObjectBuilder()
                                .add(ServiceConstants.FileListJSON.NAME, file.getName())
                                .add(ServiceConstants.FileListJSON.SIZE, file.length())
                                .add(ServiceConstants.FileListJSON.URL,  hostPath + file.getName())
                                .add(ServiceConstants.FileListJSON.THUMBNAIL_URL, uriInfo.getBaseUri() + THUMBNAIL_ENDPOINT + file.getName())
                                .add(ServiceConstants.FileListJSON.DELETE_URL, hostPath + file.getName())
                                .add(ServiceConstants.FileListJSON.DELETE_TYPE, ServiceConstants.FileListJSON.DELETE_METHOD);

                        fileArrayBuilder.add(fileItem);
                    }
                } else {
                    logger.error("Could not get file name.");
                    throw new WebApplicationException("Could not get file name.");
                }
            } else {
                logger.error("Could not get content disposition.");
                throw new WebApplicationException("Could not get content disposition.");
            }
            
        }
        
        return responseWithEntity(fileArrayBuilder.build(), null).build();
    }
    
    protected void saveFile(InputStream uploadedInputStream, String filePath) throws WebApplicationException {
        if (filePath != null && filePath.length() > 0 && uploadedInputStream != null) {
            try {
                int read = 0;
                byte[] bytes = new byte[1024];
                OutputStream outputStream = new FileOutputStream(new File(filePath));
                
                while ((read = uploadedInputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                logger.warn("Error saving " + filePath + ": " + e.getMessage());
                throw new WebApplicationException(e.getMessage());
            }
        }
    }
}
