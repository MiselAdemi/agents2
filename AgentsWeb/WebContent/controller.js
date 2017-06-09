var ip = "127.0.0.1";
var ipMaster = "127.0.0.1";
var port = window.document.location.port;
var webSocket;

angular.module('agents')
.controller('AgentsController', ['$scope', '$http', '$uibModal',
	function($scope, $http, $uibModal){

	var handshake = function(){
		var registerMe_data = {
				alias: "local_" + ip + ":" + port,
				address: ip + ":" + port
		}
		$http.post("http://" + ipMaster + ":8080/AgentsWeb/rest/ac/node", registerMe_data);
	}

	$scope.onload = function(){
		$http.get("http://" + ip + ":" + port + "/AgentsWeb/rest/ac/isMaster")
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
			//console.log($scope.runningAgents);
		});
	};
	//get agent types
	$scope.getAgentTypes = function(){
		$http.get("http://" + ip + ":" + port + "/AgentsWeb/rest/agents/classes").
		success(function(data){
			$scope.agentTypes = data;
			//console.log($scope.agentTypes);
		});
	};
	
	//get performative
	$http.get("http://" + ip + ":" + port + "/AgentsWeb/rest/messages").
	success(function(data){
		$scope.performatives = data;
	})

	$scope.sendMessage = function(){
		var agentCenter = {
				"alias":
					"master",
					"address":
						ip + ":" + port
		}
		var agentType = {
				"name":
					"neko ime tipa",
					"module":
						"neki modul tipa"
		}
		var data = {
				"performative":
					$scope.selectedPerformative,
					"sender":
					$scope.selectedSender.id,
					"receivers":
					[$scope.selectedReciever.id],
					"replyTo":
					$scope.selectedReplyTo.id,
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
		$http.post("http://" + ip + ":" + port + "/AgentsWeb/rest/messages", data);

	}
	
	$scope.clearConsole = function() {
		
	}

	setInterval($scope.getRunningAgents, 2000);
	setInterval($scope.getAgentTypes, 2000);
	
}
])
.controller('WebSocketController', ['$scope', '$http', '$uibModal',
	function($scope, $http, $uibModal){

	if(webSocket == undefined)
		webSocket = new WebSocket("ws://" + ip + ":" + port + "/AgentsWeb/websocket");

	var handshake = function(){
		var registerMe_data = {
				alias: "local_" + ip,
				address: ip
		}
		$http.post("http://" + ipMaster + ":" + port + "/AgentsWeb/rest/ac/node", registerMe_data);
	}

	$scope.onload = function(){
		$http.get("http://" + ip + ":" + port + "/AgentsWeb/rest/ac/isMaster")
		.success(function(data){
			console.log(data);
			if(data == 'true')
				console.log('I am master');
			else{
				console.log('Handshake...');
				handshake();
			}
		})
	};


	//adding agent
	$scope.addAgent = function(agent){
		var modalInstance = $uibModal.open({
			animation: false,
			templateUrl: 'agentName.html',
			controller: 'AgentNameWSController',
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
		if(webSocket.readyState == 1){
			var text = {"type": "RUNNING_AGENTS"};
			webSocket.send(JSON.stringify(text));
		}
	};
	
	//get agent types
	$scope.getAgentTypes = function(){
		if(webSocket.readyState == 1){
			var text = {"type": "AGENT_TYPES"};
			webSocket.send(JSON.stringify(text));
		}
	};

	//get performative
	$scope.getPerformative = function(){
		if(webSocket.readyState == 1){
			var text = {"type":"PERFORMATIVES"};
			webSocket.send(JSON.stringify(text));
		}
	}

	//TODO: WEBSOCKET
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

		var data = {
				"performative":
					$scope.selectedPerformative,
					"sender":
						$scope.selectedSender.id,
						"receivers":
							[$scope.selectedReciever.id],
							"replyTo":
								$scope.selectedReplyTo.id,
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
		
		if(webSocket.readyState == 1){
			var message = {"type":"SEND_MESSAGE", "data": data}
			webSocket.send(JSON.stringify(message));
		}
	}

	$scope.clearConsole = function(){
	}
	
	webSocket.onopen = function(){
		$scope.getAgentTypes();
		$scope.getPerformative();
		$scope.getRunningAgents();
	}
	
	webSocket.onmessage = function(message){
		var msg = JSON.parse(message.data);
		if(msg.runningAgents != undefined){
			$scope.runningAgents = msg.runningAgents;
			$scope.$apply();
		}
		else if(msg.agentTypes != undefined){
			$scope.agentTypes = msg.agentTypes;
			console.log($scope.agentTypes);
			console.log($scope.agentTypes[0].name);
			$scope.$apply();
		}
		else if(msg.performatives != undefined){
			$scope.performatives = msg.performatives;
			$scope.$apply();
		}
	}


	setInterval($scope.getRunningAgents, 2000);
	setInterval($scope.getAgentTypes,2000)

}
])
.controller('AgentNameController', ['$scope', 'agent', '$uibModalInstance', '$http',
	function($scope, agent, $uibModalInstance, $http){
	console.log(agent);
	$scope.agent = agent;
	$scope.agentName = "";
	$scope.create = function(){
		console.log($scope.agentName);
		$http.put("http://" + ip + ":" + port + "/AgentsWeb/rest/agents/running/PingPong$" + $scope.agent.name + "/" + $scope.agentName);
		$uibModalInstance.close();
	}
	$scope.close = function(){
		$uibModalInstance.close();
	}

}                 
])
.controller('AgentNameWSController', ['$scope', 'agent', '$uibModalInstance', '$http',

	function($scope, agent, $uibModalInstance, $http){
	console.log(agent);
	$scope.agent = agent;
	$scope.agentName = "";
	$scope.create = function(){
		console.log($scope.agentName);
		var data = {"type":"ADD_AGENT",
				"data": {"1":$scope.agent.name,
				"2":$scope.agentName
			}
		}
		webSocket.send(JSON.stringify(data));
		$uibModalInstance.close();
	}
	$scope.close = function(){
		$uibModalInstance.close();
	}

}                 
]);