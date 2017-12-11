//var myApp = angular.module("myApp");
myApp.controller("googleController",function($scope, $http, myFactory, $compile){
	console.log("in controller");
	
	window.onSignIn = onSignIn;
	
	function onSignIn(googleUser){
		console.log("tested");
		var profile = googleUser.getBasicProfile();
	      console.log("ID: " + profile.getId());  // Don't send this directly to your server!
	      console.log('Full Name: ' + profile.getName());
	      console.log('Given Name: ' + profile.getGivenName());
	      console.log('Family Name: ' + profile.getFamilyName());
	      console.log("Image URL: " + profile.getImageUrl());
	      console.log("Email: " + profile.getEmail());
	      
	      var emp = {};
	      emp["name"]= profile.getName();
	      emp["email"]=profile.getEmail();
	      emp["givenName"]=profile.getGivenName();
	      emp["familyName"]=profile.getFamilyName();
	      
	      $http.post("empData",emp).then(function(response){
	    	  
	      });
	};
	window.onSignIn = onSignIn;
	
});