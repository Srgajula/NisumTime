myApp.controller("projectTeamController",function($scope, myFactory, $mdDialog, $http, appConfig, $timeout){
	$scope.records = [];
	$scope.empSearchId = "";
	
	$scope.parentData = {
			"employeeId": "",
			"employeeName": "",
			"emailId":"",
			"role": "",
			"shift": "",
			"projectId":"",
			"projectName":"",
			"managerId":"",
			"managerName":"",
			"experience":"",
			"designation":"",
			"billableStatus":"",
			"mobileNumber":"",
			"experience":"",
			"action":""
	};
	$scope.employees = [];
	$scope.projects = [];
	var getCellTemplate = '<p class="col-lg-12"><i class="fa fa-pencil-square-o fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Update\')"></i>'+
	'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-minus-circle fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Delete\')"></i></p>';
	$scope.gridOptions = {
		paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
		paginationPageSize : 10,
	    pageNumber: 1,
		pageSize:10,
		columnDefs : [ 
			{field : 'employeeId',displayName: 'Employee ID', enableColumnMenu: true, enableSorting: true, width:120},
			{field : 'employeeName',displayName: 'Name', enableColumnMenu: false, enableSorting: false},
			{field : 'emailId',displayName: 'Email', enableColumnMenu: false, enableSorting: false},
			{field : 'mobileNumber',displayName: 'Mobile No', enableColumnMenu: false, enableSorting: false, width:100}, 
			{field : 'billableStatus',displayName: 'Billability', enableColumnMenu: false, enableSorting: false}, 
			{field : 'projectName',displayName: 'Project', enableColumnMenu: false, enableSorting: false},
			{name : 'Actions', displayName: 'Actions',cellTemplate: getCellTemplate, enableColumnMenu: false, enableSorting: false, width:100} 
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
		$scope.parentData.billableStatus = row.entity.billableStatus;
		$scope.parentData.mobileNumber = row.entity.mobileNumber;
		if(action == "Update"){
			$scope.updateEmployee(action, $scope.parentData);
		}
		else if(action == "Delete")
			$scope.deleteRole(row);
	}
	
	$scope.refreshPage = function(){
		$scope.empSearchId = "";
		$scope.getUserRoles();
		$scope.getEmployeesToTeam();
	}
	
	$scope.getUserRoles = function(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "/projectTeam/getTeamDetails?employeeId="+myFactory.getEmpId()
	    }).then(function mySuccess(response) {
	        $scope.gridOptions.data = response.data;
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.gridOptions.data = [];
	    });
	};
	$scope.getEmployeesToTeam = function(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "/projectTeam/getEmployeesToTeam"
	    }).then(function mySuccess(response) {
	        $scope.employees=response.data;
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.gridOptions.data = [];
	    });
	};
	
	$scope.getProjects = function(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "/projectTeam/getProjects?employeeId="+myFactory.getEmpId()
	    }).then(function mySuccess(response) {
	        $scope.projects = response.data;
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
						'Alert Dialog').ok('Ok'));
	}
	
	$scope.assignRole = function(action, userData){
		userData.action = action;
		$mdDialog.show({
		      controller: AddProjectTeamController,
		      templateUrl: 'templates/newTeamMate.html',
		      parent: angular.element(document.body),
		      clickOutsideToClose:true,
		      locals:{dataToPass: userData, gridOptionsData: $scope.gridOptions.data, employees: $scope.employees, projects: $scope.projects},
		    })
		    .then(function(result) {
		    	if(result == "Assign") showAlert('New Teammate assigned successfully');
		    	else if(result == "Update") showAlert('Teammate updated successfully');
		    	else if(result == "Cancelled") console.log(result);
		    	else showAlert('Teammate assigning/updation failed!!!');
		    });
	};
	$scope.updateEmployee = function(action, userData){
		userData.action = action;
		$mdDialog.show({
		      controller: AddProjectTeamController,
		      templateUrl: 'templates/UpdateTeamMate.html',
		      parent: angular.element(document.body),
		      clickOutsideToClose:true,
		      locals:{dataToPass: userData, gridOptionsData: $scope.gridOptions.data, employees: $scope.employees, projects: $scope.projects},
		    })
		    .then(function(result) {
		    	if(result == "Assign") showAlert('New Teammate assigned successfully');
		    	else if(result == "Update") {
		    		$scope.refreshPage();
		    		showAlert('Teammate updated successfully');
		    	
		    	}
		    	else if(result == "Cancelled") console.log(result);
		    	else showAlert('Teammate assigning/updation failed!!!');
		    });
	};
	$scope.cancel = function() {
	    $mdDialog.hide();
	};
	
	$scope.deleteRole = function(row){
	    var confirm = $mdDialog.confirm()
	          .textContent('Are you sure you want to delete the teammate?')
	          .ok('Ok')
	          .cancel('Cancel');
	    $mdDialog.show(confirm).then(function() {
			deleteUserRole(row.entity.employeeId, row.entity.managerId);
			$timeout(function(){updateGridAfterDelete(row)},500);
	    }, function() {
	    	console.log("Cancelled dialog");
	    });
	};
	
	function deleteUserRole(empId, managerId){
		var req = {
				method : 'DELETE',
				url : appConfig.appUri+ "projectTeam/deleteTeammate?empId="+empId+"&managerId="+managerId
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
			showAlert('Teammate deleted successfully');
		}else if($scope.result == "Error"){
			showAlert('Something went wrong while deleting the role.')
		}
	}
	
	function AddProjectTeamController($scope, $mdDialog, dataToPass, gridOptionsData,employees,projects) {
		$scope.templateTitle = dataToPass.action;
		$scope.alertMsg = "";
		$scope.isDisabled = false;
		$scope.result = "";
		$scope.employeeList = employees;
		$scope.projectList = projects;
		$scope.employeeModel;
		$scope.projectModel;
		if(dataToPass.action == "Assign"){
			$scope.empId = "";
			$scope.empName = "";
			$scope.empRole;
			$scope.empShift;
			$scope.empEmail = "";
			$scope.isDisabled = false;
		}else if(dataToPass.action == "Update"){
			$scope.employeeId = dataToPass.employeeId;
			$scope.employeeName = dataToPass.employeeName;
			$scope.role = dataToPass.role;
			$scope.emailId = dataToPass.emailId;
			$scope.shift = dataToPass.shift;
			$scope.projectId = dataToPass.projectId;
			$scope.projectName = dataToPass.projectName;
			$scope.managerId = dataToPass.managerId;
			$scope.managerName = dataToPass.managerName;
			$scope.empDesignation = dataToPass.designation;
			$scope.empBillableStatus = dataToPass.billableStatus;
			$scope.mobileNumber = dataToPass.mobileNumber;
			$scope.experience = dataToPass.experience;
			$scope.isDisabled = true;
		    $scope.projectModel = {
					 'projectName': dataToPass.projectName,
					 'projectId': dataToPass.projectId
					  };
		}
		$scope.designations = ["Director","Sr. Software Engineer","Software Engineer"];
		$scope.billableStatuses = ["Billable","Shadow","Bench"];
		$scope.shifts = ["Shift 1(09:00 AM - 06:00 PM)","Shift 2(03:30 PM - 12:30 PM)", "Shift 3(09:00 PM - 06:00 AM)"];
		$scope.getSelectedDesignation = function(){
			if ($scope.empDesignation !== undefined) {
				return $scope.empDesignation;
			} else {
				return "Please select a designation";
			}
		};
		$scope.getSelectedBillableStatus = function(){
			if ($scope.empBillableStatus !== undefined) {
				return $scope.empBillableStatus;
			} else {
				return "Please select a billable status";
			}
		};
		$scope.getEmployeeSelected = function(){
			if ($scope.employeeModel !== undefined) {
				$scope.employee=$scope.employeeModel;
				return $scope.employeeModel.employeeName;
			} else {
				return "Please select a employee";
			}
		};
		$scope.getProjectSelected = function(){
			if ($scope.projectModel !== undefined) {
				$scope.project=$scope.projectModel;
				return $scope.projectModel.projectName;
			} else {
				return "Please select a project";
			}
		};
		$scope.getSelectedShift = function(){
			if ($scope.shift !== undefined) {
				return $scope.shift;
			} else {
				return "Please select a shift";
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
			}else if(searchId != "" && checkRoleExistence(searchId)){
				$scope.alertMsg = 'Employee ID is already assigned a role';
				document.getElementById('empId').focus();
			}else{
				$scope.alertMsg = "";
			}
		};
		
		function checkRoleEmpIdRange(searchId){
			return parseInt(searchId) >= appConfig.empStartId && parseInt(searchId) <= appConfig.empEndId;
		}
		
		function checkRoleExistence(searchId){
			for(var i in gridOptionsData){
				if(gridOptionsData[i].employeeId == searchId){
					return true;
				}
			}
			return false;
		}
		
		$scope.validateEmailId = function(){
			var emailId = $scope.empEmail;
			if(emailId != "" && !validateEmail(emailId)){
				$scope.alertMsg = "Please enter a valid nisum email id";
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
		
		$scope.validateFields = function(action){
			if(action == "Assign"){
				var employeeModel = $scope.employeeModel;
				var projectModel = $scope.projectModel;
				if(employeeModel == undefined){
					$scope.alertMsg = "Please select a employee";
				document.getElementById('selectEmp').focus();
				}else if(projectModel == undefined){
					$scope.alertMsg = "Please select a project";
					document.getElementById('selectProject').focus();
				} else {
					$scope.alertMsg = "";
					var record = {"employeeId":employeeModel.employeeId, "employeeName":employeeModel.employeeName, "emailId": employeeModel.emailId, "role": employeeModel.role, "shift": employeeModel.shift,"projectId":projectModel.projectId,"projectName":projectModel.projectName,"managerId":myFactory.getEmpId(),"managerName":myFactory.getEmpName()};
					addOrUpdateRole(record, $scope.templateTitle);
					$timeout(function(){updateGrid($scope.templateTitle, record)},500);
				}
			}else{
				$scope.alertMsg = "";
				var record = {"employeeId":$scope.employeeId, "employeeName":$scope.employeeName, "emailId": $scope.emailId, "role": $scope.role, "shift": $scope.shift,"projectId":$scope.projectModel.projectId,"projectName":$scope.projectModel.projectName,"managerId":myFactory.getEmpId(),"managerName":myFactory.getEmpName(),"designation":$scope.empDesignation,"billableStatus":$scope.empBillableStatus,"experience":$scope.experience,"mobileNumber":$scope.mobileNumber};
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
				urlRequest = appConfig.appUri+ "projectTeam/addEmployeeToTeam";
			}else if(action == "Update"){
				urlRequest = appConfig.appUri+ "projectTeam/updateTeammate";
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