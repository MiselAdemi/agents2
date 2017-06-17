var agents = angular.module('agents', ['ngRoute','ngResource', 'ui.bootstrap', 'ngSanitize']);


agents.config(['$routeProvider', function($routeProvider){

	$routeProvider
			.when("/", {
				templateUrl: "agents.html"
			})
			.when("/ws", {
				templateUrl: "websocket.html"
			})
		}
]);