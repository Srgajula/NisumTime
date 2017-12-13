myApp.controller("employeesController", function($scope, $http, myFactory, $mdDialog, appConfig) {
	$scope.records = [];
	$scope.empId = myFactory.getEmpId();
	$scope.empName = myFactory.getEmpName();
	$scope.empEmailId = myFactory.getEmpEmailId();
	$scope.role = myFactory.getEmpRole();
	
	$scope.startDate = "";
	$scope.endDate = "";
	$scope.searchId="";
	$scope.pageTitle = "";
	
	// Date picker related code
	var today = new Date();
	var priorDt = null;
	
	if($scope.role == "HR"){
		$scope.pageTitle = "Employees Login Details";
		priorDt = today;
	}else if($scope.role == "Manager"){
		$scope.pageTitle = "Reportees Login Details";
		priorDt = new Date(today.getTime() - (30 * 24 * 60 * 60 * 1000));
	}
	$scope.maxDate = today;
	$scope.fromDate = priorDt;
	$scope.toDate = today;

	$scope.gridOptions = {
		paginationPageSizes : [ 5, 10, 15 ],
		paginationPageSize : 5,
	    pageNumber: 1,
		pageSize:5,
		columnDefs : [ 
				{field : 'employeeId',displayName: 'Employee ID'},
				{field : 'employeeName',displayName: 'Name'},
				{field : 'dateOfLogin',displayName: 'Date'},
				{field : 'firstLogin',displayName: 'Login Time'}, 
				{field : 'lastLogout',displayName: 'Logout Time'}, 
				{field : 'totalLoginTime',displayName: 'Total Hours(HH:MM)'} 
			],
		onRegisterApi: function(gridApi) {
		    gridApi.core.on.rowsRendered($scope, function(gridApi) {
		    	var length = gridApi.grid.renderContainers.body.visibleRowCache.length;
		    	if(length > 0){
		    		var firstRow = gridApi.grid.renderContainers.body.visibleRowCache[0].entity;
			    	var lastRow = gridApi.grid.renderContainers.body.visibleRowCache[length-1].entity;
			    	$scope.startDate = firstRow.dateOfLogin;
			    	$scope.endDate = lastRow.dateOfLogin;
		    	}
		    });
		}
	};
	$scope.gridOptions.data = [];
	
	$scope.setPageDefaults = function(){
		if($scope.role == "HR"){
			getData(0, getFormattedDate($scope.fromDate), getFormattedDate($scope.toDate));
		}
	}
	
	$scope.refreshPage = function(){
		$scope.startDate = "";
		$scope.endDate = "";
		$scope.searchId="";
		$scope.fromDate = priorDt;
		$scope.toDate = today;
		$scope.gridOptions.data = [];
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
			}else{
				if($scope.role == "Manager"){
					if(searchId != ""){
						getData(searchId, fromDate, toDate);
					}
				}else if($scope.role == "HR"){
					if(searchId == "") getData(0, fromDate, toDate);
					else getData(searchId, fromDate, toDate);
				}
			}
		}else if(type == "click"){
			if(searchId == ""){
				if($scope.role == "HR"){
					getData(0, fromDate, toDate);
				}else{
					showAlert('Please enter an Employee ID');
				}
			}else if(searchId != "" && isNaN(searchId)){
				showAlert('Please enter only digits');
				setFieldsEmpty();
			}else if(searchId != ""&& (searchId.length < 5 || searchId.length > 5)){
				showAlert('Employee ID should be 5 digits');
				setFieldsEmpty();
			}else{
				getData(searchId, fromDate, toDate);
			}
		}
		
	}
	
	function getData(empId, fromDate, toDate){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "attendance/employeeLoginsBasedOnDate?empId=" + empId + "&fromDate=" + fromDate + "&toDate=" +toDate
	    }).then(function mySuccess(response) {
	        $scope.gridOptions.data = response.data;
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.gridOptions.data = [];
	    });
	}

	$scope.validateDates = function(dateValue, from) {
		if($scope.role == "Manager"){
			if(from == "FromDate"){
				var toDt = $scope.toDate;
				var diff = daysBetween(dateValue, toDt);
				if(diff < 30 ){
					showAlert('Date range should have minimum of 30 days difference');
					$scope.fromDate = priorDt;
					$scope.toDate = today;
					setFieldsEmpty();
				}else{
					$scope.fromDate = dateValue;
					$scope.toDate = getCalculatedDate(dateValue, 'Add');
				}
			}else if(from == "ToDate"){
				$scope.toDate = dateValue;
				$scope.fromDate = getCalculatedDate(dateValue, 'Substract');
			}
		}
	};
	
	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Got it!'));
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
		$scope.startDate = "";
		$scope.endDate = "";
		$scope.gridOptions.data = [];
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
	
	function getCalculatedDate(selectedDate, type){
		var futureDt = null;
		if(type == "Add"){
			futureDt = new Date(selectedDate.getTime() + (30 * 24 * 60 * 60 * 1000));
		}else {
			futureDt = new Date(selectedDate.getTime() - (30 * 24 * 60 * 60 * 1000));
		}
		return futureDt;
	}
});
