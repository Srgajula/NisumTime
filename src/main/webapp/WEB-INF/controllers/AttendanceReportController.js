myApp.controller("attendanceReportController", function($scope, $http, myFactory, $mdDialog, appConfig) {
	$scope.records = [];
	$scope.empId = myFactory.getEmpId();
	$scope.empName = myFactory.getEmpName();
	$scope.empEmailId = myFactory.getEmpEmailId();
	// Date picker related code
	var today = new Date();
	$scope.maxDate = today;
	$scope.reportDate = today;
	
	$scope.gridOptions = {
		paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
		paginationPageSize : 10,
	    pageNumber: 1,
		pageSize:10,
		columnDefs : [ 
				{field : 'employeeId',displayName: 'Employee Id', enableColumnMenu: false, enableSorting: true},
				{field : 'employeeName',displayName: 'Employee Name', enableColumnMenu: false, enableSorting: false},
				{field : 'ifPresent',displayName: 'Status', enableColumnMenu: false, enableSorting: false}
			]
	};
	$scope.gridOptions.data = [];
	
	$scope.getEmployeePresent = function(){
		var reportDate = getFormattedDate($scope.reportDate);
		$http({
	        method : "GET",
	        url : appConfig.appUri + "attendance/attendanciesReport/" + reportDate
	    }).then(function mySuccess(response) {
	        $scope.gridOptions.data = response.data;
	        $scope.totalPresent = response.data[0].totalPresent;
	        $scope.totalAbsent = response.data[0].totalAbsent;
	        $scope.totalEmployees = response.data[0].totalPresent + response.data[0].totalAbsent;
	        
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.gridOptions.data = [];
	    });
	}
	
	$scope.refreshPage = function(){
		$scope.searchDate = today;
		$scope.gridOptions.data = [];
		$scope.reportDate = today;
		$scope.totalPresent = "";
        $scope.totalAbsent = "";
        $scope.totalEmployees = "";
        
		
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
