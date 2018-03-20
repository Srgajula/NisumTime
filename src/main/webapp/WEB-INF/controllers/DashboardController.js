
myApp.controller("dashboardController", function($scope, $http, myFactory,exportUiGridService, $mdDialog, appConfig) {
	$scope.records = [];
	$scope.parentData = {
			"projectId": "",
			"projectName": "",
			"account": "",
			"managerId":"",
			"managerName": "",
			"status": "",
			"action":""
	};
	
	/*private String employeeId;
	private String employeeName;
	private String emailId;
	private String baseTechnology;
	private String technologyKnown;
	private String alternateMobileNumber;
	private String personalEmailId;
	private Date createdOn;
	private Date lastModifiedOn;
	private String role;
	private String shift;
	private String projectId;
	private String projectName;
	private String account;
	private String managerId;
	private String managerName;
	private String experience;
	private String designation;
	private String billableStatus;
	private String mobileNumber;
	@DateTimeFormat(iso = ISO.DATE)
	private Date startDate;
	@DateTimeFormat(iso = ISO.DATE)
	private Date endDate;
	private boolean active;
	private boolean projectAssigned;
	private boolean hasB1Visa;
	private boolean hasH1Visa;
	private boolean hasPassport*/
	
	$scope.managers = [];
	var getCellActiveTemplate='<div ng-show="COL_FIELD==true"><p class="col-lg-12">Y</P></div><div ng-show="COL_FIELD==false"><p class="col-lg-12">N</p></div>';
	
	var getCellTemplate = '<p class="col-lg-12"><i class="fa fa-book fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'View\')"></i>'+
	'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-pencil-square-o fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Update\')"></i>'+
	'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-minus-circle fa-2x" aria-hidden="true" style="font-size:1.5em;margin-top:3px;cursor:pointer;" ng-click="grid.appScope.getRowData(row,\'Delete\')"></i></p>';
	var getEmpDetTemplate = '<p><i  style="margin-top:3px;color:blue;cursor:pointer" ng-click="grid.appScope.getRowData(row,\'View\')">{{COL_FIELD}}</i></p>';
	
	$scope.gridOptions = {
		paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
		paginationPageSize : 10,
	    pageNumber: 1,
		pageSize:10,
		enableFiltering: true,
		
		columnDefs : [ 
			{field : 'employeeId',displayName: 'Emp ID', enableColumnMenu: false, enableSorting: false,enableFiltering:true, width:120,cellTemplate: getEmpDetTemplate},
			{field : 'employeeName',displayName: 'EmployeeName ', enableColumnMenu: false, enableSorting: true,enableFiltering:true},
			{field : 'designation',displayName: 'Designation ', enableColumnMenu: false, enableSorting: true,enableFiltering:true},
			{field : 'baseTechnology',displayName: 'Skill ', enableColumnMenu: false, enableSorting: false,enableFiltering:true},
			{field : 'account',displayName: 'Account', enableColumnMenu: false, enableSorting: true,enableFiltering:true},
			{field : 'projectName',displayName: 'Project', enableColumnMenu: false, enableSorting: true,enableFiltering:true},
			/*{field : 'projectAssigned',displayName: 'Allocated ', enableColumnMenu: false, enableSorting: true,enableFiltering:true,cellTemplate: getCellActiveTemplate,filterHeaderTemplate: '<div class="ui-grid-filter-container" ng-repeat="colFilter in col.filters"><div my-custom-dropdown></div></div>', 
		        filter: { 
		            term: 1,
		            options: [ {id: 1, value: 'male'}, {id: 2, value: 'female'}]     // custom attribute that goes with custom directive above 
		          }, "Billable","Shadow","Bench","NA"
		          cellFilter: 'mapGender'},*/
		          { field: 'billableStatus',displayName: 'Allocation Status',
		              filterHeaderTemplate: '<div class="ui-grid-filter-container" ng-repeat="colFilter in col.filters"><div my-custom-dropdown></div></div>', 
		              filter: { 
		                term: '',
		                options: [ {id: 'Billable', value: 'Billable'}, {id: 'Shadow', value: 'Shadow'},{id: 'NA', value: 'NA'},{id: 'Bench', value: 'Bench'},{id: 'UA', value: 'Unassigned'},{id: '', value: 'All'}]     // custom attribute that goes with custom directive above 
		              }, 
		              cellFilter: 'mapGender' }
		   // {name : 'Actions', displayName: 'Actions',cellTemplate: getCellTemplate, enableColumnMenu: false, enableSorting: false, enableFiltering:false,width:130} 
		],
        enableGridMenu: true,
	    enableSelectAll: true,
	    exporterMenuExcel:false,
	    exporterMenuCsv:false,
	    exporterCsvFilename: 'AbsentDetails.csv',
	    exporterExcelFilename:'AbsentDetails',
	    exporterPdfDefaultStyle: {fontSize: 9},
	    exporterPdfTableStyle: {margin: [30, 30, 30, 30]},
	    exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
	    exporterPdfHeader: { text: "Absent Details", style: 'headerStyle' },
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
	$scope.gridOptions.data = $scope.records;
	
	$scope.getRowData = function(row, action){
		 if(action == "View")
			$scope.viewEmpDetails(action, row.entity);
	}
	$scope.viewEmpDetails = function(action, userData){
		$('#home').addClass('md-scroll-mask');
		userData.action = action;
		$mdDialog.show({
		      controller: ViewEmpController,
		      templateUrl: 'templates/employeeDetails.html',
		      parent: angular.element(document.body),
		      clickOutsideToClose:true,
		      locals:{dataToPass: userData},
		    })
		    .then(function(result) {/*
		    	if(result == "Assign") showAlert('Manager assigned successfully');
		    	else if(result == "Update") showAlert('Manager updated successfully');
		    	else if(result == "Cancelled") console.log(result);
		    	else showAlert('Manager assigning/updation failed!!!');
		    */});
	};
	$scope.getEmployeesDashBoardData = function(){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "projectTeam/getEmployeesDashBoard"
	    }).then(function mySuccess(response) {
	    	//alert("response"+response);
	     //	alert("response"+response.data);
	     	$scope.gridOptions.data = response.data;
	      }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    });
	}
	
	$scope.refreshPage = function(){
			$scope.getEmployeesDashBoardData();
	};
	
	function ViewEmpController($scope, $mdDialog,dataToPass) {
		  $scope.profile = dataToPass;
		  $scope.showOrHidePA="Show";
		  $scope.showOrHidePV="Show";
		  $scope.showOrHideBD="Show";
		    $scope.toggleProjectAllocations = function() {
		        $scope.showProjectAllocations = !$scope.showProjectAllocations;
		        if($scope.showOrHidePA=="Show"){
		        $scope.showOrHidePA="Hide";
		        }else {
		        	 $scope.showOrHidePA="Show";
		        }
		    };
		    $scope.toggleVisaDisplay = function() {
		        $scope.showVisaDisplay= !$scope.showVisaDisplay;
		        if($scope.showOrHidePV=="Show"){
			        $scope.showOrHidePV="Hide";
			        }else {
			        	 $scope.showOrHidePV="Show";
			        }
		    };
		var getCellActiveTemplate='<div ng-show="COL_FIELD==true"><p class="col-lg-12">Y</P></div><div ng-show="COL_FIELD==false"><p class="col-lg-12">N</p></div>';
		
		$scope.gridOptionsProjectAllocatons = {
			paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
			paginationPageSize : 10,
		    pageNumber: 1,
			pageSize:10,
			columnDefs : [ 
				{field : 'projectName',displayName: 'Project', enableColumnMenu: true, enableSorting: true},
				{field : 'account',displayName: 'Account', enableColumnMenu: false, enableSorting: false},
				{field : 'managerName',displayName: 'Manager Name', enableColumnMenu: false, enableSorting: false},
				{field : 'billableStatus',displayName: 'Billability', enableColumnMenu: false, enableSorting: false},
				{field : 'startDate',displayName: 'Start Date', enableColumnMenu: false, enableSorting: false},
				{field : 'endDate',displayName: 'End Date', enableColumnMenu: false, enableSorting: false},
				{field : 'active',displayName: 'Active', enableColumnMenu: false,cellTemplate:getCellActiveTemplate,enableSorting: false}
			]
		};
		$scope.gridOptionsVisaDetails = {
				paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
				paginationPageSize : 10,
			    pageNumber: 1,
				pageSize:10,
				columnDefs : [ 
					{field : 'visa',displayName: 'Visa', enableColumnMenu: true, enableSorting: true},
					{field : 'country',displayName: 'Country', enableColumnMenu: false, enableSorting: false},
					{field : 'visaNo',displayName: 'Visa No', enableColumnMenu: false, enableSorting: false},
					{field : 'visaStatus',displayName: 'Status', enableColumnMenu: false, enableSorting: false},
					{field: 'visaExpiryDate',
				        displayName: 'Expiry Date',
				        cellFilter: 'date:"dd-MMM-yyyy"'
				      },
				    {field : 'comments',displayName: 'Comments',  enableColumnMenu: false, enableSorting: false}
				]
			};
		 var getCellActiveTemplateBilling='<div ng-show="COL_FIELD==true"><p class="col-lg-12">Y</P></div><div ng-show="COL_FIELD==false"><p class="col-lg-12">N</p></div>';
			
			$scope.gridOptionsEmpBillability= {
				paginationPageSizes : [ 10, 20, 30, 40, 50, 100],
				paginationPageSize : 10,
			    pageNumber: 1,
				pageSize:10,
				enableCellEditOnFocus: true,
		        columnDefs : [ 
					{field : 'projectName',displayName: 'Project', enableColumnMenu: true, enableSorting: true},
					{field : 'account',displayName: 'Account', enableColumnMenu: false, enableSorting: false},
					{field: 'billingStartDate',
				        displayName: 'Start Date',
				        cellFilter: 'date:"dd-MMM-yyyy"'
				      },
					{
				        field: 'billingEndDate',
				        displayName: 'End Date'
				         },
					{field : 'comments',displayName: 'Comments',  enableColumnMenu: false, enableSorting: false},
					{field : 'active',displayName: 'Active',enableColumnMenu: false, enableSorting: false,cellTemplate:getCellActiveTemplateBilling,enableCellEdit: false}
					
				]
			};
			
		$scope.gridOptionsProjectAllocatons.data = $scope.dataToPass;
		$scope.gridOptionsEmpBillability.data = $scope.dataToPass;
		$scope.cancel = function() {
		    $mdDialog.hide();
		};
		 $scope.toggleBillability = function() {
		        $scope.showBillable = !$scope.showBillable;
		        if($scope.showOrHideBD=="Show"){
			        $scope.showOrHideBD="Hide";
			        }else {
			        	 $scope.showOrHideBD="Show";
			        }
		    };
	}

	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Ok'));
	}
	
}).filter('mapGender', function() {
	  var genderHash = {
			    'Billable': 'Billable',
			    'Shadow': 'Shadow',
			    'NA': 'NA',
			    'Bench': 'Bench',
			    'UA': 'Unassigned',
			    '': 'All'
			  };

			  return function(input) {
			    if (!input){
			      return '';
			    } else {
			      return genderHash[input];
			    }
			  };
			}).directive('myCustomDropdown', function() {
				  return {
					    template: '<select class="form-control" ng-model="colFilter.term" ng-options="option.id as option.value for option in colFilter.options"></select>'
					  };
					});
