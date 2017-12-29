myApp.controller("reporteesController", function($scope, $http, myFactory, $mdDialog, appConfig) {
	$scope.records = [];
	$scope.empId = myFactory.getEmpId();
	$scope.empName = myFactory.getEmpName();
	$scope.empEmailId = myFactory.getEmpEmailId();
	$scope.role = myFactory.getEmpRole();
	$scope.avgLoginHrs = "";
	$scope.isVisible = false;
	$scope.reportees = [];
	$scope.reporteeDetail;
	
	// Date picker related code
	var today = new Date();
	var priorDt = new Date(today.getTime() - (30 * 24 * 60 * 60 * 1000));
	
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
	
	$scope.refreshPage = function(){
		$scope.reporteeDetail = undefined;
		$scope.fromDate = priorDt;
		$scope.toDate = today;
		$scope.gridOptions.data = [];
	};
	
	$scope.getReporteesDetails = function(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "projectTeam/getTeamDetails?employeeId=" + $scope.empId
	    }).then(function mySuccess(response) {
    			$scope.reportees = response.data;
	    }, function myError(response) {
	    	$scope.reportees = [];
	    });
	};
	
	$scope.getSelectedReportee = function(){
		if ($scope.reporteeDetail !== undefined) {
			return $scope.reporteeDetail.employeeName;
		} else {
			return "Select an Employee ID";
		}
	};
	
	$scope.getEmployeeData = function(){
		var searchId = $scope.reporteeDetail;
		var fromDate = getFormattedDate($scope.fromDate);
		var toDate = getFormattedDate($scope.toDate);
		if(searchId == undefined){
			showAlert('Please select an Employee ID');
			document.getElementById('reporteeDetail').focus();
		}else{
			getData(searchId.employeeId, fromDate, toDate);
		}
		
	}
	
	function getData(empId, fromDate, toDate){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "attendance/employeeLoginsBasedOnDate?empId=" + empId + "&fromDate=" + fromDate + "&toDate=" +toDate
	    }).then(function mySuccess(response) {
	    	var recs = response.data;
	    	if(recs.length == 0){
	    		showAlert('No data available');
	    		setFieldsEmpty();
	    	}else{
	    		$scope.gridOptions.data = response.data;
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
				setFieldsEmpty();
			}else{
				$scope.fromDate = dateValue;
				$scope.toDate = toDat;
			}
		}else if(from == "ToDate"){
			var fromDat = $scope.fromDate;
			var differene = daysBetween(fromDat, dateValue);
			if(differene < 0 ){
				showAlert('To Date should not be less than From Date');
				setFieldsEmpty();
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
		$scope.reporteeDetail = undefined;
		$scope.fromDate = priorDt;
		$scope.toDate = today;
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
	
});
