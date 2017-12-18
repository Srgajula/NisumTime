myApp.controller("assignRoleController",function($scope, myFactory, $mdDialog, $http, appConfig, $timeout){
	$scope.records = [];
	$scope.empSearchId = "";
	$scope.parentData = {
			"employeeId": "",
			"employeeName": "",
			"emailId":"",
			"role": "",
			"action":""
	};
	
	var getCellTemplate = '<p class="col-lg-12"><i class="fa fa-pencil-square-o fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Update\')"></i>'+
	'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-minus-circle fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Delete\')"></i></p>';
	
	$scope.gridOptions = {
		paginationPageSizes : [ 5, 10, 20, 30, 40, 50],
		paginationPageSize : 5,
	    pageNumber: 1,
		pageSize:5,
		columnDefs : [ 
			{field : 'employeeId',displayName: 'Employee ID', enableColumnMenu: true, enableSorting: true},
			{field : 'employeeName',displayName: 'Name', enableColumnMenu: false, enableSorting: false},
			{field : 'emailId',displayName: 'Email', enableColumnMenu: false, enableSorting: false},
			{field : 'role',displayName: 'Role', enableColumnMenu: false, enableSorting: false}, 
			{name : 'Actions', displayName: 'Actions',cellTemplate: getCellTemplate, enableColumnMenu: false, enableSorting: false} 
		]
	};
	$scope.gridOptions.data = $scope.records;
	
	$scope.getRowData = function(row, action){
		$scope.parentData.employeeId = row.entity.employeeId;
		$scope.parentData.employeeName = row.entity.employeeName;
		$scope.parentData.emailId = row.entity.emailId;
		$scope.parentData.role = row.entity.role;
		if(action == "Update")
			$scope.assignRole(action, $scope.parentData);
		else if(action == "Delete")
			$scope.deleteRole(row);
	}
	
	$scope.refreshPage = function(){
		$scope.empSearchId = "";
		$scope.getUserRoles();
	}
	
	$scope.getUserRoles = function(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "user/getUserRoles"
	    }).then(function mySuccess(response) {
	        $scope.gridOptions.data = response.data;
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.gridOptions.data = [];
	    });
	};
	
	$scope.validateEmpId = function(){
		var searchId = $scope.empSearchId;
		if(searchId !="" && isNaN(searchId)){
			showAlert('Please enter only digits');
			$scope.empSearchId = "";
			document.getElementById('empSearchId').focus();
		}else if(searchId != "" && (searchId.length < 5 || searchId.length > 5)){
			showAlert('Employee ID should be 5 digits');
			$scope.empSearchId = "";
			document.getElementById('empSearchId').focus();
		}else if(searchId != "" && !checkEmpIdRange(searchId)){
			showAlert('Employee ID should be in between '+appConfig.empStartId+' - '+appConfig.empEndId);
			$scope.empSearchId = "";
			document.getElementById('empSearchId').focus();
		}
	};
	
	function checkEmpIdRange(searchId){
		return parseInt(searchId) >= appConfig.empStartId && parseInt(searchId) <= appConfig.empEndId;
	}
	
	$scope.getEmployeeRole = function(type){
		var searchId = $scope.empSearchId;
		if(searchId =="" && searchId.length == 0){
			showAlert('Employee ID is mandatory');
			$scope.empSearchId = "";
			document.getElementById('empSearchId').focus();
		}else if(searchId != "" && !checkEmpIdRange(searchId)){
			showAlert('Employee ID should be in between '+appConfig.empStartId+' - '+appConfig.empEndId);
			$scope.empSearchId = "";
			document.getElementById('empSearchId').focus();
		}else{
			$scope.gridOptions.data = [];
			getEmployeeRoleData(searchId);
		}
	};
	
	function getEmployeeRoleData(empId){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "user/getEmployeeRoleData?empId=" + empId
	    }).then(function mySuccess(response) {
	    	if(response.data != "" && response.data.length !=0){
	    		$scope.gridOptions.data.push(response.data);
	    	}
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.refreshPage();
	    });
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
		    	else if(result == "Cancelled") console.log(result);
		    	else showAlert('Role assigning/updation failed!!!');
		    });
	};
	
	$scope.cancel = function() {
	    $mdDialog.hide();
	};
	
	$scope.deleteRole = function(row){
	    var confirm = $mdDialog.confirm()
	          .textContent('Are you sure want to delete the role?')
	          .ok('Do it!')
	          .cancel('Cancel');
	    $mdDialog.show(confirm).then(function() {
			deleteUserRole(row.entity.employeeId);
			$timeout(function(){updateGridAfterDelete(row)},500);
	    }, function() {
	    	console.log("Cancelled dialog");
	    });
	};
	
	function deleteUserRole(empId){
		var req = {
				method : 'DELETE',
				url : appConfig.appUri+ "user/deleteEmployee?empId="+empId
			}
			$http(req).then(function mySuccess(response) {
				$scope.result = response.data;
				console.log($scope.result);
			}, function myError(response){
				$scope.result = "Error";
			});
	}
	
	function updateGridAfterDelete(row){
		if($scope.result == "Success" || $scope.result == ""){
			var index = $scope.gridOptions.data.indexOf(row.entity);
			$scope.gridOptions.data.splice(index, 1);
			showAlert('Role deleted successfully');
		}else if($scope.result == "Error"){
			showAlert('Something went wrong while deleting the role.')
		}
    	
	}
	
	function AddRoleController($scope, $mdDialog, dataToPass, gridOptionsData) {
		$scope.templateTitle = dataToPass.action;
		$scope.alertMsg = "";
		$scope.isDisabled = false;
		$scope.result = "";
		if(dataToPass.action == "Assign"){
			$scope.empId = "";
			$scope.empName = "";
			$scope.empRole;
			$scope.empEmail = "";
			$scope.isDisabled = false;
		}else if(dataToPass.action == "Update"){
			$scope.empId = dataToPass.employeeId;
			$scope.empName = dataToPass.employeeName;
			$scope.empRole = dataToPass.role;
			$scope.empEmail = dataToPass.emailId;
			$scope.isDisabled = true;
		}
		$scope.roles = ["HR","Manager","Employee"];
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
			}else if(searchId != "" && !checkRoleEmpIdRange(searchId)){
				$scope.alertMsg = 'Employee ID should be in between '+appConfig.empStartId+' - '+appConfig.empEndId;
				document.getElementById('empId').focus();
			}else{
				$scope.alertMsg = "";
			}
		};
		
		function checkRoleEmpIdRange(searchId){
			return parseInt(searchId) >= appConfig.empStartId && parseInt(searchId) <= appConfig.empEndId;
		}
		
		$scope.validateEmailId = function(){
			var emailId = $scope.empEmail;
			if(emailId != "" && !validateEmail(emailId)){
				$scope.alertMsg = "Please enter a valid email id";
				document.getElementById('empEmail').focus();
			}else{
				$scope.alertMsg = "";
			}
		}
		
		function validateEmail(emailId){
			 var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
			 if(re.test(emailId)){
		        if(emailId.indexOf("@nisum.com", emailId.length - "@nisum.com".length) !== -1){
		        	return true;
		        }
			 }
			 return false;
		 }
		
		$scope.validateFields = function(){
			var searchId = $scope.empId;
			var empName = $scope.empName;
			var empRole = $scope.empRole;
			var empEmail = $scope.empEmail;
			if(searchId == ""){
				$scope.alertMsg = "Employee ID is mandatory";
				document.getElementById('empId').focus();
			}else if(searchId != "" && !checkRoleEmpIdRange(searchId)){
				$scope.alertMsg = 'Employee ID should be in between '+appConfig.empStartId+' - '+appConfig.empEndId;
				document.getElementById('empId').focus();
			}else if(empName == ""){
				$scope.alertMsg = "Employee Name is mandatory";
				document.getElementById('empName').focus();
			}else if(empEmail == ""){
				$scope.alertMsg = "Email ID is mandatory";
				document.getElementById('empEmail').focus();
			}else if(empRole == undefined){
				$scope.alertMsg = "Please select a role";
				document.getElementById('empRole').focus();
			}else{
				$scope.alertMsg = "";
				var record = {"employeeId":$scope.empId, "employeeName": $scope.empName, "emailId": $scope.empEmail, "role": $scope.empRole};
				addOrUpdateRole(record, $scope.templateTitle);
				$timeout(function(){updateGrid($scope.templateTitle, record)},500);
			}
		};
		
		$scope.cancel = function() {
		    $mdDialog.hide('Cancelled');
		};
		
		function updateGrid(action, record){
			if($scope.alertMsg == ""){
				if($scope.result == "Success"){
					if(action == "Assign"){
						gridOptionsData.push(record);
					}else if(action == "Update"){
						var existingRecord = getRowEntity($scope.empId);
						var index = gridOptionsData.indexOf(existingRecord);
						gridOptionsData[index] = record;
					}
					$mdDialog.hide(action);
				}else{
					$mdDialog.hide("Error");
				}
				
			}
		}
		
		function addOrUpdateRole(record, action){
			var urlRequest  = "";
			if(action == "Assign"){
				urlRequest = appConfig.appUri+ "user/assignEmployeeRole";
			}else if(action == "Update"){
				urlRequest = appConfig.appUri+ "user/updateEmployeeRole";
			}
			var req = {
				method : 'POST',
				url : urlRequest,
				headers : {
					"Content-type" : "application/json"
				},
				data : record
			}
			$http(req).then(function mySuccess(response) {
				$scope.result = "Success";
			}, function myError(response){
				$scope.result = "Error";
			});
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