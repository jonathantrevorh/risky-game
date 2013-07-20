risky.controller("GameController", function ($scope, $q, Toast, Lobby, TurnOrder, Map, Player) {
    
    $scope.lobby = Lobby.get();
    $scope.turnOrder = TurnOrder.get();
    $scope.players = Player.query();
    $scope.map = Map.get();
    
    function getCurrentPlayer() {
        return $scope.players[$scope.turnOrder.playerIndex];
    }
    
    function nextAction() {
        $scope.turnOrder.$nextAction({}, function () {}, Toast.error);
    }
    
    $scope.states = {
        "setup": {
            0: {// seize territory
                "mapClick": function (e) {
                    var territory = map.getTerritoryAt(map.toMapPoint([e.pageX, e.pageY]));
                    
                    var d = $q.defer();
                    getCurrentPlayer().$seize({
                        "territory": territory.id
                    }, d.resolve, d.reject);
                    return d.promise;
                }
            }
        },
        "placearmies": {
            0: {// place army
                "mapClick": function (e) {
                    var territory = map.getTerritoryAt(map.toMapPoint([e.pageX, e.pageY]));
                    
                    var d = $q.defer();
                    getCurrentPlayer().$fortify({
                        to: territory.id,
                        armies: 4
                    }, d.resolve, d.reject);
                    return d.promise;
                }
            }
        },
        "play": {
            0: {// place armies
                "mapClick": function (e) {
                    var territory = map.getTerritoryAt(map.toMapPoint([e.pageX, e.pageY]));
                    
                    var d = $q.defer();
                    Toast.notify("fortify a territory");
                    getCurrentPlayer().$fortify({
                        to: territory.id,
                        armies: 4
                    }, d.resolve, d.reject);
                    return d.promise;
                }
            }, 
            1: {// attack
                "data": {},
                "mapClick": function (e) {
                    var data = $scope.states.play[1].data;
                    if (!data.attacking) {
                        Toast.notify(getCurrentPlayer().name + " is attacking!");
                        Toast.notify("chose attack origin");
                        var territory = map.getTerritoryAt(map.toMapPoint([e.pageX, e.pageY]));
                        if (map.getOwnerOfTerritory($scope.players, territory.id).name != getCurrentPlayer().name) throw new Exception("You do not own this territory");
                        data["attacking"] = territory;
                        
                        Toast.notify("Attacking from territory " + territory.id + ". Where are you attacking?");
                        
                    } else if (!data.defending) {// get the territory to defend from
                        var territory = map.getTerritoryAt(map.toMapPoint([e.pageX, e.pageY]));
                        if (map.getOwnerOfTerritory($scope.players, territory.id).name == getCurrentPlayer().name) throw new Exception("You own this territory");
                        data["defending"] = territory;
                        
                        Toast.notify("Defender, man your station, " + territory.id + " is being attacked!");
                        
                        function generateNumericButtons(min, max) {
                            var buttons = [];
                            for (var i=min ; i <= max ; i++) {
                                buttons.push({"name": i+"", "value": i});
                            }
                            return buttons;
                        }
                        
                        var maxAttackingArmies = map.getDeedForTerritory(data.attacking).armies-1;
                        var maxDefendingArmies = map.getDeedForTerritory(data.defending).armies;
                        
                        // then ask for the number of die to attack and defend with
                        Toast.request(undefined, map.getOwnerOfTerritory($scope.players, data.attacking).name + ", attack with how many die?", generateNumericButtons(1, Math.min(maxAttackingArmies, 3))).then(function (value) {
                            console.log(value);
                            data.attackingDie = value;
                            return Toast.request(undefined, map.getOwnerOfTerritory($scope.players, data.defending).name + ", defend with how many die?", generateNumericButtons(1, Math.min(maxDefendingArmies, 2)));
                            
                        }).then(function (value) {
                            console.log(value);
                            data.defendingDie = value;
                        });
                        
                    } else {
                        // send attack
                        var d = $q.defer();
                        getCurrentPlayer().$attack({
                            from: data.attacking.id,
                            to: data.defending.id, // could this just be $attack(data, d.resolve ....?
                            attackingDie: data.attackingDie,
                            defendingDie: data.defendingDie
                        }, d.resolve, d.reject);
                        return d.promise;
                    }
                }
            }, 
            2: {// fortify
                "mapClick": function (e) {
                    
                }
            },
            3: {// end turn
                "mapClick": function (e) {
                    
                }
            }
        }
    }
    
    $scope.automateSetup = function () {
        $scope.turnOrder.$automateSetup({}, function () {
            $scope.players = Player.query();
            
        }, Toast.error);
    };
    
    $scope.automatePlacearmies = function () {
        $scope.turnOrder.$automatePlacearmies({}, function () {
            $scope.players = Player.query();
        }, Toast.error);
    };
    
    $scope.onMapClick = function (e) {
        handleAction($scope.states[$scope.turnOrder.state][$scope.turnOrder.action].mapClick, [e]);
    }
    
    function handleAction(func, args) {
        var d = $q.defer();
        try {
            var value = func.apply(null, args);
            if (value === undefined || !value.hasOwnProperty("then")) return;
            d.resolve(value);
        } catch (e) {
            d.reject(e);
        }
        
        var a;
        
        d.promise.then(function () {
            var p = $q.defer();
            a = new Date().getTime();
            $scope.players = Player.query({}, p.resolve, p.reject);
            return p.promise;
            
        }).then(function (data) {
            console.log(new Date().getTime() - a);
            console.log($scope.players[0].territories);
            map.draw($scope.players);
            nextAction();
            
        }, function (error) {
            Toast.error(error);
        });
    }
    
    var map;
    $scope.map = Map.get({}, function () {
        $scope.players = Player.query({}, function () {
            map = new CanvasMap(document.getElementById("map"), $scope.map, $scope.players, {});
        });
    });
    
    $scope.$watch("players", function () {
        if (map) map.draw($scope.players);
    }, true);
    
});
