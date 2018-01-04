myApp.controller("projectController",function($scope, myFactory,exportUiGridService, $mdDialog, $http, appConfig, $timeout){
	$scope.records = [];
	$scope.empSearchId = "";
	$scope.parentData = {
			"projectId": "",
			"projectName": "",
			"managerId":"",
			"managerName": "",
			"status": "",
			"action":""
	};
	
	$scope.managers = [];
	
	var getCellTemplate = '<p class="col-lg-12"><i class="fa fa-book fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'View\')"></i>'+
	'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-pencil-square-o fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Update\')"></i>'+
	'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-minus-circle fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Delete\')"></i></p>';

	$scope.gridOptions = {
		paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
		paginationPageSize : 10,
	    pageNumber: 1,
		pageSize:10,
		columnDefs : [ 
			{field : 'projectId',displayName: 'Project ID', enableColumnMenu: true, enableSorting: true, width:120},
			{field : 'projectName',displayName: 'Project ', enableColumnMenu: false, enableSorting: false},
			{field : 'managerId',displayName: 'Manager ID ', enableColumnMenu: false, enableSorting: false},
			{field : 'managerName',displayName: 'Manager Name ', enableColumnMenu: false, enableSorting: false},
			{field : 'status',displayName: 'Status ', enableColumnMenu: false, enableSorting: false},
			{name : 'Actions', displayName: 'Actions',cellTemplate: getCellTemplate, enableColumnMenu: false, enableSorting: false, width:130} 
		]
	};
	$scope.gridOptions.data = $scope.records;
	
	$scope.getRowData = function(row, action){
		$scope.parentData.projectId = row.entity.projectId;
		$scope.parentData.projectName = row.entity.projectName;
		$scope.parentData.managerId = row.entity.managerId;
		$scope.parentData.managerName = row.entity.managerName;
		$scope.parentData.status = row.entity.status;
		if(action == "Update")
			$scope.addProject(action, $scope.parentData);
		else if(action == "Delete")
			$scope.deleteRole(row);
		else if(action == "View")
			$scope.viewTeamDetails(action, $scope.parentData);
	}
	
	$scope.refreshPage = function(){
		$scope.empSearchId = "";
		$scope.getProjects();
		$scope.getManagerDetails();
	}
	
	$scope.getProjects = function(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "project/getProjects"
	    }).then(function mySuccess(response) {
	        $scope.gridOptions.data = response.data;
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.gridOptions.data = [];
	    });
	};
	$scope.getManagerDetails = function(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "/user/getManagers"
	    }).then(function mySuccess(response) {
	        $scope.managers=response.data;
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
	
	$scope.addProject = function(action, userData){
		userData.action = action;
		$mdDialog.show({
		      controller: AddProjectController,
		      templateUrl: 'templates/newProject.html',
		      parent: angular.element(document.body),
		      clickOutsideToClose:true,
		      locals:{dataToPass: userData,gridOptionsData: $scope.gridOptions.data, managers: $scope.managers},
		    })
		    .then(function(result) {
		    	if(result == "Assign") showAlert('Project created successfully');
		    	else if(result == "Update") showAlert('Project updated successfully');
		    	else if(result == "Cancelled") console.log(result);
		    	else showAlert('Project assigning/updation failed!!!');
		    });
	};
	$scope.viewTeamDetails = function(action, userData){
		userData.action = action;
		$mdDialog.show({
		      controller: AddProjectController,
		      templateUrl: 'templates/projectTeamDetails.html',
		      parent: angular.element(document.body),
		      clickOutsideToClose:true,
		      locals:{dataToPass: userData,gridOptionsData: $scope.gridOptions.data, managers: $scope.managers},
		    })
		    .then(function(result) {
		    	if(result == "Assign") showAlert('Manager assigned successfully');
		    	else if(result == "Update") showAlert('Manager updated successfully');
		    	else if(result == "Cancelled") console.log(result);
		    	else showAlert('Manager assigning/updation failed!!!');
		    });
	};
	$scope.getUnAssignedEmployees = function(action, userData){
		userData.action = action;
		$mdDialog.show({
		      controller: AddProjectController,
		      templateUrl: 'templates/projectNotAssignedDetails.html',
		      parent: angular.element(document.body),
		      clickOutsideToClose:true,
		      locals:{dataToPass: userData,gridOptionsData: $scope.gridOptions.data, managers: $scope.managers},
		    })
		    .then(function(result) {
		    	if(result == "Assign") showAlert('Manager assigned successfully');
		    	else if(result == "Update") showAlert('Manager updated successfully');
		    	else if(result == "Cancelled") console.log(result);
		    	else showAlert('Manager assigning/updation failed!!!');
		    });
	};
	$scope.getAllocatedEmployees = function(action, userData){
		userData.action = action;
		$mdDialog.show({
		      controller: AddProjectController,
		      templateUrl: 'templates/projectAssignedDetails.html',
		      parent: angular.element(document.body),
		      clickOutsideToClose:true,
		      locals:{dataToPass: userData,gridOptionsData: $scope.gridOptions.data, managers: $scope.managers},
		    })
		    .then(function(result) {
		    	if(result == "Assign") showAlert('Manager assigned successfully');
		    	else if(result == "Update") showAlert('Manager updated successfully');
		    	else if(result == "Cancelled") console.log(result);
		    	else showAlert('Manager assigning/updation failed!!!');
		    });
	};
	$scope.cancel = function() {
	    $mdDialog.hide();
	};
	
	$scope.deleteRole = function(row){
	    var confirm = $mdDialog.confirm()
	          .textContent('Are you sure you want to delete this project?')
	          .ok('Ok')
	          .cancel('Cancel');
	    $mdDialog.show(confirm).then(function() {
			deleteUserRole(row.entity.projectId);
			$timeout(function(){updateGridAfterDelete(row)},500);
	    }, function() {
	    	console.log("Cancelled dialog");
	    });
	};
	
	function deleteUserRole(projectId){
		var req = {
				method : 'DELETE',
				url : appConfig.appUri+ "project/deleteProject?projectId="+projectId
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
			showAlert('Project deleted successfully');
		}else if($scope.result == "Error"){
			showAlert('Something went wrong while deleting the role.')
		}
    	
	}
	
	
	function AddProjectController($scope, $mdDialog, dataToPass,gridOptionsData, managers) {
		$scope.templateTitle = dataToPass.action;
		$scope.alertMsg = "";
		$scope.isDisabled = false;
		$scope.result = "";
		$scope.managerDetails = managers;
		$scope.prjctStses=["Active","Completed","On Hold","Proposed"];
		if(dataToPass.action == "Assign"){
			$scope.projectId = "";
			$scope.projectName = "";
			$scope.managerId = "";
			$scope.managerName = "";
			$scope.isDisabled = false;
	}else if(dataToPass.action == "Update"){
		$scope.projectId = dataToPass.projectId;
		$scope.projectName = dataToPass.projectName;
		$scope.managerId = dataToPass.managerId;
		$scope.managerName = dataToPass.managerName;
		$scope.projectStatus = dataToPass.status;
		$scope.managerModel = {
			 'employeeName': dataToPass.managerName,
			 'employeeId': dataToPass.managerId
			  };
		$scope.managerDetails = managers;
			$scope.isDisabled = true;
	}else if(dataToPass.action == "View"){
		$scope.projectId = dataToPass.projectId;
		$scope.projectName = dataToPass.projectName;
		$scope.managerId = dataToPass.managerId;
		$scope.managerName = dataToPass.managerName;
		$scope.projectStatus = dataToPass.status;
	    $scope.gridOptions = {
				paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
				paginationPageSize : 10,
			    pageNumber: 1,
				pageSize:10,
				columnDefs : [ 
					{field : 'employeeId',displayName: 'Emp ID', enableColumnMenu: true, enableSorting: true, width:100},
					{field : 'employeeName',displayName: 'Empl Name ', enableColumnMenu: false, enableSorting: false},
					{field : 'emailId',displayName: 'Email Id ', enableColumnMenu: false, enableSorting: false},
					//{field : 'projectName',displayName: 'Project ', enableColumnMenu: false, enableSorting: false},
					//{field : 'managerName',displayName: 'Manager ', enableColumnMenu: false, enableSorting: false},
					{field : 'experience',displayName: 'Exp', enableColumnMenu: true, enableSorting: true,width:80},
					{field : 'designation',displayName: 'Designation ', enableColumnMenu: false, enableSorting: false},
					{field : 'billableStatus',displayName: 'Billability ', enableColumnMenu: false, enableSorting: false},
				]
			};
			//$scope.gridOptions.data = $scope.records;
			$scope.isDisabled = true;
			$http({
		        method : "GET",
		        url : appConfig.appUri + "/projectTeam/getProjectDetails?projectId="+$scope.projectId
		    }).then(function mySuccess(response) {
		        //$scope.teamdetails=response.data;
		        //$scope.gridOptions.data.push(response.data);
		    	$scope.gridOptions.data = response.data;
		    }, function myError(response) {
		    	showAlert("Something went wrong while fetching data!!!");
		    	$scope.gridOptions.data = [];
		    });
	}else if(dataToPass.action == "UnAssigned"){
		//$scope.projectId = dataToPass.projectId;
		//$scope.projectName = dataToPass.projectName;
		//$scope.managerId = dataToPass.managerId;
		//$scope.managerName = dataToPass.managerName;
	  //  $scope.managerModel = dataToPass.managerModel;
		$scope.gridOptions = {
				paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
				paginationPageSize : 10,
			    pageNumber: 1,
				pageSize:10,
				columnDefs : [ 
					{field : 'employeeId',displayName: 'Emp ID', enableColumnMenu: true, enableSorting: true, width:100},
					{field : 'employeeName',displayName: 'Empl Name ', enableColumnMenu: false, enableSorting: false},
					{field : 'emailId',displayName: 'Email Id ', enableColumnMenu: false, enableSorting: false},
					//{field : 'projectName',displayName: 'Project ', enableColumnMenu: false, enableSorting: false},
					//{field : 'managerName',displayName: 'Manager ', enableColumnMenu: false, enableSorting: false},
					//{field : 'experience',displayName: 'Exp', enableColumnMenu: true, enableSorting: true,width:50},
					//{field : 'designation',displayName: 'Designation ', enableColumnMenu: false, enableSorting: false},
					//{field : 'billableStatus',displayName: 'Billability ', enableColumnMenu: false, enableSorting: false},
				]
			};
			//$scope.gridOptions.data = $scope.records;
			$scope.isDisabled = true;
			$http({
		        method : "GET",
		        url : appConfig.appUri + "/projectTeam/getUnAssignedEmployees"
		    }).then(function mySuccess(response) {
		        //$scope.teamdetails=response.data;
		        //$scope.gridOptions.data.push(response.data);
		    	$scope.gridOptions.data = response.data;
		    }, function myError(response) {
		    	showAlert("Something went wrong while fetching data!!!");
		    	$scope.gridOptions.data = [];
		    });
	}else if(dataToPass.action == "allocated"){
		//$scope.projectId = dataToPass.projectId;
		//$scope.projectName = dataToPass.projectName;
		//$scope.managerId = dataToPass.managerId;
		//$scope.managerName = dataToPass.managerName;
	  //  $scope.managerModel = dataToPass.managerModel;
		$scope.gridOptions = {
				paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
				paginationPageSize : 10,
			    pageNumber: 1,
				pageSize:10,
				columnDefs : [ 
					{field : 'employeeId',displayName: 'Emp ID', enableColumnMenu: true, enableSorting: true, width:100},
					{field : 'employeeName',displayName: 'Empl Name ', enableColumnMenu: false, enableSorting: false},
					{field : 'emailId',displayName: 'Email Id ', enableColumnMenu: false, enableSorting: false},
					{field : 'projectName',displayName: 'Project ', enableColumnMenu: false, enableSorting: false},
					{field : 'managerName',displayName: 'Manager ', enableColumnMenu: false, enableSorting: false},
					{field : 'experience',displayName: 'Exp', enableColumnMenu: true, enableSorting: true,width:50},
					{field : 'designation',displayName: 'Designation ', enableColumnMenu: false, enableSorting: false},
					{field : 'billableStatus',displayName: 'Billability ', enableColumnMenu: false, enableSorting: false},
				],
				enableGridMenu: true,
			    enableSelectAll: true,
			    exporterMenuExcel:false,
			    exporterMenuCsv:false,
			    exporterCsvFilename: 'Allocated.csv',
			    exporterExcelFilename:'AllocatedResources',
			    exporterPdfDefaultStyle: {fontSize: 9},
			    exporterPdfTableStyle: {margin: [30, 30, 30, 30]},
			    exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
			    exporterPdfHeader: { text: "Allocated Resources", style: 'headerStyle' },
			    exporterPdfFooter: function ( currentPage, pageCount ) {
			      return { text: currentPage.toString() + ' of ' + pageCount.toString(), style: 'footerStyle' };
			    },
			    exporterPdfCustomFormatter: function ( docDefinition ) {
			      docDefinition.styles.headerStyle = { fontSize: 22, bold: true };
			      docDefinition.styles.footerStyle = { fontSize: 10, bold: true };
			      return docDefinition;
			    },
			    exporterPdfOrientation: 'portrait',
			    exporterPdfPageSize: 'LETTER',
			    exporterPdfMaxGridWidth: 500,
			    exporterCsvLinkElement: angular.element(document.querySelectorAll(".custom-csv-link-location")),
			    onRegisterApi: function(gridApi){
			      $scope.gridApi = gridApi;
			    },
			            gridMenuCustomItems: [{
			                    title: 'Export all data as EXCEL',
			                    action: function ($event) {
			                        exportUiGridService.exportToExcel('sheet 1', $scope.gridApi, 'all', 'all');
			                    },
			                    order: 110
			                },
			                {
			                    title: 'Export visible data as EXCEL',
			                    action: function ($event) {
			                        exportUiGridService.exportToExcel('sheet 1', $scope.gridApi, 'visible', 'visible');
			                    },
			                    order: 111
			                }
			            ]
			};
			//$scope.gridOptions.data = $scope.records;
			$scope.isDisabled = true;
			$http({
		        method : "GET",
		        url : appConfig.appUri + "/projectTeam/getProjectAllocations"
		    }).then(function mySuccess(response) {
		        //$scope.teamdetails=response.data;
		        //$scope.gridOptions.data.push(response.data);
		    	$scope.gridOptions.data = response.data;
		    }, function myError(response) {
		    	showAlert("Something went wrong while fetching data!!!");
		    	$scope.gridOptions.data = [];
		    });
	}
		//$scope.roles = ["HR","Manager","Employee"];
		//$scope.shifts = ["Shift 1(09:00 AM - 06:00 PM)","Shift 2(03:30 PM - 12:30 PM)", "Shift 3(09:00 PM - 06:00 PM)"];
		$scope.getManagers = function(){
			if ($scope.managerModel !== undefined) {
				return $scope.managerModel.employeeName;
			} else {
				return "Please select a manager";
			}
		};
		$scope.getProjectStatus = function(){
			if ($scope.projectStatus !== undefined) {
				return $scope.projectStatus;
			} else {
				return "Please select project status";
			}
		};
//		$scope.getSelectedShift = function(){
//			if ($scope.empShift !== undefined) {
//				return $scope.empShift;
//			} else {
//				return "Please select a shift";
//			}
//		};
		
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
		
		$scope.validateFields = function(){
			var project = $scope.projectId;
			var projectName = $scope.projectName;
			var managerModel = $scope.managerModel;
			
			if(project == ""){
				$scope.alertMsg = "Project ID is mandatory";
				document.getElementById('projectId').focus();
			}else if(projectName == ""){
				$scope.alertMsg = "Project Name is mandatory";
				document.getElementById('projectName').focus();
			}
			else if(managerModel == undefined){
				$scope.alertMsg = "Please select a manager";
			}else{
				$scope.alertMsg = "";
				var record = {"projectId":$scope.projectId, "projectName": $scope.projectName, "managerId": $scope.managerModel.employeeId,  "managerName": $scope.managerModel.employeeName, "status": $scope.projectStatus};
				addOrUpdateProject(record, $scope.templateTitle);
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
						var existingRecord = getRowEntity($scope.projectId);
						var index = gridOptionsData.indexOf(existingRecord);
						gridOptionsData[index] = record;
					}
					$mdDialog.hide(action);
				}else{
					$mdDialog.hide("Error");
				}
				
			}
		}
		
		
		function addOrUpdateProject(record, action){
			var urlRequest  = "";
			if(action == "Assign"){
				urlRequest = appConfig.appUri+ "project/addProject";
			}else if(action == "Update"){
				urlRequest = appConfig.appUri+ "project/updateProject";
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
				if(record.projectId == empId){
					return record;
				}
			}
		}
	}
});