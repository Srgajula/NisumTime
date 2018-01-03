myApp.controller("resyncDataController",function($scope, myFactory, $mdDialog, appConfig, $http, $timeout){
	
	// Date picker related code
	var today = new Date();
	$scope.maxDate = today;
	$scope.searchDate = today;
	
	$scope.setSearchDate = function(dateValue){
		$scope.searchDate = dateValue;
	};
	
	$scope.resyncData = function(){
		showProgressDialog("Performing data resync!!! Please wait...");
		var searchDate = getFormattedDate($scope.searchDate);
		$http({
	        method : "POST",
	        url : appConfig.appUri + "attendance/employeesDataSave/" + searchDate
	    }).then(function mySuccess(response) {
	    	$mdDialog.hide();
	    	if(response.data == true){
	    		$timeout(function(){showAlert('Data resync successful.');},600);
	    	}else{
	    		$timeout(function(){showAlert('Data resync failed.');},600);
	    	}
	    	
	    }, function myError(response) {
	    	showAlert("Something went wrong while data resync!!!");
	    });
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