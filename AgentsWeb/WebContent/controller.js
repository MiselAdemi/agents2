var agents = angular.module('agents', []);

agents.controller('AgentsController', ['$scope', '$http',
	function($scope, $http){
	var agent = {"name": "Ping"};
	var agent2 = {"name": "Pong"};
	$scope.agentTypes = [agent, agent2]

	$scope.addAgent = function(agent){
		console.log(agent);
		console.log("hello from add agent");
	}

	$http.get("http://localhost:8080/AgentsWeb/rest/agents/test").
	success(function(data){
		$scope.test = data;
		console.log($scope.test);
	})
}
]);