var agents = angular.module('agents', ['ui.bootstrap']);

agents.controller('AgentsController', ['$scope', '$http', '$uibModal',
	function($scope, $http, $uibModal){

	$scope.addAgent = function(agent){
		var modalInstance = $uibModal.open({
			animation: false,
			templateUrl: 'agentName.html',
			controller: 'AgentNameController',
			resolve:{
				agent: function(){
					return agent;
				}
			}
		})
		console.log(agent);
	}
	//get agent types
	$http.get("http://localhost:8080/AgentsWeb/rest/agents/classes").
	success(function(data){
		$scope.agentTypes = data;
	});
	//get running agents
	$http.get("http://localhost:8080/AgentsWeb/rest/agents/running").
	success(function(data){
		$scope.runningAgents = data;
	});
}
])


.controller('AgentNameController', ['$scope', 'agent', '$uibModalInstance', '$http',
	function($scope, agent, $uibModalInstance, $http){
	console.log(agent);
	$scope.agent = agent;
	$scope.agentName = "";
	$scope.create = function(){
		console.log($scope.agentName);
		$http.put("http://localhost:8080/AgentsWeb/rest/agents/running/PingPong$" + $scope.agent.name + "/" + $scope.agentName);
		console.log("here");
		$uibModalInstance.close();
	}
	$scope.close = function(){
		$uibModalInstance.close();
	}

}                 
]);