myApp.controller("employeeController", function($scope, $http, myFactory, $mdDialog, appConfig) {
	$scope.records = [];
	$scope.empId = myFactory.getEmpId();
	$scope.empName = myFactory.getEmpName();
	$scope.empEmailId = myFactory.getEmpEmailId();
	$scope.avgLoginHrs = "";
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
				{field : 'dateOfLogin',displayName: 'Date', enableColumnMenu: true, enableSorting: true},
				{field : 'firstLogin',displayName: 'Login Time', enableColumnMenu: false, enableSorting: false}, 
				{field : 'lastLogout',displayName: 'Logout Time', enableColumnMenu: false, enableSorting: false}, 
				{field : 'totalLoginTime',displayName: 'Total Hours(HH:MM)', enableColumnMenu: false, enableSorting: false} 
			],
		onRegisterApi: function(gridApi) {
		    gridApi.core.on.rowsRendered($scope, function(gridApi) {
		    	var length = gridApi.grid.renderContainers.body.visibleRowCache.length;
		    	if(length > 0){
		    		//Need to make this value dynamic
		    		$scope.avgLoginHrs = "07:55 Hrs";
		    	}
		    });
		}
	};
	$scope.gridOptions.data = [];
	
	$scope.getEmployeeData = function(){
		var fromDate = getFormattedDate($scope.fromDate);
		var toDate = getFormattedDate($scope.toDate);
		var empId = $scope.empId;
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
	
	$scope.refreshPage = function(){
		$scope.fromDate = priorDt;
		$scope.toDate = today;
		$scope.getEmployeeData();
	};
	
	function getFormattedDate(date){
		var day = date.getDate();
		var month = date.getMonth() + 1;
		var year = date.getFullYear();
		return year + '-' + (month < 10 ? "0" + month : month) + '-'
				+ (day < 10 ? "0" + day : day);
	}

	$scope.validateDates = function(dateValue, from) {
		if(from == "FromDate"){
			var toDt = $scope.toDate;
			var diff = daysBetween(dateValue, toDt);
			if(diff < 30 ){
				showAlert('Date range should have minimum of 30 days difference');
				$scope.fromDate = priorDt;
				$scope.toDate = today;
			}else{
				$scope.fromDate = dateValue;
				$scope.toDate = getCalculatedDate(dateValue, 'Add');
			}
		}else if(from == "ToDate"){
			$scope.toDate = dateValue;
			$scope.fromDate = getCalculatedDate(dateValue, 'Substract');
		}
	};
	
	function getCalculatedDate(selectedDate, type){
		var futureDt = null;
		if(type == "Add"){
			futureDt = new Date(selectedDate.getTime() + (30 * 24 * 60 * 60 * 1000));
		}else {
			futureDt = new Date(selectedDate.getTime() - (30 * 24 * 60 * 60 * 1000));
		}
		return futureDt;
	}
	
	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Ok'));
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
