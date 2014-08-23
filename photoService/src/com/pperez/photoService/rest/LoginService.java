/**
 * 
 */
package com.pperez.photoService.rest;

import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pperez.photoService.rest.util.JsonUtilities;

/**
 * @author pperez
 * @version Aug 22, 2014 
 * <p>LoginService.java</p>
 */
@Path("/login")
@Consumes(MediaType.APPLICATION_JSON)
public class LoginService extends BaseService {
    private final Logger logger = LoggerFactory.getLogger(LoginService.class);
    protected JsonUtilities jsonUtility;
    
    public LoginService() {
        jsonUtility = new JsonUtilities();
    }
    
    @OPTIONS
    public Response returnLoginOptions() {
        logger.debug("returnLoginOptions()");
        return returnOptions();
    }
    
    @POST
    @Path("facebook")
    public Response doFaceBookLogin() {
        
        JsonObject authResponse = jsonUtility.sampleFacebookAuthResponse();
        JsonObject meResponse = jsonUtility.sampleFacebookMeResponse();
        
        
        
        return null;
    }

}
