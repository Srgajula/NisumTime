myApp.controller("reportsController", function($scope, $location, $filter,
		$http, myFactory, $cookies, $mdDialog, $window) {
	$scope.records = [];
	$scope.empId = myFactory.getEmpId();
	$scope.empName = myFactory.getEmpName();
	$scope.empEmailId = myFactory.getEmpEmailId();
	$scope.searchId="";
	// Date picker related code
	var today = new Date();
	var priorDt = new Date(today.getTime() - (30 * 24 * 60 * 60 * 1000));
	$scope.maxDate = today;
	$scope.fromDate = priorDt;
	$scope.toDate = today;
	$scope.records =[
		{"employeeId":"16209","employeeName":"Mahesh Gutam","dateOfLogin":"2017-10-30","firstLogin":"09:31","lastLogout":"16:54","totalLoginTime":"7:10"},
		{"employeeId":"16209","employeeName":"Mahesh Gutam","dateOfLogin":"2017-10-31","firstLogin":"09:21","lastLogout":"16:12","totalLoginTime":"7:15"},
		{"employeeId":"16209","employeeName":"Mahesh Gutam","dateOfLogin":"2017-11-01","firstLogin":"09:15","lastLogout":"16:24","totalLoginTime":"7:05"},
		{"employeeId":"16209","employeeName":"Mahesh Gutam","dateOfLogin":"2017-11-02","firstLogin":"09:01","lastLogout":"16:36","totalLoginTime":"7:51"},
		{"employeeId":"16209","employeeName":"Mahesh Gutam","dateOfLogin":"2017-11-03","firstLogin":"09:41","lastLogout":"16:37","totalLoginTime":"7:49"},
		{"employeeId":"16209","employeeName":"Mahesh Gutam","dateOfLogin":"2017-11-04","firstLogin":"09:01","lastLogout":"16:33","totalLoginTime":"7:31"},
		{"employeeId":"16209","employeeName":"Mahesh Gutam","dateOfLogin":"2017-11-05","firstLogin":"09:01","lastLogout":"16:33","totalLoginTime":"7:31"},
		{"employeeId":"16209","employeeName":"Mahesh Gutam","dateOfLogin":"2017-11-06","firstLogin":"09:08","lastLogout":"16:33","totalLoginTime":"7:31"},
		{"employeeId":"16209","employeeName":"Mahesh Gutam","dateOfLogin":"2017-12-01","firstLogin":"09:01","lastLogout":"16:36","totalLoginTime":"7:31"},
		{"employeeId":"16209","employeeName":"Mahesh Gutam","dateOfLogin":"2017-12-02","firstLogin":"09:01","lastLogout":"16:33","totalLoginTime":"7:31"},
		{"employeeId":"16209","employeeName":"Mahesh Gutam","dateOfLogin":"2017-12-03","firstLogin":"09:01","lastLogout":"16:33","totalLoginTime":"7:31"},
		{"employeeId":"16207","employeeName":"Sumith Lambu","dateOfLogin":"2017-12-03","firstLogin":"09:01","lastLogout":"16:33","totalLoginTime":"7:31"},
		{"employeeId":"16207","employeeName":"Sumith Lambu","dateOfLogin":"2017-12-02","firstLogin":"09:01","lastLogout":"16:33","totalLoginTime":"7:31"},
		{"employeeId":"16207","employeeName":"Sumith Lambu","dateOfLogin":"2017-11-10","firstLogin":"09:01","lastLogout":"16:33","totalLoginTime":"7:31"},
		{"employeeId":"16207","employeeName":"Sumith Lambu","dateOfLogin":"2017-11-11","firstLogin":"09:01","lastLogout":"16:33","totalLoginTime":"7:31"},
		{"employeeId":"16142","employeeName":"Srinivas Maneti","dateOfLogin":"2017-11-08","firstLogin":"09:01","lastLogout":"16:33","totalLoginTime":"7:31"},
		{"employeeId":"16142","employeeName":"Srinivas Maneti","dateOfLogin":"2017-11-09","firstLogin":"09:01","lastLogout":"16:33","totalLoginTime":"7:31"},
		{"employeeId":"16142","employeeName":"Srinivas Maneti","dateOfLogin":"2017-11-10","firstLogin":"09:01","lastLogout":"16:33","totalLoginTime":"7:31"}
	];
	
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
	
	function showAlert(message) {
		$mdDialog.show(
				$mdDialog.alert()
				.parent(angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true)
				.textContent(message)
				.ariaLabel('Alert Dialog')
				.ok('Got it!'));
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
	
	function getCalculatedDate(selectedDate, type){
		var futureDt = null;
		if(type == "Add"){
			futureDt = new Date(selectedDate.getTime() + (30 * 24 * 60 * 60 * 1000));
		}else {
			futureDt = new Date(selectedDate.getTime() - (30 * 24 * 60 * 60 * 1000));
		}
		return futureDt;
	}
	

	$scope.pdfUrl = 'reports/sample.pdf';
	$scope.scroll = 0;
	$scope.loading = 'loading';

	$scope.getNavStyle = function(scroll) {
		if (scroll > 100)
			return 'pdf-controls fixed';
		else
			return 'pdf-controls';
	}

	$scope.onError = function(error) {
		console.log(error);
	}

	$scope.onLoad = function() {
		$scope.loading = '';
	}

	$scope.onProgress = function(progressData) {
	};
	
	$scope.print = function(){
		//Need to do
//		var binaryData = [];
//		binaryData.push($scope.pdfData);
//		var pdfUrl = $window.URL.createObjectURL(new Blob(binaryData, {type: "application/pdf"}))
//        var printwWindow = $window.open(pdfUrl);
//        printwWindow.print();
	}
	
});
