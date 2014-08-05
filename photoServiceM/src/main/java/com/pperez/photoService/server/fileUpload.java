/**
 * 
 */
package com.pperez.photoService.server;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

/**
 * @author pperez
 *
 */

@Path("fileupload")
public class fileUpload {
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public String upload() {
		return "";
	}
}
