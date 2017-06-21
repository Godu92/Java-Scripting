/**
 * 
 */
'use strict';
angular.module("outerMain", []).controller('main', function($scope, $http) {

	$scope.login = function() {
		console.log($scope.input);
		$http({
			method : 'POST',
			url : 'login',
			data : $scope.input,
			headers : {
				'Content-Type' : 'application/json'
			}
		}).success(function(data) {
			console.log(data);
		}).error(function(data) {
			console.log(data);
		});
		console.log("POST done");
	};
});