# Media Management API

This codebase was an attempt to create a file management API to upload, store, manage, 
and download media files (photos, videos, etc). Designed to run on a Tomcat server, 
the service is built as a RESTful API on top of the Jersey (JAX-RS) java framework. 
While the project was not completed, the operations for uploading, downloading, deleting, and 
listing the media files are functional.

##Endpoints

###Upload A File

`POST /upload`

####Description

To upload a file, the client sends a POST request to the "/upload"
endpoint with the body formatted/encoded as "multipart/form-data".
The multi-part data should contain one or more files to store on
the back-end. For image files, the service also creates a 
100px width thumbnail. If the same file is uploaded more than once, the 
original does not get overwritten. Each version is stored as a unique file.

The response will be a JSON formatted list of all the files sent
from the client and successfully stored on the server.

####Source Notes
The upload service was first implemented as a servlet outside the
Jersey framework using the Apache FileUpload library:

[`com.pperez.photoService.servlet.UploadService.java`](photoService/src/com/pperez/photoService/servlet/UploadService.java)

Later on, however, the code was refactored using the Jersey 
framework to have a consistent implementation across all 
endpoints:

[`com.pperez.photoService.rest.UploadService.java`](src/com/pperez/photoService/rest/UploadService.java)

###Download A File

`GET /file/{filename}`

####URI Parameters
* filename (String) - Name of an existing file
 on the server. If not specified the
 response will be a JSON formatted list of all files in the
 repository.
 
####Errors
* 404 Not Found - This status code is returned if the file does not exist
 on the server.
 
####Description

To fetch a existing file from the repository on the
server, the client sends a GET request to "/file/" and appends
the name of the file. The response is the binary data for the 
file with a MIME type of file as determined by the name. If the 
 MIME cannot be automatically determined the type defaults 
 to "application/octet-stream".

####Source Notes

This functionality is implemented in:

[`com.pperez.photoService.rest.FileService.java`](photo-service/photoService/src/com/pperez/photoService/rest/FileService.java)

###Delete A File

`DELETE /file/{filename}`

####URI Parameters
* filename (String) - Name of an existing file
 on the server. 
 
####Errors
* 404 Not Found - This status code is returned if the file does not exist
 on the server.
 
####Description

To delete a existing file from the repository on the
server, the client sends a DELETE request to "/file/" and appends
the name of the file. Both the file and its thumbnail are deleted.
The response is a JSON formatted list of the files successfully deleted.

####Source Notes

This functionality is implemented in:

[`com.pperez.photoService.rest.FileService.java`](photo-service/photoService/src/com/pperez/photoService/rest/FileService.java)


###List of Uploaded Files

`GET /upload`

or

`GET /file`

####Description

A GET request to either "/file" or "/upload" will return a JSON formatted list of all
the files in the server's upload repository. 

####Source Notes
This functionality is implemented in:

[`com.pperez.photoService.rest.UploadService.java`](photo-service/photoService/src/com/pperez/photoService/rest/UploadService.java)

and

[`com.pperez.photoService.rest.FileService.java`](photo-service/photoService/src/com/pperez/photoService/rest/FileService.java)

###Fetching File Thumbnails

`GET /thumbnail/{filename}`

####URI Parameters
* filename (String) - Name of an existing image file
 to get the thumbnail image for.
 
####Errors
* 404 Not Found - This status code is returned if
 the file name is not specified or the file does not exist
 on the server.

 
####Description
To fetch the thumbnail for an existing image file on the
server, the client sends a GET request to "/thumbnail/" and appends
the name of the file. The response is the binary image data for the 
thumbnail file with the MIME type of the original image, such as 
"image/jpeg", "image/gif", etc.

####Source Notes

This functionality is implemented in:

[`com.pperez.photoService.rest.ThumbnailService.java`](photo-service/photoService/src/com/pperez/photoService/rest/ThumbnailService.java)

###Facebook Login

`POST /login/facebook`
 
####Description
Rudimentary support for user logins with Facebook credentials was
prototyped with this endpoint. The API expects a POST request with
a body containing the "authResponse" JSON object obtained when the
user successfully authenticates with Facebook. The authResponse
object should contain the "fbAccessToken", "fbExpiresIn", "fbSignedRequest"
and "facebookId" attributes. The service will store these fields
in the back-end db until the user logs out or their FB credentials
expire.

####Source Notes

This functionality is implemented in:

[`com.pperez.photoService.rest.LoginService.java`](photo-service/photoService/src/com/pperez/photoService/rest/LoginService.java)
