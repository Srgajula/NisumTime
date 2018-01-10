myApp.controller('SessionController',
		function($scope, Idle, Keepalive, myFactory, $mdDialog, $compile) {
			$scope.started = false;

			function closeModals() {
				$mdDialog.hide();
				if ($scope.warning) {
					$scope.warning = null;
				}

				if ($scope.timedout) {
					$scope.timedout = null;
				}
			}

			$scope.$on('IdleStart', function() {
				closeModals();
				$scope.warning  = showProgressDialog('Your session is exipired!!! Please wait redirecting to Login page.');
			});

			$scope.$on('IdleEnd', function() {
				closeModals();
			});

			$scope.$on('IdleTimeout', function() {
				closeModals();
				$scope.timedout = redirectToLoginPage();
			});

			$scope.start = function() {
				closeModals();
				Idle.watch();
				$scope.started = true;
			};

			$scope.stop = function() {
				closeModals();
				Idle.unwatch();
				$scope.started = false;

			};

			function redirectToLoginPage() {
				$mdDialog.hide();

				var auth2 = gapi.auth2.getAuthInstance();
				auth2.signOut().then(function() {
					console.log('User signed out.');
				});
				auth2.disconnect();

				// Clear if any values set to factory
				var menuItems = [], designations = [], accounts = [], technologies = [], shifts = [];
				myFactory.setEmpId("");
				myFactory.setEmpName("");
				myFactory.setEmpEmailId("");
				myFactory.setEmpRole("");
				myFactory.setMenuItems(menuItems);
				myFactory.setTemplateUrl("");
				myFactory.setProfileUrl("");
				myFactory.setDesignations(designations);
				myFactory.setAccounts(accounts);
				myFactory.setTechnologies(technologies);
				myFactory.setShifts(shifts);

				var element = document.getElementById('home');
				var path = "'templates/login.html'";
				element.setAttribute("src", path);
				var newTemplate = angular.element(element);
				$('#home').html(newTemplate);
				$compile($('#home'))($scope)
			}

			function showProgressDialog(msg) {
				$('#home').addClass('md-scroll-mask');
				$mdDialog.show({
					templateUrl : 'templates/progressDialog.html',
					controller : ProgressController,
					parent : angular.element(document.body),
					clickOutsideToClose : false,
					locals : {
						dataToPass : msg
					}
				});
			}

			function ProgressController($scope, dataToPass) {
				$scope.progressText = dataToPass;
			}
		})

.config(function(IdleProvider, KeepaliveProvider) {
	IdleProvider.idle(600);
	IdleProvider.timeout(3);
	KeepaliveProvider.interval(5);
});