myApp.controller("loginController",function($scope,$http, myFactory, $compile){
	var menuItems = myFactory.getMenuItems();
	$scope.authenticate = function(role){
		
		//Write any login authentication logic here 
		
		//Setting default values to myFactory object so that we can use it anywhere in application
		myFactory.setEmpId("16209");
		myFactory.setEmpName("Mahesh Gutam");
		myFactory.setEmpEmailId("mgutam@nisum.com");
		myFactory.setEmpRole(role);
		if(role == "HR"){
			menuItems.push({"menu" : "My Details","icon" : "fa fa-indent fa-2x","path" : "templates/employee.html"});
			menuItems.push({"menu" : "Employee Details","icon" : "fa fa-users fa-2x","path" : "templates/employees.html"});
			menuItems.push({"menu" : "Reports","icon" : "fa fa-flag fa-2x","path" : "templates/reports.html"});
			menuItems.push({"menu" : "Assign Roles","icon" : "fa fa-universal-access fa-2x","path" : "templates/roles.html"});
		}else if(role == "Manager"){
			menuItems.push({"menu" : "My Details","icon" : "fa fa-indent fa-2x","path" : "templates/employee.html"});
			menuItems.push({"menu" : "Reportee Details","icon" : "fa fa-users fa-2x","path" : "templates/employees.html"});
		}else{
			menuItems.push({"menu" : "My Details","icon" : "fa fa-indent fa-2x","path" : "templates/employee.html"});
		}
		myFactory.setMenuItems(menuItems);
		myFactory.setTemplateUrl("templates/employee.html");
		
		$scope.goToEmploy = function(){
			$location.path("templates/employee.html");
		};
		
		//Redirecting to home page after logging in successfully
		var element = document.getElementById('home');
		var path = "'templates/home.html'";
		element.setAttribute("src", path);
		var newTemplate = angular.element(element);
		$('#home').html(newTemplate);
		$compile($('#home'))($scope)
	};
	$scope.onSignIn = function(googleUser){
		 var profile = googleUser.getBasicProfile();
	      console.log("ID: " + profile.getId()); // Don't send this directly to your server!
	      console.log('Full Name: ' + profile.getName());
	      console.log('Given Name: ' + profile.getGivenName());
	      console.log('Family Name: ' + profile.getFamilyName());
	      console.log("Image URL: " + profile.getImageUrl());
	      console.log("Email: " + profile.getEmail());

	      // The ID token you need to pass to your backend:
	      var id_token = googleUser.getAuthResponse().id_token;
	      console.log("ID Token: " + id_token);
	};
	
});