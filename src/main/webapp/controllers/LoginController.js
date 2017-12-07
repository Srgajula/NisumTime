myApp.controller("loginController",function($scope, myFactory, $compile){
	var menuItems = myFactory.getMenuItems();
	$scope.authenticate = function(role){
		
		//Write any login authentication logic here 
		
		//Setting default values to myFactory object so that we can use it anywhere in application
		myFactory.setEmpId("12345");
		myFactory.setEmpName("Mahesh Kumar G");
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
		
		//Redirecting to home page after logging in successfully
		var element = document.getElementById('home');
		var path = "'templates/home.html'";
		element.setAttribute("src", path);
		var newTemplate = angular.element(element);
		$('#home').html(newTemplate);
		$compile($('#home'))($scope)
	}
	
});