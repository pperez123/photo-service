// JavaScript Document
// FB API init

var loggedIntoFB = false;

$(document).ready(function() {
  $.ajaxSetup({ cache: true });
  $.getScript('//connect.facebook.net/en_US/sdk.js', function(){
    FB.init({
		appId      : '694165110660539',
		xfbml      : true,
		version    : 'v2.1',
		cookie     : true  // enable cookies to allow the server to access the session
    });     
    $('#loginbutton,#feedbutton').removeAttr('disabled');
	
	// Now that we've initialized the JavaScript SDK, we call 
	// FB.getLoginStatus().  This function gets the state of the
	// person visiting this page and can return one of three states to
	// the callback you provide.  They can be:
	//
	// 1. Logged into your app ('connected')
	// 2. Logged into Facebook, but not your app ('not_authorized')
	// 3. Not logged into Facebook and can't tell if they are logged into
	//    your app or not.
	//
	// These three cases are handled in the callback function.
    FB.getLoginStatus(function(response) {
		statusChangeCallback(response);
	});
  });
});
	   
// This is called with the results from from FB.getLoginStatus().
function statusChangeCallback(response) {
	console.log('statusChangeCallback');
  	console.log(response);
	// The response object is returned with a status field that lets the
	// app know the current login status of the person.
	// Full docs on the response object can be found in the documentation
	// for FB.getLoginStatus().
	if (response.status === 'connected') {
		// Logged into your app and Facebook.
		loggedIntoFB = true;
		getMeAPI(response);
	} else if (response.status === 'not_authorized') {
	  	// The person is logged into Facebook, but not your app.
		console.log('Please log into this app.');
		loggedIntoFB = false;
	} else {
		// The person is not logged into Facebook, so we're not sure if
		// they are logged into this app or not.
		console.log('Please log into Facebook.');
		loggedIntoFB = false;
	}
}

// This function is called when someone finishes with the Login
// Button.  See the onlogin handler attached to it in the sample
// code below.
function checkLoginState() {
	FB.getLoginStatus(function(response) {
	  statusChangeCallback(response);
	});
	
	if (loggedIntoFB) {
		// TODO: send login data to the server
		window.location = "angularjs.html";
	}
}

// Here we run a very simple test of the Graph API after login is
  // successful.  See statusChangeCallback() for when this call is made.
function getMeAPI(authResponse) {
	console.log('Welcome!  Fetching your information.... ');
	FB.api('/me', function(response) {
		console.log(response);	
	  	console.log('Successful FB login for: ' + response.name);
        logIntoBackEnd(authResponse, response);
	});
}

function logIntoBackEnd(authResponse, meResponse) {
    console.log("Attempting to log into the back end using fb credentials...");
    
    var jqXHR = $.ajax({ 
                    url: "//localhost:8080/photoservice/webapi/login/facebook",
                    type: "POST",
                    crossdomain: true,
                    contentType: "application/json",
                    data: JSON.stringify({ "authResponse" : authResponse, "meResponse" : meResponse }), 
                    success: function( data, textStatus, jqXHR ) {
                                console.log("Successful login to the back end for: " + meResponse.name);
                                console.log(data);
                                console.log(textStatus);
            
                                // Replace deafault sign-in buttons
		                        $('#sign-in').html($('#galleries').html());
                    },
                    xhrFields: {
                        withCredentials: true
                    }
        })
        .done(function() {
            console.log("Finished login to the back end.");
        })
        .fail(function(xhr, textStatus, errorThrown) {
            console.log("Failed login to the back end.");
            console.log(xhr.responseText);
            console.log(textStatus);
        })
        .always(function() {
            console.log("Login happened.");
    });
}