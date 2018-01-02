myApp.controller("loginController",function($scope, myFactory, $compile, $window, $http, appConfig, $mdDialog, $timeout){
	var menuItems = myFactory.getMenuItems();
	$window.onSignIn = onSignIn;
	$window.onFailure = onFailure;
	$scope.rolesData = [];
	
	function onSignIn(googleUser) {
		var profile = googleUser.getBasicProfile();
		console.log('Name: ' + profile.getName());
		console.log('Email: ' + profile.getEmail()); 
		console.log('Image URL: ' + profile.getImageUrl());
		getUserRole(profile);
		getAllUserRoles();
	}
	
	function onFailure(error){
		if(error.type == "tokenFailed"){
			showAlert('Please login with @Nisum account');
		}
	}
	
	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Ok'));
	}
	
	function getUserRole(profile){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "user/employee?emailId="+profile.getEmail()
	    }).then(function mySuccess(response) {
	    	var result = response.data;
	    	if(result == "" || result.length == 0){
	    		showRegisterEmployeeScreen(profile);
	    	}else{
	    		showProgressDialog("Please wait!!! Redirecting to home page.");
	    		$timeout(function(){setDefaultValues(result, profile.getImageUrl());},2000);
	    		
	    	}
	    }, function myError(response) {
	    	showProgressDialog("Something went wrong redirecting to login page!!!");
	    	$timeout(function(){redirectToLoginPage();},2000);
	    });
	}
	
	function getAllUserRoles(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "user/getUserRoles"
	    }).then(function mySuccess(response) {
	    	$scope.rolesData = response.data;
	    }, function myError(response) {
	    	$scope.rolesData = [];
	    });
	};
	
	function showRegisterEmployeeScreen(profile){
		$mdDialog.show({
	      controller: NewEmployeeController,
	      templateUrl: 'templates/registerEmployee.html',
	      parent: angular.element(document.body),
	      clickOutsideToClose:false,
	      locals:{dataToPass: profile, rolesData:$scope.rolesData },
	    })
	    .then(function(result) {
	    	if(result == "Cancelled"){ console.log(result);
		    	showProgressDialog("Registration cancelled!!! Please wait while redirected to login page.");
		    	$timeout(function(){redirectToLoginPage();},2000);
	    	}else if(result == "Error"){
	    		showProgressDialog("Registration failed!!! Please wait while redirected to login page.");
	    		$timeout(function(){redirectToLoginPage();},2000);
	    	}else{
	    		showProgressDialog("Registered successfully!!! Please wait while redirected to home page.");
	    		console.log("Result:: "+result.data);
	    		$timeout(function(){setDefaultValues(result.data, profile.getImageUrl());},2000);
	    	}
	    });
	}
	
	function NewEmployeeController($scope, $mdDialog, dataToPass, rolesData) {
		$scope.alertMsg = "";
		$scope.empId = "";
		$scope.empName = dataToPass.getName();
		$scope.empEmail = dataToPass.getEmail();
		$scope.empShift;
		
		$scope.shifts = ["Shift 1(09:00 AM - 06:00 PM)","Shift 2(03:30 PM - 12:30 PM)", "Shift 3(09:00 PM - 06:00 AM)"];
		
		$scope.getSelectedShift = function(){
			if ($scope.empShift !== undefined) {
				return $scope.empShift;
			} else {
				return "Please select a shift";
			}
		};
		
		$scope.validateEmpId = function(){
			var searchId = $scope.empId;
			if(searchId != "" && isNaN(searchId)){
				$scope.alertMsg = "Please enter only digits";
				document.getElementById('empId').focus();
			}else if(searchId != "" && ((searchId.length >0 && searchId.length <5) || searchId.length>5)){
				$scope.alertMsg = "Employee ID should be 5 digits";
				document.getElementById('empId').focus();
			}else if(searchId != "" && !checkEmpIdRange(searchId)){
				$scope.alertMsg = 'Employee ID should be in between '+appConfig.empStartId+' - '+appConfig.empEndId;
				document.getElementById('empId').focus();
			}else if(searchId != "" && checkRoleExistence(searchId)){
				$scope.alertMsg = 'Employee is already registered';
				document.getElementById('empId').focus();
			}else{
				$scope.alertMsg = "";
			}
		};
		
		function checkEmpIdRange(searchId){
			return parseInt(searchId) >= appConfig.empStartId && parseInt(searchId) <= appConfig.empEndId;
		}
		
		function checkRoleExistence(searchId){
			for(var i in rolesData){
				if(rolesData[i].employeeId == searchId){
					return true;
				}
			}
			return false;
		}
		
		$scope.validateFields = function(){
			var searchId = $scope.empId;
			var empName = $scope.empName;
			var empShift = $scope.empShift;
			if(searchId == ""){
				$scope.alertMsg = "Employee ID is mandatory";
				document.getElementById('empId').focus();
			}else if(searchId != "" && !checkEmpIdRange(searchId)){
				$scope.alertMsg = 'Employee ID should be in between '+appConfig.empStartId+' - '+appConfig.empEndId;
				document.getElementById('empId').focus();
			}else if(searchId != "" && checkRoleExistence(searchId)){
				$scope.alertMsg = 'Employee is already registered';
				document.getElementById('empId').focus();
			}else if(empName == ""){
				$scope.alertMsg = "Employee Name is mandatory";
				document.getElementById('empName').focus();
			}else if(empShift == undefined){
				$scope.alertMsg = "Please select a shift";
				document.getElementById('empShift').focus();
			}else{
				$scope.alertMsg = "";
				var record = {"employeeId":$scope.empId, "employeeName": $scope.empName, "emailId": $scope.empEmail, "role": "Employee", "shift": $scope.empShift};
				addEmployee(record);
			}
		};
		
		function addEmployee(record){
			var urlRequest = appConfig.appUri+ "user/assignEmployeeRole";
			var req = {
				method : 'POST',
				url : urlRequest,
				headers : {
					"Content-type" : "application/json"
				},
				data : record
			}
			$http(req).then(function mySuccess(response) {
				$mdDialog.hide(response);
			}, function myError(response){
				$mdDialog.hide("Error");
			});
		}
		
		$scope.cancel = function() {
		    $mdDialog.hide('Cancelled');
		};
	}
	
	function setDefaultValues(userRole, profilePicUrl){
		$mdDialog.hide();
		menuItems = [];
		//Setting default values to myFactory object so that we can use it anywhere in application
		myFactory.setEmpId(userRole.employeeId);
		myFactory.setEmpName(userRole.employeeName);
		myFactory.setEmpEmailId(userRole.emailId);
		var role = userRole.role;
		myFactory.setEmpRole(role);
		if(role == "HR"){
			menuItems.push({"menu" : "My Details","icon" : "fa fa-indent fa-2x","path" : "templates/employee.html"});
			menuItems.push({"menu" : "Employee Details","icon" : "fa fa-users fa-2x","path" : "templates/employees.html"});
			menuItems.push({"menu" : "Reports","icon" : "fa fa-pie-chart fa-2x","path" : "templates/reports.html"});
			menuItems.push({"menu" : "Manage Employees","icon" : "fa fa-user-plus fa-2x","path" : "templates/roles.html"});
			menuItems.push({"menu" : "Manage Projects","icon" : "fa fa-tasks fa-2x","path" : "templates/projects.html"});
			menuItems.push({"menu" : "Attendance Report","icon" : "fa fa-bar-chart fa-2x","path" : "templates/attendanceReport.html"});
			menuItems.push({"menu" : "Shift Details","icon" : "fa fa-superpowers fa-2x","path" : "templates/shiftdetails.html"});
		}else if(role == "Manager"){
			menuItems.push({"menu" : "My Details","icon" : "fa fa-indent fa-2x","path" : "templates/employee.html"});
			menuItems.push({"menu" : "Reportee Details","icon" : "fa fa-users fa-2x","path" : "templates/reportees.html"});
			menuItems.push({"menu" : "Manage Team","icon" : "fa fa-sitemap fa-2x","path" : "templates/projectDetails.html"});
			menuItems.push({"menu" : "My Team","icon" : "fa fa-futbol-o fa-2x","path" : "templates/myTeam.html"});
		}else if(role == "Employee"){
			menuItems.push({"menu" : "My Details","icon" : "fa fa-indent fa-2x","path" : "templates/employee.html"});
			menuItems.push({"menu" : "My Team","icon" : "fa fa-futbol-o fa-2x","path" : "templates/myTeam.html"});
		}else if(role == "HR Manager"){
			menuItems.push({"menu" : "My Details","icon" : "fa fa-indent fa-2x","path" : "templates/employee.html"});
			menuItems.push({"menu" : "Employee Details","icon" : "fa fa-users fa-2x","path" : "templates/employees.html"});
			menuItems.push({"menu" : "Reports","icon" : "fa fa-pie-chart fa-2x","path" : "templates/reports.html"});
			menuItems.push({"menu" : "Manage Employees","icon" : "fa fa-user-plus fa-2x","path" : "templates/roles.html"});
			menuItems.push({"menu" : "Manage Team","icon" : "fa fa-sitemap fa-2x","path" : "templates/projectDetails.html"});
			menuItems.push({"menu" : "Manage Projects","icon" : "fa fa-tasks fa-2x","path" : "templates/projects.html"});
			menuItems.push({"menu" : "Attendance Report","icon" : "fa fa-bar-chart fa-2x","path" : "templates/attendanceReport.html"});
			menuItems.push({"menu" : "Shift Details","icon" : "fa fa-superpowers fa-2x","path" : "templates/shiftdetails.html"});
			menuItems.push({"menu" : "My Team","icon" : "fa fa-futbol-o fa-2x","path" : "templates/myTeam.html"});
			
		}else if(role == "Director"){
			menuItems.push({"menu" : "My Details","icon" : "fa fa-indent fa-2x","path" : "templates/employee.html"});
			menuItems.push({"menu" : "Employee Details","icon" : "fa fa-users fa-2x","path" : "templates/employees.html"});
			menuItems.push({"menu" : "Reports","icon" : "fa fa-pie-chart fa-2x","path" : "templates/reports.html"});
			menuItems.push({"menu" : "Manage Employees","icon" : "fa fa-user-plus fa-2x","path" : "templates/roles.html"});
			menuItems.push({"menu" : "Manage Projects","icon" : "fa fa-tasks fa-2x","path" : "templates/projects.html"});
			menuItems.push({"menu" : "Attendance Report","icon" : "fa fa-bar-chart fa-2x","path" : "templates/attendanceReport.html"});
			menuItems.push({"menu" : "Shift Details","icon" : "fa fa-superpowers fa-2x","path" : "templates/shiftdetails.html"});
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
		$mdDialog.hide();
		
		var auth2 = gapi.auth2.getAuthInstance();
	    auth2.signOut().then(function () {
	      console.log('User signed out.');
	    });
	    auth2.disconnect();
	    
		//Clear if any values set to factory
		var menuItems = [];
		myFactory.setEmpId("");
		myFactory.setEmpName("");
		myFactory.setEmpEmailId("");
		myFactory.setEmpRole("");
		myFactory.setMenuItems(menuItems);
		myFactory.setTemplateUrl("");
		myFactory.setProfileUrl("");
		
		var element = document.getElementById('home');
		var path = "'templates/login.html'";
		element.setAttribute("src", path);
		var newTemplate = angular.element(element);
		$('#home').html(newTemplate);
		$compile($('#home'))($scope)
	}
	
	function showProgressDialog(msg){
		$mdDialog.show({
	      templateUrl: 'templates/progressDialog.html',
	      controller: ProgressController,
	      parent: angular.element(document.body),
	      clickOutsideToClose:false,
	      locals: {dataToPass:msg}
	    });
	}
	
	function ProgressController($scope, dataToPass) {
		$scope.progressText = dataToPass;
	}
	
});