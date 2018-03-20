myApp.controller("headerController",function($scope, myFactory, $compile, $mdDialog, $timeout){
	$scope.empId = myFactory.getEmpId();
	$scope.empName = myFactory.getEmpName();
	$scope.empEmailId = myFactory.getEmpEmailId();
	$scope.profilePicUrl = myFactory.getProfileUrl();
	
	$scope.logout = function() {
		showProgressDialog(); 
		
		var auth2 = gapi.auth2.getAuthInstance();
	    auth2.signOut().then(function () {
	      console.log("User signed out.");
	    });
	    
	    auth2.disconnect();
	    
		//Clear if any values set to factory
	    var menuItems = [], designations = [], accounts = [], technologies = [], shifts = [];
		myFactory.setEmpId("");
		myFactory.setEmpName("");
		myFactory.setEmpEmailId("");
		myFactory.setEmpRole("");
		myFactory.setMenuItems(menuItems);
		myFactory.setTemplateUrl("");
		myFactory.setProfileUrl("");
		myFactory.setDesignations(designations);
		myFactory.setAccounts(accounts);
		myFactory.setTechnologies(technologies);
		myFactory.setShifts(shifts);
		
		$timeout(function(){redirectToLoginPage();},2000);
		
	}
	
	function redirectToLoginPage(){
		var element = document.getElementById('home');
		var path = "'templates/login.html'";
		element.setAttribute("src", path);
		var newTemplate = angular.element(element);
		$('#home').html(newTemplate);
		$compile($('#home'))($scope);
		$mdDialog.hide();
	}
	
	function showProgressDialog(){
		$('#home').addClass('md-scroll-mask');
		$mdDialog.show({
	      templateUrl: 'templates/progressDialog.html',
	      controller: ProgressController,
	      parent: angular.element(document.body),
	      clickOutsideToClose:false
	    });
	}
	
	function ProgressController($scope) {
		$scope.progressText = "Please wait!!! Logging out from My Time.";
	}
});