myApp.controller("reportsController", function($scope, $http, myFactory, $mdDialog, appConfig, $timeout, $compile) {
	$scope.records = [];
	$scope.searchId="";
	// Date picker related code
	var today = new Date();
	$scope.maxDate = today;
	$scope.fromDate = today;
	$scope.toDate = today;
	$scope.reportMsg ="Please generate a report for preview.";
	$scope.validateDates = function(dateValue, from) {
		if(from == "FromDate"){
			var toDat = $scope.toDate;
			var difference = daysBetween(dateValue, toDat);
			if(difference < 0 ){
				showAlert('From Date should not be greater than To Date');
				$scope.fromDate = today;
				$scope.toDate = today;
			}else{
				$scope.fromDate = dateValue;
				$scope.toDate = toDat;
			}
		}else if(from == "ToDate"){
			var fromDat = $scope.fromDate;
			var differene = daysBetween(fromDat, dateValue);
			if(differene < 0 ){
				showAlert('To Date should not be less than From Date');
				$scope.fromDate = today;
				$scope.toDate = today;
			}else{
				$scope.fromDate = fromDat;
				$scope.toDate = dateValue;
			}
		}
	};
	
	$scope.refreshPage = function(){
		$scope.fromDate = today;
		$scope.toDate = today;
		setDefaults();
	};
	
	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Ok'));
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
	
	$scope.validateEmpId = function(){
		var searchId = $scope.searchId;
		if(searchId !="" && isNaN(searchId)){
			showAlert('Please enter only digits');
			$scope.searchId = "";
			document.getElementById('searchId').focus();
		}else if(searchId != ""&& (searchId.length < 5 || searchId.length > 5)){
			showAlert('Employee ID should be 5 digits');
			$scope.searchId = "";
			document.getElementById('searchId').focus();
		}else if(searchId !="" && !checkEmpIdRange(searchId)){
			showAlert('Employee ID should be in between '+appConfig.empStartId+' - '+appConfig.empEndId);
			$scope.searchId = "";
			document.getElementById('searchId').focus();
		}
	};
	
	function checkEmpIdRange(searchId){
		return parseInt(searchId) >= appConfig.empStartId && parseInt(searchId) <= appConfig.empEndId;
	}
	
	$scope.generateReport = function(){
		deletePreviousReport();
		$scope.pdfUrl = "";
		$scope.reportMsg ="";
		parentData.empId = $scope.searchId;
		parentData.fromDate = getFormattedDate($scope.fromDate);
		parentData.toDate = getFormattedDate($scope.toDate);
		generatePdfReport(parentData);
		showProgressDialog();
		$timeout(function(){previewPdfReport();},6000);
	};
	
	function deletePreviousReport(){
		var empId = "";
		if(parentData.empId != ""){
			empId = parentData.empId;
		}else{
			empId = $scope.searchId;
		}
		if(empId == "") empId = 0;
		var fileName = empId+"_"+parentData.fromDate+"_"+parentData.toDate;
		$http({
	        method : "GET",
	        url : appConfig.appUri + "deleteReport/" + fileName
	    }).then(function mySuccess(response) {
	    	console.log("Report deleted successfully.");
	    }, function myError(response) {
	    	console.log("Something went wrong while deleting the report!!!");
	    });
	}
	
	function showProgressDialog(){
		$('#home').addClass('md-scroll-mask');
		$mdDialog.show({
	      templateUrl: 'templates/progressDialog.html',
	      controller: ProgressController,
	      parent: angular.element(document.body),
	      clickOutsideToClose:false
	    });
	}
	
	function ProgressController($scope) {
		$scope.progressText = "Please wait!!! Report is being generated.";
	}
	
	function previewPdfReport(){
		var pdfTemplate = '';
		if($scope.pdfUrl != "No data available"){
			pdfTemplate = '<ng-pdf template-url="templates/pdf-viewer.html" canvasid="pdf" scale="page-fit" page=1 style="width:940px;border-radius:5px;"></ng-pdf>';
		}else{
			pdfTemplate = '<p style="color: #fff; font-size: 1.35em; vertical-align: middle; margin-top: 200px; margin-left: 300px;">No data available for the search criteria...</p>';
		}
		$("#pdfReportPreview").html($compile(pdfTemplate)($scope));
		$mdDialog.hide();
	}
	
	function generatePdfReport(data){
		var empId = "";
		if(data.empId == ""){
			empId = 0;
		}else{
			empId = data.empId;
		}
		$http({
	        method : "GET",
	        url : appConfig.appUri + "attendance/generatePdfReport/" + empId + "/" + data.fromDate + "/" +data.toDate
	    }).then(function mySuccess(response) {
	    	if(response.data == "No data available"){
	    		$scope.pdfUrl = response.data;
	    	}else{
	    		$scope.pdfUrl = "reports/"+response.data;
	    	}
	    }, function myError(response) {
	    	showAlert("Something went wrong while generating report!!!");
	    	$scope.pdfUrl = "";
	    });
	}
	
	
	function getFormattedDate(date){
		var day = date.getDate();
		var month = date.getMonth() + 1;
		var year = date.getFullYear();
		return year + '-' + (month < 10 ? "0" + month : month) + '-'
				+ (day < 10 ? "0" + day : day);
	}
	
	$scope.sendEmail = function(ev){
		$('#home').addClass('md-scroll-mask');
		parentData.toEmail = [];
		parentData.ccEmail = [];
		$mdDialog.show({
		      controller: DialogController,
		      templateUrl: 'templates/emailTemplate.html',
		      parent: angular.element(document.body),
		      targetEvent: ev,
		      clickOutsideToClose:true,
		      locals:{dataToPass: parentData},
		    })
		    .then(function(result) {
		    	if(result.data == "Success"){
		    		showAlert('Report has been emailed successfully to the recepient(s)');
		    		deleteReport($scope.pdfUrl);
		    	}
		    	else if(result.data == "Cancelled" || result.data == undefined){
		    		console.log("Dialog cancelled");
		    	}
		    	else{
		    		showAlert("Something went wrong while sending email!!!");
		    	}
		    });
		  };

	  $scope.cancel = function() {
	    $mdDialog.hide('Cancelled');
	  };
	  
	  function deleteReport(pdfReport){
		  var fileName = pdfReport.substring(pdfReport.indexOf("/")+1,pdfReport.indexOf("."));
		  $http({
		        method : "GET",
		        url : appConfig.appUri + "deleteReport/" + fileName
		    }).then(function mySuccess(response) {
		    	console.log("Report deleted successfully after sending email.");
		    }, function myError(response) {
		    	console.log("Something went wrong while deleting the report!!!");
		    });
		  setDefaults();
	  }
	  
	  function setDefaults(){
		  var defaultTemplate = '<p id="reportMsg" style="color: #fff; font-size: 1.35em; opacity: 0.5; vertical-align: middle; margin-top: 200px;'+ 
			  'margin-left: 300px;">Please generate a report for preview.</p>';
		  $("#pdfReportPreview").html($compile(defaultTemplate)($scope));
		  $scope.searchId="";
	  }
	  
	 function DialogController($scope, $mdDialog, dataToPass) {
		 $scope.toEmail = "";
		 $scope.ccEmail = "";
		 $scope.invalidMsg = "";
		 $scope.showLoader = false;
		 
		 $scope.hide = function() {
			 $mdDialog.hide('Cancelled');
		 };

		 $scope.cancel = function() {
			 $mdDialog.hide('Cancelled');
		 };

		 $scope.send = function() {
			 if($scope.invalidMsg == ""){
				$scope.showLoader = true;
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
						 $scope.invalidMsg = "Please enter only valid nisum email id(s)!";
						 document.getElementById(elementId).focus();
					 }
				 }
			 }
		 };
		 
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
			 var toEmail = $scope.toEmail;
			 if(toEmail == ""){
				 $scope.invalidMsg = "To Email is mandatory";
				 document.getElementById('toEmail').focus();
			 }else{
				 $scope.validateEmail("TO",'toEmail');
				 $scope.send();
			 }
		 };
	 }
});
