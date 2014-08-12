/**
 * 
 */
package com.pperez.photoService.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
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
    public Response doUpload(FormDataMultiPart formParams) throws WebApplicationException {
        JsonArrayBuilder fileArrayBuilder = Json.createArrayBuilder();
        Map<String, List<FormDataBodyPart>> allFields = formParams.getFields();
        
        for (String key : allFields.keySet()) {
            logger.debug("Key is " + key);
            List<FormDataBodyPart> files = allFields.get(key);
            
            if (files != null && files.size() > 0) {
                for (FormDataBodyPart part : files) {
                    FormDataContentDisposition disposition = part.getFormDataContentDisposition();

                    if (disposition != null) {
                        String fileName = disposition.getFileName();

                        if (fileName != null) {
                            String filePath = pathForUploadedFile(fileName);
                            logger.debug("File path is " + filePath);

                            saveFile(part.getValueAs(InputStream.class), filePath);

                            File file = new File(filePath);

                            if (file.exists() && file.isFile()) {
                                fileArrayBuilder.add(getJsonFileBuilder(file));
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
            } else {
                logger.warn("No files to upload");
            }
        }
        
        return responseWithEntity(fileArrayBuilder.build(), null).build();
    }
    
    @GET
    public Response getFileList() {
        logger.debug("getFileList()");
        return getUploadFileList();
    }
    
    @OPTIONS
    public Response returnUploadOptions() {
        logger.debug("returnUploadOptions()");
        return returnOptions();
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
