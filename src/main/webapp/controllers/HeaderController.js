myApp.controller("headerController",function($scope, myFactory, $compile){
	$scope.empId = myFactory.getEmpId();
	$scope.empName = myFactory.getEmpName();
	$scope.empEmailId = myFactory.getEmpEmailId();
	$scope.profilePicUrl = myFactory.getProfileUrl();
	
	$scope.logout = function() {
		
		var auth2 = gapi.auth2.getAuthInstance();
	    auth2.signOut().then(function () {
	      console.log('User signed out.');
	    });
	    
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
});