package com.pperez.photoService.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pperez.photoService.ServiceConstants;

/**
 * Servlet implementation class UploadService
 */
@WebServlet("/UploadService")
public class UploadService extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(UploadService.class);

    private static final long serialVersionUID = 1L;

    // endpoints
    private static final String FILE_ENDPOINT =  ServiceConstants.getRESTRelativeUri(true) + "/file/";
    private static final String THUMBNAIL_ENDPOINT = ServiceConstants.getRESTRelativeUri(true) + "/thumbnail/";

    // upload settings
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

    /**
     * Default constructor.
     */
    public UploadService() {
        // TODO Auto-generated constructor stub
    }

    /**
     * A Java servlet that handles file upload from client. Upon receiving file upload submission, parses the request to
     * read upload data and saves the file on disk.
     */
    protected void processFileUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        // checks if the request actually contains upload file
        if (!ServletFileUpload.isMultipartContent(request)) {
            // if not, we stop here
            logger.warn("Aborting: Form must have enctype=multipart/form-data.");

            JsonObject jsonObj = Json.createObjectBuilder().add("error_code", "1")
                    .add("error_msg", "Form must have enctype=multipart/form-data.").build();

            PrintWriter writer = response.getWriter();
            writer.println(jsonObj.toString());
            writer.flush();

            return;
        }

        // configures upload settings
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // sets memory threshold - beyond which files are stored in disk
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // sets temporary location to store files
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        logger.debug("java.io.tmpdir: " + System.getProperty("java.io.tmpdir"));

        ServletFileUpload upload = new ServletFileUpload(factory);

        // sets maximum size of upload file
        upload.setFileSizeMax(MAX_FILE_SIZE);

        // sets maximum size of request (include file + form data)
        upload.setSizeMax(MAX_REQUEST_SIZE);

        // constructs the directory path to store upload file
        // this path is relative to application's directory
        String uploadPath = getServletContext().getRealPath("") + File.separator + ServiceConstants.UPLOAD_DIRECTORY;

        // creates the directory if it does not exist
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            logger.info("Upload directory does not exist so create it: " + uploadDir.getPath());
            uploadDir.mkdir();
        }
        else {
            logger.debug("Upload directory exists: " + uploadDir.getPath());
        }

        String errMsg = "";
        JsonArrayBuilder fileArrayBuilder = Json.createArrayBuilder();

        try {
            // parses the request's content to extract file data
            List<FileItem> formItems = upload.parseRequest(request);

            logger.debug("Now parsing file items.");

            if (formItems != null && formItems.size() > 0) {
                // iterates over form's fields
                for (FileItem item : formItems) {
                    // processes only fields that are not form fields
                    if (!item.isFormField() && item.getSize() > 0) {
                        String fileName = new File(item.getName()).getName();
                        String filePath = uploadPath + File.separator + fileName;
                        File storeFile = new File(filePath);

                        logger.debug("Now writing file: " + filePath);

                        // saves the file on disk
                        item.write(storeFile);
                        logger.info("Upload has been done successfully!");
                        String hostName = "http://" + request.getServerName();

                        if (request.getServerPort() != 80) {
                            hostName += ":" + request.getServerPort();
                        }

                        JsonObjectBuilder fileItem = Json.createObjectBuilder()
                                .add(ServiceConstants.FileListJSON.NAME, fileName)
                                .add(ServiceConstants.FileListJSON.SIZE, item.getSize())
                                .add(ServiceConstants.FileListJSON.URL, hostName + FILE_ENDPOINT + fileName)
                                .add(ServiceConstants.FileListJSON.THUMBNAIL_URL, hostName + THUMBNAIL_ENDPOINT + fileName)
                                .add(ServiceConstants.FileListJSON.DELETE_URL, hostName + FILE_ENDPOINT + fileName)
                                .add(ServiceConstants.FileListJSON.DELETE_TYPE, ServiceConstants.FileListJSON.DELETE_METHOD);

                        fileArrayBuilder.add(fileItem);
                    }
                    else {
                        errMsg = "Error: Please specify a valid file item.";
                        logger.warn("Missing or empty form field.");
                    }
                }
            }
        }
        catch (Exception ex) {
            logger.error("There was an error: " + ex.getMessage());
            ex.printStackTrace();
            errMsg = "There was an error: " + ex.getMessage();
        }

        setHeaders(response);
        PrintWriter out = response.getWriter();

        if (errMsg.length() > 0) {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("error_code", "1");
            builder.add("error_msg", errMsg);
            out.write(builder.build().toString());
        }
        else {
            response.setHeader(ServiceConstants.HTTPHeader.CONTENT_DISPOSITION, "inline; filename=\"files.json\"");
            JsonObject files = Json.createObjectBuilder().add("files", fileArrayBuilder).build();
            out.write(files.toString());
            logger.debug(files.toString());
        }

        out.flush();
    }

    protected void showUploadedFiles(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        // constructs the directory path to store upload file
        // this path is relative to application's directory
        String uploadPath = getServletContext().getRealPath("") + File.separator + ServiceConstants.UPLOAD_DIRECTORY;
        File uploadDir = new File(uploadPath);
        JsonArrayBuilder fileArrayBuilder = Json.createArrayBuilder();
        String errMsg = "";

        if (uploadDir.exists() && uploadDir.isDirectory()) {
            for (File file : uploadDir.listFiles()) {
                String hostName = "http://" + request.getServerName();

                if (request.getServerPort() != 80) {
                    hostName += ":" + request.getServerPort();
                }

                JsonObjectBuilder fileItem = Json.createObjectBuilder()
                        .add(ServiceConstants.FileListJSON.NAME, file.getName())
                        .add(ServiceConstants.FileListJSON.SIZE, file.length())
                        .add(ServiceConstants.FileListJSON.URL, hostName + FILE_ENDPOINT + file.getName())
                        .add(ServiceConstants.FileListJSON.THUMBNAIL_URL, hostName + THUMBNAIL_ENDPOINT + file.getName())
                        .add(ServiceConstants.FileListJSON.DELETE_URL, hostName + FILE_ENDPOINT + file.getName())
                        .add(ServiceConstants.FileListJSON.DELETE_TYPE, ServiceConstants.FileListJSON.DELETE_METHOD);

                fileArrayBuilder.add(fileItem);
            }
        }
        else {
            errMsg = "Upload directory does not exist yet. Please upload at least one file.";
        }
        
        setHeaders(response);
        PrintWriter out = response.getWriter();

        if (errMsg.length() > 0) {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("error_code", "1");
            builder.add("error_msg", errMsg);
            out.write(builder.build().toString());
        }
        else {
            response.setHeader(ServiceConstants.HTTPHeader.CONTENT_DISPOSITION, "inline; filename=\"files.json\"");
            JsonObject files = Json.createObjectBuilder().add("files", fileArrayBuilder).build();
            out.write(files.toString());
            logger.debug(files.toString());
        }

        out.flush();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("doGet()");
        showUploadedFiles(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        logger.debug("doPost()");
        processFileUpload(request, response);
    }

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        logger.debug("doHead()");
        setHeaders(response);
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        logger.debug("doOptions()");
        setHeaders(response);
    }

    protected void setHeaders(HttpServletResponse response) {
        response.setHeader(ServiceConstants.HTTPHeader.PRAGMA, ServiceConstants.HTTPHeader.PRAGMA_VALUE);
        response.setHeader(ServiceConstants.HTTPHeader.CACHE_CONTROL, ServiceConstants.HTTPHeader.CACHE_CONTROL_VALUE);
        response.setHeader(ServiceConstants.HTTPHeader.X_CONTENT_TYPE_OPTIONS, ServiceConstants.HTTPHeader.X_CONTENT_TYPE_OPTIONS_VALUE); // Prevent IE from MIME sniffing the content
        response.setHeader(ServiceConstants.HTTPHeader.ACCESS_CONTROL_ALLOW_ORIGIN, ServiceConstants.HTTPHeader.ACCESS_CONTROL_ALLOW_ORIGIN_VALUE); // Allow cross domain resource sharing
        response.setHeader(ServiceConstants.HTTPHeader.ACCESS_CONTROL_ALLOW_CREDENTIALS, ServiceConstants.HTTPHeader.ACCESS_CONTROL_ALLOW_CREDENTIALS_VALUE);
        response.setHeader(ServiceConstants.HTTPHeader.ACCESS_CONTROL_ALLOW_METHODS, ServiceConstants.HTTPHeader.ACCESS_CONTROL_ALLOW_METHODS_VALUE);
        response.setHeader(ServiceConstants.HTTPHeader.ACCESS_CONTROL_ALLOW_HEADERS, ServiceConstants.HTTPHeader.ACCESS_CONTROL_ALLOW_HEADERS_VALUE);
        response.setContentType(ServiceConstants.ContentType.APPLICATION_JSON);
    }
}
