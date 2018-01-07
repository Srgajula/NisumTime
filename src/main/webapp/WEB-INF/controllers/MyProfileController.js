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
	    });
	}
	
	$scope.refreshPage = function(){
			$scope.getProfileData();
	};
	$scope.updateProfile = function(){
		$('#home').addClass('md-scroll-mask');
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
		    });
	};
	
	function UpdateProfileController($scope, $mdDialog, dataToPass) {
		$scope.profile = dataToPass;
		$scope.technologies=myFactory.getTechnologies();
		$scope.designations=myFactory.getDesignations();
		$scope.baseTechnology=(dataToPass.baseTechnology == null ? undefined: dataToPass.baseTechnology);
		$scope.mobileNumber=dataToPass.mobileNumber;
		$scope.alternateMobileNumber=dataToPass.alternateMobileNumber;
		$scope.personalEmailId=dataToPass.personalEmailId;
		$scope.technologyKnown=dataToPass.technologyKnown;
		$scope.designationEmp=(dataToPass.designation == null ? undefined: dataToPass.designation);
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
		$scope.getDesignationText = function(){
			if ($scope.designationEmp !== undefined) {
				return $scope.designationEmp;
			} else {
				return "Please select designation";
			}
		};
		$scope.validateFields = function(){
				var mobileNumber = $scope.mobileNumber;
				$scope.alertMsg = "";
				var record = {"employeeId":myFactory.getEmpId(),"designation": $scope.designationEmp, "mobileNumber": mobileNumber, "alternateMobileNumber": $scope.alternateMobileNumber, "personalEmailId": $scope.personalEmailId, "baseTechnology": $scope.baseTechnology, "technologyKnown": $scope.technologyKnown};
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
					$mdDialog.hide('Success');
					$scope.dataToPass=response.data;
				}, function myError(response){
					$mdDialog.hide('Error');
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
