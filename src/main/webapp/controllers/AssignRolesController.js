myApp.controller("assignRoleController",function($scope, myFactory, $mdDialog){
	$scope.records = [];
	$scope.parentData = {
			"empId": "",
			"empName": "",
			"role": "",
			"action":""
	};
	$scope.records =[
		{"employeeId":"16209","employeeName":"Mahesh Gutam","role":"Manager"},
		{"employeeId":"16207","employeeName":"Sumith Lambu","role":"HR"},
		{"employeeId":"16112","employeeName":"Srikanth Gajula","role":"Manager"},
		{"employeeId":"16142","employeeName":"Srinivas Maneti","role":"HR"},
		{"employeeId":"16175","employeeName":"Srihari Kakasthapol","role":"Manager"}
	];
	
	var getCellTemplate = '<p class="col-lg-12"><i class="fa fa-pencil-square-o fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Update\')"></i>'+
	'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-minus-circle fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Delete\')"></i></p>';
	
	$scope.gridOptions = {
		paginationPageSizes : [ 5, 10, 15 ],
		paginationPageSize : 5,
	    pageNumber: 1,
		pageSize:5,
		columnDefs : [ 
			{field : 'employeeId',displayName: 'Employee ID'},
			{field : 'employeeName',displayName: 'Name'}, 
			{field : 'role',displayName: 'Role'}, 
			{name : 'Actions', displayName: 'Actions',cellTemplate: getCellTemplate} 
		]
	};
	$scope.gridOptions.data = $scope.records;
	
	$scope.getRowData = function(row, action){
		$scope.parentData.empId = row.entity.employeeId;
		$scope.parentData.empName = row.entity.employeeName;
		$scope.parentData.role = row.entity.role;
		if(action == "Update")
			$scope.assignRole(action, $scope.parentData);
		else if(action == "Delete")
			$scope.deleteRole(row);
	}
	
	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Got it!'));
	}
	
	$scope.assignRole = function(action, userData){
		userData.action = action;
		$mdDialog.show({
		      controller: AddRoleController,
		      templateUrl: 'templates/newRoleTemplate.html',
		      parent: angular.element(document.body),
		      clickOutsideToClose:true,
		      locals:{dataToPass: userData, gridOptionsData: $scope.gridOptions.data},
		    })
		    .then(function(result) {
		    	if(result == "Assign") showAlert('Role assigned successfully');
		    	else if(result == "Update") showAlert('Role updated successfully');
		    	else showAlert("Role assigning failed!!!");
		    });
	};
	
	$scope.cancel = function() {
	    $mdDialog.cancel();
	};
	
	$scope.deleteRole = function(row){
		//Need to implement backend logic for deleting the row from db table
	    var confirm = $mdDialog.confirm()
	          .textContent('Are you sure want to delete the role?')
	          .ok('Do it!')
	          .cancel('Cancel');
	    $mdDialog.show(confirm).then(function() {
	    	var index = $scope.gridOptions.data.indexOf(row.entity);
			$scope.gridOptions.data.splice(index, 1);
			showAlert('Role deleted successfully');
	    }, function() {
	    	
	    });
	};
	
	function AddRoleController($scope, $mdDialog, dataToPass, gridOptionsData) {
		$scope.templateTitle = dataToPass.action;
		$scope.alertMsg = "";
		if(dataToPass.action == "Assign"){
			$scope.empId = "";
			$scope.empName = "";
			$scope.empRole;
		}else if(dataToPass.action == "Update"){
			$scope.empId = dataToPass.empId;
			$scope.empName = dataToPass.empName;
			$scope.empRole = dataToPass.role;
		}
		$scope.roles = ["HR","Manager"];
		$scope.getSelectedRole = function(){
			if ($scope.empRole !== undefined) {
				return $scope.empRole;
			} else {
				return "Please select a role";
			}
		};
		
		$scope.validateEmpId = function(){
			var searchId = $scope.empId;
			if(searchId != "" && isNaN(searchId)){
				$scope.alertMsg = "Please enter only digits";
				document.getElementById('empId').focus();
			}else if(searchId != "" && ((searchId.length >0 && searchId.length <5) || searchId.length>5)){
				$scope.alertMsg = "Employee ID should be 5 digits";
				document.getElementById('empId').focus();
			}else{
				$scope.alertMsg = "";
			}
		};
		
		$scope.validateFields = function(){
			var searchId = $scope.empId;
			var empName = $scope.empName;
			var empRole = $scope.empRole;
			if(searchId == ""){
				$scope.alertMsg = "Employee ID is mandatory";
			}else if(empName == ""){
				$scope.alertMsg = "Employee Name is mandatory";
			}else if(empRole == undefined){
				$scope.alertMsg = "Please select an Employee role";
			}else{
				$scope.alertMsg = "";
				updateGrid($scope.templateTitle);
			}
		};
		
		$scope.cancel = function() {
		    $mdDialog.hide();
		};
		
		function updateGrid(action){
			//Need to implement backend functionality for updating or inserting role to role table in db
			var record = {"employeeId":$scope.empId, "employeeName": $scope.empName, "role": $scope.empRole};
			if(action == "Assign"){
				gridOptionsData.push(record);
			}else if(action == "Update"){
				var existingRecord = getRowEntity($scope.empId);
				var index = gridOptionsData.indexOf(existingRecord);
				gridOptionsData[index] = record;
			}
			$mdDialog.hide(action);
		}
		
		function getRowEntity(empId){
			for(var i=0;i<gridOptionsData.length;i++){
				var record = gridOptionsData[i];
				if(record.employeeId == empId){
					return record;
				}
			}
		}
	}
});