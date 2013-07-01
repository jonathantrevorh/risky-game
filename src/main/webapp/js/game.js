risky.controller('GameController', function ($scope, $q, asynchttp) {
    $scope.turnOwner = 0;
    // Get necesarry http requests (player data and map json)
    polygon_promise = asynchttp.get("map");
    players_promise = asynchttp.get("players");
    var map = new Map(document.getElementById("map"), polygon_promise, {});

    polygon_promise.then(function(ret_polygons){
        $scope.polygons=ret_polygons;
        map.draw();
    });
    players_promise.then(function(ret_players){
        $scope.players=ret_players;
        map.draw();
    });

    // Set up the game infrastructure to work with promises
    $scope.nextTurn = function() {
        players_promise.then(function(ret_players){
            $scope.turnOwner = ($scope.turnOwner+1) % ret_players.length;
            map.draw();
        });
    }
risky.controller('GameController', function ($scope, modelloader) {
    
    $scope.setState = function (newState) {
        $scope.state = newState;
        $scope.turnOwner = 0;
        $scope.currentAction = 0;
        if ($scope.states[$scope.state].init) {
            $scope.states[$scope.state].init();
        }
        if ($scope.states[$scope.state].actions[0].init) {
            $scope.states[$scope.state].actions[0].init();
        }
    };
    
    $scope.nextTurn = function (forceful) {
        if ($scope.turnOwner == $scope.players.length-1 && !forceful) {// if last player
            if ($scope.states[$scope.state].deinit) {
                $scope.states[$scope.state].deinit();// deinitialize the state
            }
        } else {// otherwise
            $scope.turnOwner = ++$scope.turnOwner % $scope.players.length;// move to the next player
            if ($scope.states[$scope.state].actions[$scope.currentAction].init) {
                $scope.states[$scope.state].actions[$scope.currentAction = 0].init();// reset action and initialize
            }
        }
    };
    
    $scope.nextAction = function () {
        if ($scope.states[$scope.state].actions[$scope.currentAction].deinit) {
            $scope.states[$scope.state].actions[$scope.currentAction].deinit();// deinitialize the previous action
        }
        if ($scope.currentAction == $scope.states[$scope.state].actions.length-1) {// if last action
            $scope.nextTurn();// move to the next player
        } else if ($scope.states[$scope.state].actions[++$scope.currentAction].init) {
            $scope.states[$scope.state].actions[++$scope.currentAction].init();// otherwise move to next state
        }
    };
    
    $scope.states = {
        /*'default': {
            'init': function () {},
            'actions': {
                0: {
                    'init': function () {},
                    'mapClick': function () {},
                    'deinit': funciton () {}
                }
            },
            'deinit': function () {}
        },*/
        'setup': {
            'init': function () {},
            'actions': {
                0: {
                    'mapClick': function (e) {
                        var territory = map.getTerritoryAt(map.toMapPoint([e.pageX, e.pageY]));
                        if (!territory) return; // alert user of invalid territory
                        if (territory.owner) return; // alert user of already taken territory
                        territory.owner = $scope.players[$scope.turnOwner];
                        map.draw();
                        $scope.nextTurn();
                    }
                }
            },
            'deinit': function () {
                if (map.allTerritoriesOwned()) {
                    $scope.setState('placearmies');
                } else {
                    $scope.nextTurn(true);
                }
            }
        },
        'placearmies': {
            'init': function () {},
            'actions': {
                0: {
                    'init': function () {
                        var player = $scope.players[$scope.turnOwner];
                        player.armies.availableThisTurn += 1*player.armies.available;
                        player.armies.available = 0;
                    },
                    'mapClick': function (e) {
                        var territory = map.getTerritoryAt(map.toMapPoint([e.pageX, e.pageY]));
                        if (!territory) return; // alert user of invalid territory
                        if (territory.owner != $scope.players[$scope.turnOwner]) return;
                        
                        if (!territory.armies) territory.armies = 0;
                        territory.armies++;
                        if (--territory.owner.armies.availableThisTurn <= 0) $scope.nextTurn();
                        map.draw();
                    }
                }
            },
            'deinit': function () {
                $scope.setState('play');
            }
        },
        'play': {
            'init': function () {},
            'actions': {
                0: {
                    'init': function () {
                        var player = $scope.players[$scope.turnOwner];
                        var allocation = Math.min(player.armies.available, 5);
                        player.armies.available -= allocation;
                        player.armies.availableThisTurn += allocation;
                    },
                    'mapClick': function (e) {
                        var territory = map.getTerritoryAt(map.toMapPoint([e.pageX, e.pageY]));
                        if (!territory) return;
                        if (territory.owner != $scope.players[$scope.turnOwner]) return;
                        
                        territory.armies++;
                        if (--territory.owner.armies.availableThisTurn <= 0) $scope.nextTurn();
                    }
                }, 
                1: {
                    'mapClick': function (e) {
                        
                    }
                }, 
                2: {
                    'mapClick': function (e) {
                        
                    }
                }, 
                3: {
                    'mapClick': function (e) {
                        
                    }
                }
            },
            'deinit': function () {
                $scope.setState('play');
            }
        }
    }
    
    $scope.setState('setup');
    
    var playerColors = ['#0971B2', '#FFFC19', '#B21212', '#11C422', '#AC15B2', '#B26012'];
    
    $scope.players = modelloader.get('players');
    for (var i=0 ; i < $scope.players.length ; i++) {
        $scope.players[i].color = (i < 5) ? playerColors[i] : generateRandomColor();
        $scope.players[i].armies = {
            'availableThisTurn': 0,
            'available': $scope.players[i].armies,
            'total': $scope.players[i].armies
        };
        $scope.players[i].getTerritories = function () {
            var owned = [];
            for (var j=0 ; j < map.polygons.length ; j++) {
                if (map.polygons[j].owner.name == this.name) {
                    owned.push(map.polygons[j]);
                }
            }
            return owned;
        };
    }
    
    var polygons = modelloader.get('territories');
    var map = new Map(document.getElementById('map'), polygons, {});
    map.draw();
    
    $scope.onMapClick = function (e) {
        $scope.states[$scope.state].actions[$scope.currentAction].mapClick(e);
    };
    
    $scope.automateTerritorySelection = function () {
        for (var i=0 ; i < map.polygons.length ; i++) {
            map.polygons[i].owner = $scope.players[i % $scope.players.length];
        }
        map.draw();
        $scope.states.setup.deinit();
    };
    
    $scope.automateArmySelection = function (playerIndex) {
        var player = $scope.players[playerIndex || $scope.turnOwner];
        
        var territories = player.getTerritories();
        var armies = player.armies.availableThisTurn;
        var each = Math.floor(armies / territories.length);
        
        if (!territories[0].armies) territories[0].armies = 0;
        territories[0].armies += armies - (territories.length-1)*each;
        
        for (var i=1 ; i < territories.length ; i++) {
            if (!territories[i].armies) territories[i].armies = 0;
            territories[i].armies += each;
        }
        map.draw();
        $scope.nextTurn();
    };
});
