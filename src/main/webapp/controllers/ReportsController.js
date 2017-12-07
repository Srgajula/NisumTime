myApp.controller("reportsController", function($scope, $http, myFactory, $mdDialog, appConfig) {
	$scope.records = [];
	$scope.searchId="";
	// Date picker related code
	var today = new Date();
	var priorDt = new Date(today.getTime() - (30 * 24 * 60 * 60 * 1000));
	$scope.maxDate = today;
	$scope.fromDate = priorDt;
	$scope.toDate = today;
	$scope.reportMsg ="Please generate a report for preview.";
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
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Got it!'));
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
	
	$scope.pdfUrl = "";
	$scope.scroll = 0;

	$scope.getNavStyle = function(scroll) {
		if (scroll > 100)
			return 'pdf-controls fixed';
		else
			return 'pdf-controls';
	}
	
	$scope.print = function(pdfFile){
		printJS({ printable: pdfFile });
	}
	
	var parentData = {
			"empId": "",
			"fromDate": getFormattedDate($scope.fromDate),
			"toDate": getFormattedDate($scope.toDate),
			"toEmail": [],
			"ccEmail": [],
			"bccEmail": []
	};
	
	$scope.generateReport = function(){
		setDefaults("Yes");
		var searchId = $scope.searchId;
		if(searchId == "" || searchId.length ==0){
			showAlert('Please enter an Employee ID');
		}else if(isNaN(searchId)){
			showAlert('Please enter only digits');
			$scope.searchId = "";
		}else if(searchId != ""&& (searchId.length < 5 || searchId.length > 5)){
			showAlert('Employee ID should be 5 digits');
			$scope.searchId = "";
		}else{
			setDefaults("No");
			parentData.empId = $scope.searchId;
			$scope.pdfUrl = "reports/"+$scope.searchId+"_"+parentData.fromDate+"_"+parentData.toDate+".pdf";
		}
	};
	
	function setDefaults(val){
		if(val == "Yes"){
			$('nav.pdf-controls').css("display","none");
			$('canvas').css("display","none");
			$('#reportMsg').css("margin-top","200px");
			$('#reportMsg').css("margin-left","300px");
			$scope.reportMsg ="Please generate a report for preview.";
		}else{
			$('nav.pdf-controls').css("display","block");
			$('canvas').css("display","block");
			$('#reportMsg').css("margin-top","0");
			$('#reportMsg').css("margin-left","0");
			$scope.reportMsg ="";
		}
		
	}
	function getFormattedDate(date){
		var day = date.getDate();
		var month = date.getMonth() + 1;
		var year = date.getFullYear();
		return year + '-' + (month < 10 ? "0" + month : month) + '-'
				+ (day < 10 ? "0" + day : day);
	}
	
	$scope.sendEmail = function(ev){
		$mdDialog.show({
		      controller: DialogController,
		      templateUrl: 'templates/emailTemplate.html',
		      parent: angular.element(document.body),
		      targetEvent: ev,
		      clickOutsideToClose:true,
		      locals:{dataToPass: parentData},
		    })
		    .then(function(result) {
		    	if(result.data == "Success") showAlert('Report has been emailed successfully to the recepient(s)');
		    	else showAlert("Something went wrong while sending email!!!");
		    });
		  };

	$scope.cancel = function() {
	    $mdDialog.cancel();
	  };
	  
	 function DialogController($scope, $mdDialog, dataToPass) {
		 $scope.toEmail = "";
		 $scope.ccEmail = "";
		 $scope.invalidMsg = "";
		 $scope.showLoader = false;
		 $scope.showSend = false;
		 
		 $scope.hide = function() {
			 $mdDialog.hide();
		 };

		 $scope.cancel = function() {
			 $mdDialog.cancel();
		 };

		 $scope.send = function() {
			 $scope.showLoader = true;
			 if($scope.invalidMsg == ""){
				var req = {
					method : 'POST',
					url : appConfig.appUri+"sendEmail",
					data : dataToPass
				}
				$http(req).then(
				 function onSuccess(response) {
					 $scope.showLoader = false;
					 $mdDialog.hide(response);
				 },function onError(response) {
					 $scope.showLoader = false;
					 $mdDialog.hide(response);
				});
			 }
		 };
		 
		 $scope.validateEmail = function(from, elementId){
			 var emailId = "";
			 if(from == "TO"){
				 emailId = $scope.toEmail;
				 dataToPass.toEmail = [];
			 }else if(from == "CC"){
				 emailId = $scope.ccEmail;
				 dataToPass.ccEmail = [];
			 }
			 if(emailId != ""){
				 if(emailId.indexOf(",") != -1){
					 var emails = emailId.split(",");
					 for(var i=0;i<emails.length;i++){
						 if(emails[i].trim() != ""){
							 if(validateEmail(emails[i].trim())){
								 $scope.invalidMsg = "";
								 if(from == "TO") dataToPass.toEmail.push(emails[i].trim());
								 else if(from == "CC") dataToPass.ccEmail.push(emails[i].trim());
							 }else{
								$scope.invalidMsg = "Please enter only valid email id(s)!";
								document.getElementById(elementId).focus();
							 }
						 }
					 }
				 }else{
					 if(validateEmail(emailId.trim())){
						 $scope.invalidMsg = "";
						 if(from == "TO") dataToPass.toEmail.push(emailId.trim());
						 else if(from == "CC") dataToPass.ccEmail.push(emailId.trim());
					 }else{
						 $scope.invalidMsg = "Please enter only valid email id(s)!";
						 document.getElementById(elementId).focus();
					 }
				 }
				 $scope.showSend = true;
			 }
		 };
		 
		 function validateEmail(emailId){
			 var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
			 return re.test(emailId);
		 }
	 }
});
