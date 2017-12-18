var myApp = angular.module(
		"myTimeApp",
		[ "ngRoute", "ngCookies", "ui.grid", "ui.grid.pagination",
				"ngMaterial", "ui.bootstrap", "pdf" ]).config(
		function($mdDateLocaleProvider) {
			$mdDateLocaleProvider.formatDate = function(date) {
				var day = date.getDate();
				var month = date.getMonth() + 1;
				var year = date.getFullYear();
				return year + '-' + (month < 10 ? "0" + month : month) + '-'
						+ (day < 10 ? "0" + day : day);
			};
		});

//TODO: Replace this appUri with the domain name created
myApp.constant('appConfig', { 
			appName: "MyTime", 
			appUri: "http://localhost:8080/", 
			version:"1.0", 
			empStartId:16001, 
			empEndId:16999
		});

myApp.factory('myFactory', function() {
	var empId = "";
	var empName = "";
	var empEmailId = "";
	var empRole = "";
	var menuItems = [];
	var templateUrl = "";
	var profileUrl = "";
	
	function setEmpId(id) {
		empId = id;
	}
	function getEmpId() {
		return empId;
	}

	function setEmpName(name) {
		empName = name;
	}
	function getEmpName() {
		return empName;
	}

	function setEmpEmailId(email) {
		empEmailId = email;
	}
	function getEmpEmailId() {
		return empEmailId;
	}

	function setEmpRole(role) {
		empRole = role;
	}

	function getEmpRole() {
		return empRole;
	}

	function setMenuItems(items) {
		menuItems = items;
	}

	function getMenuItems() {
		return menuItems;
	}

	function setTemplateUrl(url) {
		templateUrl = url;
	}

	function getTemplateUrl() {
		return templateUrl;
	}
	
	function setProfileUrl(picurl) {
		profileUrl = picurl;
	}

	function getProfileUrl() {
		return profileUrl;
	}

	return {
		setEmpId : setEmpId,
		getEmpId : getEmpId,
		setEmpName : setEmpName,
		getEmpName : getEmpName,
		setEmpEmailId : setEmpEmailId,
		getEmpEmailId : getEmpEmailId,
		setEmpRole : setEmpRole,
		getEmpRole : getEmpRole,
		setMenuItems : setMenuItems,
		getMenuItems : getMenuItems,
		setTemplateUrl : setTemplateUrl,
		getTemplateUrl : getTemplateUrl,
		setProfileUrl : setProfileUrl,
		getProfileUrl : getProfileUrl
	}

});
