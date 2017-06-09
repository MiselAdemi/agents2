var agents = angular.module('agents', ['ui.bootstrap']);
var ip = "localhost";
var ipMaster = "127.0.0.1";

agents.controller('AgentsController', ['$scope', '$http', '$uibModal',
	function($scope, $http, $uibModal){

	var handshake = function(){
		var registerMe_data = {
				alias: "local_" + ip,
				address: ip
		}
		$http.post("http://" + ipMaster + ":8080/AgentsWeb/rest/ac/node", registerMe_data);
	}

	window.onload = function(){
		$http.get("http://" + ip + ":8080/AgentsWeb/rest/ac/isMaster")
		.success(function(data){
			console.log(data);
			if(data == 'true')
				console.log('I am master');
			else {
				console.log("Handshake...");
				handshake();
			}
		})
	};


	//adding agent
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

	//get running agents
	$scope.getRunningAgents = function(){
		$http.get("http://" + ip + ":8080/AgentsWeb/rest/agents/running").
		success(function(data){
			$scope.runningAgents = data;
			console.log("Running agents: " + data);
		});
	};
	//get agent types
	$scope.getAgentTypes = function(){
		$http.get("http://" + ip + ":8080/AgentsWeb/rest/agents/classes").
		success(function(data){
			$scope.agentTypes = data.agentTypes;
		});
	};
	
	//get performative
	$http.get("http://" + ip + ":8080/AgentsWeb/rest/messages").
	success(function(data){
		$scope.performatives = data;
	})

	$scope.sendMessage = function(){
		var agentCenter = {
				"alias":
					"neki alias",
					"address":
						"neka adresa"
		}
		var agentType = {
				"name":
					"neko ime tipa",
					"module":
						"neki modul tipa"
		}
		var sender = {
				"name":
					$scope.selectedSender.id,
					"host":
						agentCenter,
						"type":
							agentType
		}
		var receiver = {
				"name":
					$scope.selectedReciever[0].id,
					"host":
						agentCenter,
						"type":
							agentType
		}
		var replyTo = {
				"name":
					$scope.selectedReplyTo.id,
					"host":
						agentCenter,
						"type":
							agentType
		}
		var data = {
				"performative":
					$scope.selectedPerformative,
					"sender":
						sender,
						"receivers":
							[receiver],
							"replyTo":
								replyTo,
								"content":
									$scope.content,
									"contentObject":
									{},
									"userArgs":
									{},
									"language":
										$scope.language,
										"encoding":
											$scope.encoding,
											"ontology":
												$scope.ontology,
												"protocol":
													$scope.protocol,
													"conversationId":
														$scope.conversationId,
														"replyWith":
															$scope.replyWith,
															"replyBy":
																parseInt($scope.replyBy)
		}
		console.log(data);
		$http.post("http://" + ip + ":8080/AgentsWeb/rest/messages", data);

	}
	
	$scope.clearConsole = function() {
		
	}

	setInterval($scope.getRunningAgents, 2000);
	setInterval($scope.getAgentTypes, 2000);
	
}
])


.controller('AgentNameController', ['$scope', 'agent', '$uibModalInstance', '$http',
	function($scope, agent, $uibModalInstance, $http){
	console.log(agent);
	$scope.agent = agent;
	$scope.agentName = "";
	$scope.create = function(){
		console.log($scope.agentName);
		$http.put("http://" + ip + ":8080/AgentsWeb/rest/agents/running/PingPong$" + $scope.agent.name + "/" + $scope.agentName);
		$uibModalInstance.close();
	}
	$scope.close = function(){
		$uibModalInstance.close();
	}

}                 
]);