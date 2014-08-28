/**
 * 
 */
package com.pperez.photoService.rest;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pperez.photoService.data.PhotoServiceDAO;
import com.pperez.photoService.rest.util.JsonUtilities;

/**
 * @author pperez
 * @version Aug 22, 2014 
 * <p>LoginService.java</p>
 */
@Path("/login")
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
    
    @GET
    @Path("facebook")
    public Response doFaceBookLogin() throws WebApplicationException {
        PhotoServiceDAO dao = new PhotoServiceDAO();
        JsonObject authResponse = jsonUtility.sampleFacebookAuthResponse();
        JsonObject meResponse = jsonUtility.sampleFacebookMeResponse();
        Response response = Response.ok().build();
        
        try {
            dao.openDatabase();
            
            JsonObject auth = authResponse.getJsonObject("authResponse");
            String fbAccessToken = null;
            long fbExpiresIn = 0;
            String fbSignedRequest = null;
            String status = authResponse.getString("status");
            logger.debug("status: " + status);
            long facebookId = 0;
            
            if (auth != null) {
                fbAccessToken = auth.getString("accessToken");
                logger.debug("fbAccessToken: " + fbAccessToken);
                fbExpiresIn = auth.getJsonNumber("expiresIn").longValue();
                logger.debug("fbExpiresIn: " + fbExpiresIn);
                fbSignedRequest = auth.getString("signedRequest");
                logger.debug("fbSignedRequest: " + fbSignedRequest);
                facebookId = Long.parseLong(auth.getString("userID"));
                logger.debug("facebookId: " + facebookId); 
            } else {
                logger.warn("Could not find authResponse object.");
            }
            
            int userId = 0;
            
            if (facebookId != 0) {
                userId = dao.updateUserFacebookAuth(fbAccessToken, fbExpiresIn, fbSignedRequest, status, facebookId);
            } else {
                logger.warn("Skipping FB update since we have no Facebook ID");
            }
            
            if (userId > 0) {
                logger.debug("Successfully updated Facebook auth info. User id is " + userId);
                
                String email = meResponse.getString("email");
                logger.debug("email: " + email);
                String firstName = meResponse.getString("first_name");
                logger.debug("firstName: " + firstName);
                String lastName = meResponse.getString("last_name");
                logger.debug("lastName: " + lastName);
                
                dao.updateUserFacebookMe(facebookId, email, firstName, lastName);
                logger.debug("Successfully updated Facebook me info.");
            } 
        } catch (Exception e) {
            logger.error("Error in Facebook login: " + e.getMessage());
            e.printStackTrace();
            throw new WebApplicationException(e);
        } finally {
            dao.closeDatabase();
        }

        return response;
    }

}
