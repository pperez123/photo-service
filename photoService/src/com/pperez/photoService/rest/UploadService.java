/**
 * 
 */
package com.pperez.photoService.rest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
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
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
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
                            logger.debug("Saving: " + filePath);

                            saveFile(part.getValueAs(InputStream.class), filePath);
                            saveThumbnail(filePath);
                            
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
        
        JsonObject files = Json.createObjectBuilder().add(ServiceConstants.FILE_LIST_PARAM, fileArrayBuilder).build();
        return responseWithEntity(files, null).build();
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
                String msg = "Error saving " + filePath + ": " + e.getMessage();
                logger.error(msg);
                throw new WebApplicationException(msg);
            }
        }
    }
    
    protected void saveThumbnail(String filePath) {
        long startTime = System.currentTimeMillis();
        File originalImageFile = new File(filePath);
        BufferedImage originalImage = null;
        
        try {
            originalImage = ImageIO.read(originalImageFile);
        } catch (IOException e) {
            logger.warn("Could not read image: " + e.getMessage());
        } // load image
        
        // Quality indicate that the scaling implementation should do everything
        // create as nice of a result as possible , other options like speed
        // will return result as fast as possible
        // Automatic mode will calculate the resultant dimensions according
        // to image orientation .so resultant image may be size of 50*36.if you want
        // fixed size like 50*50 then use FIT_EXACT
        // other modes like FIT_TO_WIDTH..etc also available.
        
        int height = originalImage.getHeight();
        int width = originalImage.getWidth();
        double targetPercent = ServiceConstants.THUMBNAIL_MAX_WIDTH/(double)width;
        int targetWidth = (int) (width * targetPercent);
        int targetHeight = (int) (height * targetPercent);
        
        logger.debug("Original widthxheight: " + width + "x" + height);
        logger.debug("Target percent: " + targetPercent);
        logger.debug("Target widthxheight: " + targetWidth + "x" + targetHeight);
        
        
        if (originalImage != null) {
            BufferedImage thumbImg = Scalr.resize(originalImage, Method.QUALITY, Mode.FIT_EXACT, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);
            File thumbnail = new File(pathForThumbnail(originalImageFile.getName()));
            
            try {
                logger.debug("Writing thumbnail " + thumbnail);
                ImageIO.write(thumbImg, "jpg", thumbnail);
            } catch (IOException e) {
                logger.warn("Could not write out thumbnail image: " + e.getMessage());
            }
        }

        logger.debug("Thumbnail generation elapsed time: " + (System.currentTimeMillis() - startTime));
    }
}
