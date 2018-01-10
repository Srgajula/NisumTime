myApp.controller("employeesController", function($scope, $http, myFactory, $mdDialog, appConfig) {
	$scope.records = [];
	$scope.empId = myFactory.getEmpId();
	$scope.empName = myFactory.getEmpName();
	$scope.empEmailId = myFactory.getEmpEmailId();
	$scope.role = myFactory.getEmpRole();
	$scope.avgLoginHrs = "";
	$scope.isVisible = false;
	$scope.searchId="";
	
	// Date picker related code
	var today = new Date();
	var priorDt = today;
	
	$scope.maxDate = today;
	$scope.fromDate = priorDt;
	$scope.toDate = today;

	$scope.gridOptions = {
		paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
		paginationPageSize : 10,
	    pageNumber: 1,
		pageSize:10,
		columnDefs : [ 
				{field : 'employeeId',displayName: 'Employee ID', enableColumnMenu: true, enableSorting: true},
				{field : 'employeeName',displayName: 'Name', enableColumnMenu: false, enableSorting: false},
				{field : 'dateOfLogin',displayName: 'Date', enableColumnMenu: true, enableSorting: true},
				{field : 'firstLogin',displayName: 'Login Time', enableColumnMenu: false,enableSorting: false}, 
				{field : 'lastLogout',displayName: 'Logout Time', enableColumnMenu: false, enableSorting: false}, 
				{field : 'totalLoginTime',displayName: 'Total Hours(HH:MM)', enableColumnMenu: false, enableSorting: false} 
			]
	};
	$scope.gridOptions.data = [];
	
	$scope.setPageDefaults = function(){
		getData(0, getFormattedDate($scope.fromDate), getFormattedDate($scope.toDate), 'onload');
	}
	
	$scope.refreshPage = function(){
		$scope.searchId="";
		$scope.fromDate = priorDt;
		$scope.toDate = today;
		$scope.gridOptions.data = [];
		$scope.isVisible = false;
		$scope.setPageDefaults();
	};
	
	$scope.getEmployeeData = function(type){
		var searchId = $scope.searchId;
		var fromDate = getFormattedDate($scope.fromDate);
		var toDate = getFormattedDate($scope.toDate);
		if(type == "blur"){
			if(searchId != "" && isNaN(searchId)){
				showAlert('Please enter only digits');
				setFieldsEmpty();
			}else if(searchId != "" && ((searchId.length >0 && searchId.length <5) || searchId.length>5)){
				showAlert('Employee ID should be 5 digits');
				setFieldsEmpty();
			}else if(searchId != "" && !checkEmpIdRange(searchId)){
				showAlert('Employee ID should be in between '+appConfig.empStartId+' - '+appConfig.empEndId);
				setFieldsEmpty();
			}else{
				if(searchId == "") getData(0, fromDate, toDate, 'onblur');
				else getData(searchId, fromDate, toDate, 'onblur');
			}
		}else if(type == "click"){
			if(searchId == ""){
				getData(0, fromDate, toDate, 'onclick');
			}else if(searchId != "" && isNaN(searchId)){
				showAlert('Please enter only digits');
				setFieldsEmpty();
			}else if(searchId != "" && !checkEmpIdRange(searchId)){
				showAlert('Employee ID should be in between '+appConfig.empStartId+' - '+appConfig.empEndId);
				setFieldsEmpty();
			}else if(searchId != ""&& (searchId.length < 5 || searchId.length > 5)){
				showAlert('Employee ID should be 5 digits');
				setFieldsEmpty();
			}else{
				getData(searchId, fromDate, toDate, 'onclick');
			}
		}
		
	}
	
	function checkEmpIdRange(searchId){
		return parseInt(searchId) >= appConfig.empStartId && parseInt(searchId) <= appConfig.empEndId;
	}
	
	function getData(empId, fromDate, toDate, type){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "attendance/employeeLoginsBasedOnDate?empId=" + empId + "&fromDate=" + fromDate + "&toDate=" +toDate
	    }).then(function mySuccess(response) {
	    	var recs = response.data;
	    	if(recs.length == 0 && type == "onclick"){
	    		showAlert('No data available');
	    		setFieldsEmpty();
	    	}else{
	    		if(empId == 0){
	    			$scope.isVisible = false;
	    			$scope.gridOptions.data = response.data;
	    		}else{
	    			$scope.isVisible = true;
	    			$scope.avgLoginHrs = response.data[0].totalAvgTime +" Hrs";
	    			$scope.gridOptions.data = response.data;
	    		}
	    		
	    	}
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.gridOptions.data = [];
	    });
	}

	$scope.validateDates = function(dateValue, from) {
		if(from == "FromDate"){
			var toDat = $scope.toDate;
			var difference = daysBetween(dateValue, toDat);
			if(difference < 0 ){
				showAlert('From Date should not be greater than To Date');
				$scope.fromDate = priorDt;
				$scope.toDate = today;
			}else{
				$scope.fromDate = dateValue;
				$scope.toDate = toDat;
			}
		}else if(from == "ToDate"){
			var fromDat = $scope.fromDate;
			var differene = daysBetween(fromDat, dateValue);
			if(differene < 0 ){
				showAlert('To Date should not be less than From Date');
				$scope.fromDate = priorDt;
				$scope.toDate = today;
			}else{
				$scope.fromDate = fromDat;
				$scope.toDate = dateValue;
			}
		}
	};
	
	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Ok'));
	}
	
	function getFormattedDate(date){
		var day = date.getDate();
		var month = date.getMonth() + 1;
		var year = date.getFullYear();
		return year + '-' + (month < 10 ? "0" + month : month) + '-'
				+ (day < 10 ? "0" + day : day);
	}
	
	function setFieldsEmpty(){
		$scope.searchId="";
		$scope.fromDate = priorDt;
		$scope.toDate = today;
		$scope.gridOptions.data = [];
		$scope.isVisible = false;
		$scope.avgLoginHrs = "";
	}
	
	function treatAsUTC(date) {
	    var result = new Date(date);
	    result.setMinutes(result.getMinutes() - result.getTimezoneOffset());
	    return result;
	}

	function daysBetween(fromDate, toDate) {
	    var millisecondsPerDay = 24 * 60 * 60 * 1000;
	    return Math.round((treatAsUTC(toDate) - treatAsUTC(fromDate)) / millisecondsPerDay);
	}
	
});
