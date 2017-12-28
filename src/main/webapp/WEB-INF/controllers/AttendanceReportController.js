myApp.controller("attendanceReportController", function($scope, $http, myFactory, $mdDialog, appConfig) {
	$scope.records = [];
	$scope.empId = myFactory.getEmpId();
	$scope.empName = myFactory.getEmpName();
	$scope.empEmailId = myFactory.getEmpEmailId();
	// Date picker related code
	var today = new Date();
	$scope.maxDate = today;
	$scope.searchDate = today;
	
	$scope.gridOptions = {
		paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
		paginationPageSize : 10,
	    pageNumber: 1,
		pageSize:10,
		columnDefs : [ 
				{field : 'employeeId',displayName: 'Employee Id', enableColumnMenu: false, enableSorting: false},
				{field : 'employeeName',displayName: 'Employee Name', enableColumnMenu: false, enableSorting: false},
				{field : 'status',displayName: 'Status', enableColumnMenu: false, enableSorting: false}
			]
	};
	$scope.gridOptions.data = [];
	
	$scope.getEmployeePresent = function(){
		var searchDate = getFormattedDate($scope.searchDate);
		$http({
	        method : "GET",
	        url : appConfig.appUri + "attendance/attendanciesReport?searchDate=" + searchDate
	    }).then(function mySuccess(response) {
	        $scope.gridOptions.data = response.data;
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.gridOptions.data = [];
	    });
	}
	
	$scope.refreshPage = function(){
		$scope.searchDate = today;
		$scope.gridOptions.data = [];
	};
	
	function getFormattedDate(date){
		var day = date.getDate();
		var month = date.getMonth() + 1;
		var year = date.getFullYear();
		return year + '-' + (month < 10 ? "0" + month : month) + '-'
				+ (day < 10 ? "0" + day : day);
	}

	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Ok'));
	}
});
