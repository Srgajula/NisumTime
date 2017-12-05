myApp.controller("headerController",function($scope, myFactory, $cookies, $location, $compile){
	$scope.empId = myFactory.getEmpId();
	$scope.empName = myFactory.getEmpName();
	$scope.empEmailId = myFactory.getEmpEmailId();
	
	$scope.logout = function() {
		//Clear if any values set to factory
		var menuItems = [];
		myFactory.setEmpId("");
		myFactory.setEmpName("");
		myFactory.setEmpEmailId("");
		myFactory.setEmpRole("");
		myFactory.setMenuItems(menuItems);
		myFactory.setTemplateUrl("");
		
		var element = document.getElementById('home');
		var path = "'templates/login.html'";
		element.setAttribute("src", path);
		var newTemplate = angular.element(element);
		$compile(newTemplate)($scope);
	}
});