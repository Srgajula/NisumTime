myApp.controller("myProjectAllocationsController",function($scope, myFactory, $mdDialog, $http, appConfig, $timeout){
	$scope.records = [];
	$scope.empSearchId = "";
	
	$scope.parentData = {
			"employeeId":"",
			"projectName": "",
			"account": "",
			"managerName":"",
			"billableStatus": "",
			"shift":"",
			"active": ""
	};
	$scope.employees = [];
	$scope.projects = [];
	var getCellTemplate = '<p class="col-lg-12"><i class="fa fa-pencil-square-o fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Update\')"></i>'+
	'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-minus-circle fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Delete\')"></i></p>';
	var getCellActiveTemplate='<div ng-show="COL_FIELD==true"><p class="col-lg-12">Y</P></div><div ng-show="COL_FIELD==false"><p class="col-lg-12">N</p></div>';
	
	$scope.gridOptions = {
		paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
		paginationPageSize : 10,
	    pageNumber: 1,
		pageSize:10,
		columnDefs : [ 
			{field : 'projectName',displayName: 'Project', enableColumnMenu: true, enableSorting: true},
			{field : 'account',displayName: 'Account', enableColumnMenu: false, enableSorting: false},
			{field : 'managerName',displayName: 'Manager Name', enableColumnMenu: false, enableSorting: false},
			{field : 'billableStatus',displayName: 'Billability', enableColumnMenu: false, enableSorting: false},
			{field : 'shift',displayName: 'Shift', enableColumnMenu: false, enableSorting: false},
			{field : 'active',displayName: 'Active', enableColumnMenu: false,cellTemplate:getCellActiveTemplate,enableSorting: false}
		]
	};
	$scope.gridOptions.data = $scope.records;
	
	$scope.getRowData = function(row, action){
		$scope.parentData.employeeId = row.entity.employeeId;
		$scope.parentData.employeeName = row.entity.employeeName;
		$scope.parentData.emailId = row.entity.emailId;
		$scope.parentData.role = row.entity.role;
		$scope.parentData.shift = row.entity.shift;
		$scope.parentData.projectId = row.entity.projectId;
		$scope.parentData.projectName = row.entity.projectName;
		$scope.parentData.managerId = row.entity.managerId;
		$scope.parentData.managerName = row.entity.managerName;
		$scope.parentData.experience = row.entity.experience;
		$scope.parentData.designation = row.entity.designation;
		
		if(action == "Update")
			$scope.updateEmployee(action, $scope.parentData);
		else if(action == "Delete")
			$scope.deleteRole(row);
	}
	
	$scope.refreshPage = function(){
		$scope.empSearchId = "";
		$scope.getMyProjectAllocations();
	}
	
	$scope.getMyProjectAllocations = function(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "/projectTeam/getMyProjectAllocations?employeeId="+myFactory.getEmpId()
	    }).then(function mySuccess(response) {
	        $scope.gridOptions.data = response.data;
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.gridOptions.data = [];
	    });
	};

	
	
	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Ok'));
	}
	
	});