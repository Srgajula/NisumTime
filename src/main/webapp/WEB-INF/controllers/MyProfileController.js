myApp.controller("profileController", function($scope, $http, myFactory, $mdDialog, appConfig) {
	$scope.records = [];
	$scope.empId = myFactory.getEmpId();
	$scope.empName = myFactory.getEmpName();
	$scope.empEmailId = myFactory.getEmpEmailId();
	
	$scope.getProfileData = function(){
		var empId = $scope.empId;
		$http({
	        method : "GET",
	        url : appConfig.appUri + "user/getEmployeeRoleData?empId=" + empId
	    }).then(function mySuccess(response) {
	        $scope.profile = response.data;
	      }, function myError(response) {
	    	showAlert("Something went wrong while fetching data!!!");
	   // 	$scope.profile = ;
	    });
	}
	
	$scope.refreshPage = function(){
			$scope.getProfileData();
	};
	$scope.updateProfile = function(){
		$mdDialog.show({
		      controller: UpdateProfileController,
		      templateUrl: 'templates/updateProfile.html',
		      parent: angular.element(document.body),
		      clickOutsideToClose:true,
		      locals:{dataToPass: $scope.profile},
		    })
		    .then(function(result) {
		   
		   if(result == "Success") showAlert('Profile updated successfully');
		   	else if(result == "Error") showAlert('Profile updation failed!!!');
		    	$scope.refreshPage();
		   // 	else showAlert('Profile updation failed!!!');
		    });
	};
	
	function UpdateProfileController($scope, $mdDialog, dataToPass) {
		$scope.profile = dataToPass;
		$scope.technologies=myFactory.getTechnologies();
		$scope.baseTechnology=dataToPass.baseTechnology;
		$scope.mobileNumber=dataToPass.mobileNumber;
		$scope.alternateMobileNumber=dataToPass.alternateMobileNumber;
		$scope.personalEmailId=dataToPass.personalEmailId;
		$scope.technologyKnown=dataToPass.technologyKnown;
		$scope.cancel = function() {
		    $mdDialog.hide();
		};
		$scope.getSelectedTech = function(){
			if ($scope.baseTechnology !== undefined) {
				return $scope.baseTechnology;
			} else {
				return "Please select primary skill";
			}
		};
		$scope.validateFields = function(){
			var mobileNumber = $scope.mobileNumber;
			var alternateMobileNumber = $scope.alternateMobileNumber;
			var personalEmailId = $scope.personalEmailId;
			var baseTechnology = $scope.baseTechnology;
			var technologyKnown = $scope.technologyKnown;
			
				$scope.alertMsg = "";
				var record = {"employeeId":myFactory.getEmpId(), "mobileNumber": mobileNumber, "alternateMobileNumber": $scope.alternateMobileNumber, "personalEmailId": $scope.personalEmailId, "baseTechnology": $scope.baseTechnology, "technologyKnown": $scope.technologyKnown};

				var urlRequest  = "";
					urlRequest = appConfig.appUri+ "user/updateProfile";
				
				var req = {
					method : 'POST',
					url : urlRequest,
					headers : {
						"Content-type" : "application/json"
					},
					data : record
				}
				$http(req).then(function mySuccess(response) {
					//$scope.refreshPage();
					$mdDialog.hide('Success');
					$scope.dataToPass=response.data;
					//$scope.profile=response.data
					//$scope.result = "Success";
					
				}, function myError(response){
					$mdDialog.hide('Error');
					//$scope.result = "Error";
				});
			
				
		};
	}

	function showAlert(message) {
		$mdDialog.show($mdDialog.alert().parent(
				angular.element(document.querySelector('#popupContainer')))
				.clickOutsideToClose(true).textContent(message).ariaLabel(
						'Alert Dialog').ok('Ok'));
	}
});
