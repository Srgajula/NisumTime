myApp.controller("attendanceReportController", function($scope, $http, myFactory,exportUiGridService, $mdDialog, appConfig,$timeout) {
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
	$scope.gridOptions.data = [];
	
	$scope.getEmployeePresent = function(type){
		if(type == "onload"){
			showProgressDialog("Fetching data please wait...");
		}
		else if(type == "onclick" && $scope.reportDate < today){
			showProgressDialog("Fetching data please wait...");
		}else{
			console.log("");
		}
		var reportDate = getFormattedDate($scope.reportDate);
		$http({
	        method : "GET",
	        url : appConfig.appUri + "attendance/attendanciesReport/" + reportDate
	    }).then(function mySuccess(response) {
	    	$mdDialog.hide();
	    	if(response.data.length == 0){
	    		$timeout(function(){showAlert('No data available');},600);
	    		$scope.refreshPage();
	    	}else{
	    		$scope.gridOptions.data = response.data;
		        $scope.totalPresent = response.data[0].totalPresent;
		        $scope.totalAbsent = response.data[0].totalAbsent;
	    	}
	    }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	    	$scope.refreshPage();
	    });
	};
	
	$scope.setSearchDate = function(dateValue){
		$scope.reportDate = dateValue;
	};
	
	$scope.refreshPage = function(){
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
	
	function showProgressDialog(msg){
		$mdDialog.show({
	      templateUrl: 'templates/progressDialog.html',
	      controller: ProgressController,
	      parent: angular.element(document.body),
	      clickOutsideToClose:false,
	      locals: {dataToPass:msg}
	    });
	}
	
	function ProgressController($scope, dataToPass) {
		$scope.progressText = dataToPass;
	}
});
