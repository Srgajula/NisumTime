myApp.controller("leftmenuController",function($scope, myFactory, $cookies, $compile){
	$scope.empId = myFactory.getEmpId();
	$scope.empName = myFactory.getEmpName();
	$scope.empEmailId = myFactory.getEmpEmailId();
	$scope.role = myFactory.getEmpRole();
	$scope.menuItems = myFactory.getMenuItems();
	
	$scope.setTemplateUrl = function(path){
		var element = document.getElementById('main');
		path = "'"+path+"'";
		element.setAttribute("ng-include", path);
		var newTemplate = angular.element(element);
		$('#main').html(newTemplate);
		$compile($('#main'))($scope)
	}
});