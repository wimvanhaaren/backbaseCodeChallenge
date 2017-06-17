angular.module('kalahApp', []).controller('GameController', ['$http', function($http) {

	var game = this;

	game.status = '';
    game.players = [ {id:1, name:'player 1'}, {id:1, name:'player 2'} ];
    game.board = {};
    game.board.pits = [];
 
    game.start = function() {
    	$http.post('/kalah', { 'player1':game.players[0], 'player2':game.players[1]})
    		.then(function successCallback(response) {
    			game.gameId = response.data.gameId;
    			game.status = 'STARTED';
    			game.currentPlayer = response.data.currentPlayer;
    			updateBoard();
    		}, function errorCallback(response) {
    			game.status = 'ERROR';
    		});
    };
    
    var updateBoard = function() {
    	$http.get('/kalah/'+game.gameId+"/board")
    		.then(function successCallback(response) {
    			game.board = response.data;
    			game.status = 'PLAYING';
    		},
    		function errorCallback(res) {
    			game.status = 'ERROR';
    		});
    }
    
    game.move = function(index) {
		$http.post("/kalah/"+game.gameId+"/board/"+index, game.currentPlayer)
		.then(function successCallback(response) {
    	    game.currentPlayer = response.data.currentPlayer;
    	    if (response.data.gameOver === true) {
    	    	game.status = 'AND THE WINNER IS ' + response.data.winner.name + '!!!';
    	    	alert('We have a winner!\nCongratulations ' + response.data.winner.name);
    	    } else {
    	    	updateBoard();
    	    }
		}, function errorCallback(response) {
			game.status = 'ERROR';
		});
    }
 
  }]);
