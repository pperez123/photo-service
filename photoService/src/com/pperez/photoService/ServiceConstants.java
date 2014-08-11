/**
 * 
 */
package com.pperez.photoService;

/**
 * @author Philip Perez
 * Aug 10, 2014
 * ServiceContants.java
 */
public class ServiceConstants {
    // Servlet contenxt base URI
    public static final String SERVLET_CONTEXT_PATH = "photoService";
    
    // Path component under which the REST API's live
    public static final String APPLICATION_PATH = "webapi";
    
    public static String getRESTRelativeUri(boolean includeLeadingSlash) {
        String uri = SERVLET_CONTEXT_PATH + "/" + APPLICATION_PATH;
        
        if (includeLeadingSlash) {
            uri = "/" + uri;
        }
        
        return uri;
    }
    
    // location to store file uploaded
    public static final String UPLOAD_DIRECTORY = "upload";
    
    // Form file upload parameter
    public static final String UPLOAD_PARAM = "files";
    
    // Media types where "Content-Disposition" header should be "inline"
    public static final String[] INLINE_MEDIA_TYPES = { "image/png", "image/jpeg", "image/gif", "image/jpg" };
    
    public class FileListJSON {
        public static final String NAME = "name";
        public static final String URL = "url";
        public static final String SIZE = "size";
        public static final String THUMBNAIL_URL = "thumbnailUrl";
        public static final String DELETE_URL = "deleteUrl";
        public static final String DELETE_TYPE = "deleteType";
        public static final String DELETE_METHOD = "DELETE";
    }
    
    public class HTTPHeader {
        public static final String CONTENT_DISPOSITION = "Content-Disposition";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String CONTENT_LENGTH = "Content-Length";
        public static final String PRAGMA = "Pragma";
        public static final String PRAGMA_VALUE = "no-cache";
        public static final String CACHE_CONTROL = "Cache-Control";
        public static final String CACHE_CONTROL_VALUE = "no-store, no-cache, must-revalidate";
        public static final String X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
        public static final String X_CONTENT_TYPE_OPTIONS_VALUE = "nosniff";
        public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
        public static final String ACCESS_CONTROL_ALLOW_ORIGIN_VALUE = "*";
        public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
        public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS_VALUE = "true";
        public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
        public static final String ACCESS_CONTROL_ALLOW_METHODS_VALUE = "OPTIONS,HEAD,GET,POST,PUT,PATCH,DELETE";
        public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
        public static final String ACCESS_CONTROL_ALLOW_HEADERS_VALUE = "Content-Type,Content-Disposition,Content-Range";
    }
    
    public class ContentType {
        public static final String APPLICATION_JSON = "application/json";
        public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    }
}
