myApp.controller("reportsController", function($scope, $http, myFactory, $mdDialog, appConfig, $timeout, $compile) {
	$scope.records = [];
	$scope.searchId="";
	// Date picker related code
	var today = new Date();
	var priorDt = new Date(today.getTime() - (30 * 24 * 60 * 60 * 1000));
	$scope.maxDate = today;
	$scope.fromDate = priorDt;
	$scope.toDate = today;
	$scope.reportMsg ="Please generate a report for preview.";
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
	
	$scope.refreshPage = function(){
		$scope.fromDate = priorDt;
		$scope.toDate = today;
		setDefaults();
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
		}
	};
	
	$scope.generateReport = function(){
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
			$scope.reportMsg ="";
			parentData.empId = $scope.searchId;
			generatePdfReport(parentData);
			showProgressDialog();
			$timeout(function(){previewPdfReport();},5000);
		}
	};
	
	function showProgressDialog(){
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
		var pdfTemplate = '<ng-pdf template-url="templates/pdf-viewer.html" canvasid="pdf" scale="page-fit" page=1 style="width:940px;border-radius:5px;"></ng-pdf>';
		$("#pdfReportPreview").html($compile(pdfTemplate)($scope));
		$mdDialog.hide();
	}
	
	function generatePdfReport(data){
		$http({
	        method : "GET",
	        url : appConfig.appUri + "attendance/generatePdfReport/" + data.empId + "/" + data.fromDate + "/" +data.toDate
	    }).then(function mySuccess(response) {
	        $scope.pdfUrl = "reports/"+response.data;
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
						 $scope.invalidMsg = "Please enter only valid email id(s)!";
						 document.getElementById(elementId).focus();
					 }
				 }
			 }
		 };
		 
		 function validateEmail(emailId){
			 var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
			 return re.test(emailId);
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
