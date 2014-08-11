/**
 * 
 */
package com.pperez.photoService.rest.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pperez.photoService.servlet.UploadService;

/**
 * @author pperez
 *
 */
public class FileStreamingOutput implements StreamingOutput {
    private final Logger logger = LoggerFactory.getLogger(FileStreamingOutput.class);
    private File file;

    public FileStreamingOutput(File file) {
        this.file = file;
    }

    @Override
    public void write(OutputStream output) throws IOException,WebApplicationException {
        FileInputStream input = new FileInputStream(file);
        
        try {
            int bytes;
            while ((bytes = input.read()) != -1) {
                output.write(bytes);
            }
        }
        catch (FileNotFoundException e) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        catch (Exception e) {
            logger.error("Error trying to stream file: " + e.getMessage());
            throw new WebApplicationException(e);
        }
        finally {
            if (output != null)
                output.close();
            if (input != null)
                input.close();
        }
    }
}
