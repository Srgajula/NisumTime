myApp.controller("loginController",function($scope, myFactory, $compile, $window, $http, appConfig, $mdDialog){
	var menuItems = myFactory.getMenuItems();
	$window.onSignIn = onSignIn;
	
	function onSignIn(googleUser) {
		var profile = googleUser.getBasicProfile();
		var loggedInUser = profile.getName();
		var loggedInEmail = profile.getEmail();
		var loggedInPic = profile.getImageUrl();
		console.log('Name: ' + loggedInUser);
		console.log('Email: ' + loggedInEmail); 
		console.log('Image URL: ' + loggedInPic);
		getUserRole(loggedInEmail, loggedInPic);
	}
	
	function getUserRole(emailId, profilePicUrl){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "user/employee?emailId="+emailId
	    }).then(function mySuccess(response) {
	        setDefaultValues(response.data, profilePicUrl);
	    }, function myError(response) {
	    	showAlert("Something went wrong redirecting to login page!!!");
	    	redirectToLoginPage();
	    });
	}
	
	function setDefaultValues(userRole, profilePicUrl){
		//Setting default values to myFactory object so that we can use it anywhere in application
		myFactory.setEmpId(userRole.employeeId);
		myFactory.setEmpName(userRole.employeeName);
		myFactory.setEmpEmailId(userRole.emailId);
		var role = userRole.role;
		myFactory.setEmpRole(role);
		if(role == "HR"){
			menuItems.push({"menu" : "My Details","icon" : "fa fa-indent fa-2x","path" : "templates/employee.html"});
			menuItems.push({"menu" : "Employee Details","icon" : "fa fa-users fa-2x","path" : "templates/employees.html"});
			menuItems.push({"menu" : "Reports","icon" : "fa fa-flag fa-2x","path" : "templates/reports.html"});
			menuItems.push({"menu" : "Assign Roles","icon" : "fa fa-universal-access fa-2x","path" : "templates/roles.html"});
		}else if(role == "Manager"){
			menuItems.push({"menu" : "My Details","icon" : "fa fa-indent fa-2x","path" : "templates/employee.html"});
			menuItems.push({"menu" : "Reportee Details","icon" : "fa fa-users fa-2x","path" : "templates/employees.html"});
		}else if(role == "Employee"){
			menuItems.push({"menu" : "My Details","icon" : "fa fa-indent fa-2x","path" : "templates/employee.html"});
		}
		myFactory.setMenuItems(menuItems);
		myFactory.setTemplateUrl("templates/employee.html");
		myFactory.setProfileUrl(profilePicUrl);
		
		//Redirecting to home page after logging in successfully
		var element = document.getElementById('home');
		var path = "'templates/home.html'";
		element.setAttribute("src", path);
		var newTemplate = angular.element(element);
		$('#home').html(newTemplate);
		$compile($('#home'))($scope)
	}
	
	function redirectToLoginPage(){
		
		//Clear if any values set to factory
		var menuItems = [];
		myFactory.setEmpId("");
		myFactory.setEmpName("");
		myFactory.setEmpEmailId("");
		myFactory.setEmpRole("");
		myFactory.setMenuItems(menuItems);
		myFactory.setTemplateUrl("");
		myFactory.setProfilePicUrl("");
		
		var element = document.getElementById('home');
		var path = "'templates/login.html'";
		element.setAttribute("src", path);
		var newTemplate = angular.element(element);
		$('#home').html(newTemplate);
		$compile($('#home'))($scope)
	}
	
	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Got it!'));
	}
	
});