/**
 * 
 */
package com.pperez.photoService.rest;

import java.io.File;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pperez.photoService.ServiceConstants;

/**
 * @author Philip Perez
 * @version Aug 10, 2014
 *          <p>
 *          BaseService.java
 *          </p>
 */
public class BaseService {
    private final Logger logger = LoggerFactory.getLogger(BaseService.class);

    // endpoints
    protected static final String THUMBNAIL_ENDPOINT = "thumbnail/";
    protected static final String FILE_ENDPOINT = "file/";

    @Context
    protected ServletContext servletContext;

    @Context
    protected HttpServletRequest servletRequest;

    @Context
    protected UriInfo uriInfo;

    protected Response returnOptions() {
        logger.debug("returnOptions()");
        return defaultOkResponse().build();
    }

    protected Response doHead() {
        logger.debug("doHead()");
        return defaultOkResponse().build();
    }

    // constructs the directory path to store upload file
    // this path is relative to application's directory
    protected String uploadDirectory() {
        String uploadPath = "";

        if (servletContext != null) {
            uploadPath = servletContext.getRealPath("") + File.separator + ServiceConstants.UPLOAD_DIRECTORY;
            File dirFile = new File(uploadPath);
            
            if (!dirFile.exists()) {
                logger.info("Upload directory does not exist so create it: " + dirFile.getPath());
                dirFile.mkdir();
            }
        } else {
            logger.debug("Servlet context is null.");
        }

        return uploadPath;
    }
    
    protected String thumbnailDirectory() {
        String thumbnailPath = "";

        if (servletContext != null) {
            thumbnailPath = servletContext.getRealPath("") + File.separator + ServiceConstants.THUMBNAIL_DIRECTORY;
            File dirFile = new File(thumbnailPath);
            
            if (!dirFile.exists()) {
                logger.info("Thumbnail directory does not exist so create it: " + dirFile.getPath());
                dirFile.mkdir();
            }
        } else {
            logger.debug("Servlet context is null.");
        }

        return thumbnailPath;
    }

    protected String pathForUploadedFile(String fileName) {
        return uploadDirectory() + File.separator + fileName;
    }
    
    protected String pathForThumbnail(String fileName) {
        return thumbnailDirectory() + File.separator + ServiceConstants.THUMB_PREFIX + fileName;
    }

    protected Response.ResponseBuilder responseWithEntity(Object entity, MediaType type) {
        Response.ResponseBuilder builder = defaultOkResponse().entity(entity);
        
        if (type != null) {
            builder.type(type);
        }
        
        return builder;
    }

    protected Response.ResponseBuilder defaultOkResponse() {
        return Response.ok().header(ServiceConstants.HTTPHeader.PRAGMA, ServiceConstants.HTTPHeader.PRAGMA_VALUE)
                .header(ServiceConstants.HTTPHeader.CACHE_CONTROL, ServiceConstants.HTTPHeader.CACHE_CONTROL_VALUE)
                .header(ServiceConstants.HTTPHeader.X_CONTENT_TYPE_OPTIONS, ServiceConstants.HTTPHeader.X_CONTENT_TYPE_OPTIONS_VALUE)
                .header(ServiceConstants.HTTPHeader.ACCESS_CONTROL_ALLOW_ORIGIN, ServiceConstants.HTTPHeader.ACCESS_CONTROL_ALLOW_ORIGIN_VALUE)
                .header(ServiceConstants.HTTPHeader.ACCESS_CONTROL_ALLOW_CREDENTIALS, ServiceConstants.HTTPHeader.ACCESS_CONTROL_ALLOW_CREDENTIALS_VALUE)
                .header(ServiceConstants.HTTPHeader.ACCESS_CONTROL_ALLOW_METHODS, ServiceConstants.HTTPHeader.ACCESS_CONTROL_ALLOW_METHODS_VALUE)
                .header(ServiceConstants.HTTPHeader.ACCESS_CONTROL_ALLOW_HEADERS, ServiceConstants.HTTPHeader.ACCESS_CONTROL_ALLOW_HEADERS_VALUE);
    }

    protected boolean inlineType(String type) {
        boolean flag = false;

        if (type != null) {
            for (String inlineType : ServiceConstants.INLINE_MEDIA_TYPES) {
                if (type.equalsIgnoreCase(inlineType)) {
                    logger.debug("Inline type detected: " + type);
                    flag = true;
                    break;
                }
            }
        }

        return flag;
    }
    
    protected JsonObjectBuilder getJsonFileBuilder(File file) {
        JsonObjectBuilder fileItem = null;

        if (file.exists() && file.isFile()) {
            fileItem = Json.createObjectBuilder()
                    .add(ServiceConstants.FileListJSON.NAME, file.getName())
                    .add(ServiceConstants.FileListJSON.SIZE, file.length())
                    .add(ServiceConstants.FileListJSON.URL,  uriInfo.getBaseUri() + FILE_ENDPOINT + file.getName())
                    .add(ServiceConstants.FileListJSON.THUMBNAIL_URL, uriInfo.getBaseUri() + THUMBNAIL_ENDPOINT + file.getName())
                    .add(ServiceConstants.FileListJSON.DELETE_URL, uriInfo.getBaseUri() + FILE_ENDPOINT + file.getName())
                    .add(ServiceConstants.FileListJSON.DELETE_TYPE, ServiceConstants.FileListJSON.DELETE_METHOD);
        }
        
        return fileItem;
    }
    
    protected JsonArray getFilesInDirectory(String path) {
        JsonArrayBuilder fileArrayBuilder = Json.createArrayBuilder();
        File directory = new File(path);
        
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (!onIgnoreList(file.getName())) {
                    fileArrayBuilder.add(getJsonFileBuilder(file));
                }
            }
        }
        
        return fileArrayBuilder.build();
    }
    
    /**
     * @param name
     * @return
     */
    private boolean onIgnoreList(String fileName) {
        boolean flag = false;
        
        for (String name : ServiceConstants.FILES_TO_IGNORE) {
            if (fileName.equalsIgnoreCase(name)) {
                flag = true;
                break;
            }
        }
        
        return flag;
    }

    protected Response getUploadFileList() {
        Response response = responseWithEntity(Json.createObjectBuilder().add(ServiceConstants.FILE_LIST_PARAM, getFilesInDirectory(uploadDirectory())).build(), MediaType.APPLICATION_JSON_TYPE).build();
        logger.debug("Files: " + response.getEntity().toString());
        return response;
    }
    
    protected boolean deleteFile(String filePath) throws WebApplicationException {
        boolean flag = false;
        
        if (filePath != null) {
            File file = new File(filePath);
            
            if (file.exists() && !file.isDirectory()) {
                try {
                    if (file.delete()) {
                        logger.debug("Successfully deleted " +  filePath);
                        flag = true;
                    }
                } catch (SecurityException ex) {
                    logger.warn(ex.getMessage());
                    throw new WebApplicationException(ex.getMessage());
                }
            } else {
                logger.warn("No such file: " + pathForUploadedFile(filePath));
                throw new WebApplicationException(Status.NOT_FOUND);
            }
        }
        
        return flag;
    }
}
