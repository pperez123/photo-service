/**
 * 
 */
package com.pperez.photoService.rest.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

/**
 * @author pperez
 *
 */
public class FileStreamingOutput implements StreamingOutput {
    private File file;

    public FileStreamingOutput(File file) {
        this.file = file;
    }

    @Override
    public void write(OutputStream output) throws IOException,
            WebApplicationException {
        FileInputStream input = new FileInputStream(file);
        try {
            int bytes;
            while ((bytes = input.read()) != -1) {
                output.write(bytes);
            }
        }
        catch (Exception e) {
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
