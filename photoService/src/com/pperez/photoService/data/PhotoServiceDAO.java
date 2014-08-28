/**
 * 
 */
package com.pperez.photoService.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pperez
 * @version Aug 22, 2014
 * <p>PhotoServiceDAO.java</p>
 */
public class PhotoServiceDAO extends MySQLAccess {
    private final Logger logger = LoggerFactory.getLogger(PhotoServiceDAO.class);
    
    public int updateUserFacebookAuth(String fbAccessToken, long fbExpiresIn, String fbSignedRequest, String status, long facebookId) throws Exception {
        logger.debug("updateUserFacebookAuth");
        int result = 0;
        
        if (connection == null) {
            logger.error("No database connection");
            return result;
        }
        
        String sql = "INSERT INTO users (fb_access_token, fb_expires_in, fb_signed_request, fb_status, fb_user_id, user_type) VALUES(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE "
                + "fb_access_token = VALUES(fb_access_token), fb_expires_in = VALUES(fb_expires_in), fb_signed_request = VALUES(fb_signed_request), fb_status = "
                + "VALUES(fb_status), user_type = VALUES(user_type), user_id = LAST_INSERT_ID(user_id)";
        
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        
        try {
            pStmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setString(1, fbAccessToken);
            pStmt.setLong(2, fbExpiresIn);
            pStmt.setString(3, fbSignedRequest);
            pStmt.setString(4,  status);
            pStmt.setLong(5, facebookId);
            pStmt.setString(6, "facebook");
            pStmt.executeUpdate();
            
            rs = pStmt.getGeneratedKeys();

            if (rs.next()){
                result = rs.getInt(1);
            }
            
            logger.debug("User id is " + result);
        } catch (Exception e) {
            logger.error("Error updating Facebook data " + e.getMessage());
            throw new Exception(e);
        } finally {
            closePreparedStatement(pStmt);
            closeResultSet(rs);
        }
        
        return result;
    }
    
    public int updateUserFacebookMe(long facebookId, String email, String firstName, String lastName) throws Exception {
        int result = 0;
        
        if (connection == null) {
            logger.error("No database connection");
            return result;
        }
        
        String sql = "UPDATE users SET email = ?, first_name = ?, last_name = ? WHERE fb_user_id = ?";
        
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        
        try {
            pStmt = connection.prepareStatement(sql);
            pStmt.setString(1, email);
            pStmt.setString(2, firstName);
            pStmt.setString(3, lastName);
            pStmt.setLong(4, facebookId);
            result = pStmt.executeUpdate();
        } catch (Exception e) {
            logger.error("Error updating Facebook data " + e.getMessage());
            throw new Exception(e);
        } finally {
            closePreparedStatement(pStmt);
        }
        
        return result;
    }
}
