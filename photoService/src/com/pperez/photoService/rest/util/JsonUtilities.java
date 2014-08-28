/**
 * 
 */
package com.pperez.photoService.rest.util;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * @author pperez
 *
 */
public class JsonUtilities {
    public JsonObject sampleFacebookAuthResponse() {
        return Json.createObjectBuilder()
                .add("authResponse", Json.createObjectBuilder()
                        .add("accessToken", "CAAJ3VujWHbsBAN9tGAUgzgLCvCNhCScyF4I7yPEMMepunBxQ2MLbG1nacyK28b63mhSP7QsMLlyqibVr250FGXyIA4KbXTEUnFgoUHsZBTdXcr1YMKigMJ8QZAdpDZCQXnf0scfDBsQhr3C31xtzN7RT81UblTjA1j9CLVIS1xFco4i12MYLsak34m0lZAc5ByZCvqZAvFgZCPg1y86fAgp")
                        .add("expiresIn", 5084)
                        .add("signedRequest", "Guy8o3qCoMd4fm9VMqz0q3AzUrso0lsBctFfGiQfieY.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImNvZGUiOiJBUUNrUmVlMUhHZUJDc29TQTQtcEVmcl9JYWVieFZfZGp6UTc5ZjhyZUE3dk9VWWQ3ckZQZEQyT05EYmVKT21hNjd3Z3Y3Rk14bzJNVEtvTEhIMlUwT1NOUF83R2QxbFpyT1hqcWNZclFwbmNRdTdDX2JWajkyRHdmMDNwcHNjelEya1VVV05Oc00yMkNNblBvN2ZGaUl1ZUVNbGY2ekVTZFNfdUMyNzh2V3Y0VFNqdGtOSmVkSUpOTFZqN3A5dDdVTjB0T0VjS2RMTXNLOHdyLTR2M1ViMkwxbWVBSEw2d1ljZmxrWFRjVVVuYXcwdlNxNHQ4TkxIOG10dkh2Mjh5NGRiM0VYWjMzQ3dMQ1pQODNVM2RZUHM1aDdyOXpMVWhGOGxlSFJJdkRmYjQ5Vi0xcmtYdi1Uc2ZLWWFVdXkyT0lxQjJBQjV1S2p3bkFnT0NoV1ExeHpieCIsImlzc3VlZF9hdCI6MTQwODc3MjExNiwidXNlcl9pZCI6IjEwMTUyNjU1NTAxMjM5NjQwIn0")
                        .add("userID", "10152655501239640"))
                .add("status", "connected").build();
    }
    
    public JsonObject sampleFacebookMeResponse() {
        return Json.createObjectBuilder()
                .add("email", "pperez@slipsleeve.com")
                .add("first_name", "Steve")
                .add("gender", "male")
                .add("id", "10152655501239640")
                .add("last_name", "Perez")
                .add("link", "https://www.facebook.com/app_scoped_user_id/10152655501239640/")
                .add("locale", "en_US")
                .add("name", "Philip Perez")
                .add("timezone", -7)
                .add("updated_time", "2014-04-19T17:44:34+0000")
                .add("verified", true)
                .build();
    }

}
