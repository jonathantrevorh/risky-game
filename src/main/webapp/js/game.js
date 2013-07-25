risky.controller("GameController", function ($scope, $q, Toast, Lobby, TurnOrder, Map, Player) {
    
    $scope.lobby = Lobby.get();
    $scope.turnOrder = TurnOrder.get();
    $scope.players = Player.query();
    
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
                        return getAttackingTerritory(e);
                        
                    } else if (!data.defending) {// get the territory to defend from
                        return getTargetTerritory(e);
                    }
                    
                    function getAttackingTerritory(e) {
                        var territory = map.getTerritoryAt(map.toMapPoint([e.pageX, e.pageY]));
                        var name = getPlayerName(territory);
                        if (name != getCurrentPlayer().name) throw new Error("You do not own this territory");
                        var maxAttackingArmies = map.getDeedForTerritory(territory).armies;
                        if (maxAttackingArmies <= 1) throw new Error("Not enough dice");
                        if(!hasAttackable(territory)) throw new Error("No enemies around here");
                        data["attacking"] = territory;
                        
                        Toast.notify("Attacking from territory #" + territory.id + ". " + name + ", where are you attacking?");
                    }
                    
                    function requestAttackingDie() {
                        console.log("Deed:");
                        console.log(map.getDeedForTerritory(data.attacking));
                        console.log(map.getDeedForTerritory(data.attacking.id));
                        var maxAttackingArmies = map.getDeedForTerritory(data.attacking).armies-1;
                        return Toast.request(getPlayerName(data.attacking) + ", attack with how many dice?", {
                            requestType: "select",
                            options: optionRangeFactory(1, Math.min(maxAttackingArmies, 3))
                        });
                    }
                    
                    function requestDefendingDie() {
                        var maxDefendingArmies = map.getDeedForTerritory(data.defending).armies;
                        return Toast.request(getPlayerName(data.defending) + ", defend with how many dice?", {
                            requestType: "select",
                            options: optionRangeFactory(1, Math.min(maxDefendingArmies, 2))
                        });
                    }
                    
                    function getTargetTerritory(e) {
                        var territory = map.getTerritoryAt(map.toMapPoint([e.pageX, e.pageY]));
                        if (getPlayerName(territory) == getCurrentPlayer().name) throw new Error("You own this territory");
                        if (data.attacking.adjacencies.indexOf(territory.id) < 0) throw new Error("That territory is not adjacent");
                        data["defending"] = territory;
                        
                        Toast.notify(getPlayerName(data.defending) + ", man your station, #" + territory.id + " is being attacked!");

                        // then ask for the number of dice to attack and defend with
                        return requestAttackingDie().then(function (attackingDie) {
                            data.attackingDie = attackingDie;
                            return requestDefendingDie();
                            
                        }).then(function (defendingDie) {
                            data.defendingDie = defendingDie;
                            return sendAttack(e);
                        }).then(function (){
                            return updateArmies();
                        });
                    }
                    function hasAttackable(territory){
                        for(var i=0;i<territory.adjacencies.length;++i){
                            if (map.getOwnerOfTerritory($scope.players,territory.adjacencies[i]).name != getCurrentPlayer().name) return true;
                        }
                        return false
                    }
                    
                    function sendAttack(e) {
                        // send attack
                        var d = $q.defer();
                        getCurrentPlayer().$attack({
                            attacking: data.attacking.id,
                            defending: data.defending.id, // could this just be $attack(data, d.resolve ....?
                            attackingDie: data.attackingDie,
                            defendingDie: data.defendingDie
                        }, d.resolve, d.reject);
                        
                        $scope.states.play[1].data = {};
                        
                        return d.promise;
                    }
                    
                    function getPlayerName(territory) {
                        return map.getOwnerOfTerritory($scope.players, territory.id).name;
                    }
                    
                    function optionRangeFactory(min, max) {
                        var options = [];
                        if (min <= max) {
                            for (var i=min ; i <= max ; i++) {
                                options.push(i);
                            }
                        }
                        return options;
                    }

                    function updateArmies(){
                        for(var i=0;i<$scope.players.length;++i) {
                            var p_territories={};
                            p_territories=$scope.players[i].territories;
                            var armies=0;
                            for (var t in p_territories) {
                                armies+=p_territories[t].armies;
                            }

                            $scope.players[i].armies=armies;
                            $scope.players[i].$updateArmies({
                                    "armies": armies
                            });
                        }
                    }
                },
                "skipAttacks": function () {
                    nextAction();
                }
            }, 
            2: {// fortify
                "data":{},
                "mapClick": function (e) {
                    var data = $scope.states.play[2].data; 
                    if (!data.source) {
                        return getSourceTerritory(e);
                        
                    } else if (!data.fortify) {
                        return getTargetTerritory(e);
                    }
                    
                    function getSourceTerritory(e) {
                        var territory = map.getTerritoryAt(map.toMapPoint([e.pageX, e.pageY]));
                        var name = getPlayerName(territory);
                        if (name != getCurrentPlayer().name) throw new Error("You do not own this territory");
                        var maxAttackingArmies = map.getDeedForTerritory(territory).armies;
                        if (maxAttackingArmies <= 1) throw new Error("Not enough armies");
                        if(!hasAdjacent(territory)) throw new Error("Nowhere to fortify from here");
                        data["source"] = territory;
                        
                        Toast.notify("Moving armies from territory #" + territory.id);
                    }
                    
                    function requestFortifyAmount() {
                        var maxFortifyArmies = map.getDeedForTerritory(data.source).armies-1;
                        return Toast.request(getPlayerName(data.source) + ", Move how many armies?", {
                            requestType: "select",
                            options: optionRangeFactory(1, maxFortifyArmies)
                        });
                    }
                    
                    
                    function getTargetTerritory(e) {
                        var territory = map.getTerritoryAt(map.toMapPoint([e.pageX, e.pageY]));
                        if (getPlayerName(territory) != getCurrentPlayer().name) throw new Error("You do not own this territory");
                        if (data.source.adjacencies.indexOf(territory.id) < 0) throw new Error("That territory is not adjacent");
                        data["fortify"] = territory;
                        
                        return requestFortifyAmount().then(function (fortifyAmount) {
                            data.fortifyAmount = fortifyAmount;
                            return fortify(e);
                        });
                    }

                    function hasAdjacent(territory){
                        for(var i=0;i<territory.adjacencies.length;++i){
                            if (map.getOwnerOfTerritory($scope.players,territory.adjacencies[i]).name == getCurrentPlayer().name) return true;
                        }
                        return false
                    }
                    
                    function fortify(e) {
                        var d = $q.defer();
                        getCurrentPlayer().$fortify({
                            from: data.source.id,
                            to: data.fortify.id,
                            armies: data.fortifyAmount,
                        }, d.resolve, d.reject);
                        $scope.states.play[2].data = {};
                        return d.promise;
                    }
                    
                    function getPlayerName(territory) {
                        return map.getOwnerOfTerritory($scope.players, territory.id).name;
                    }
                    
                    function optionRangeFactory(min, max) {
                        var options = [];
                        if (min <= max) {
                            for (var i=min ; i <= max ; i++) {
                                options.push(i);
                            }
                        }
                        return options;
                    }

                },
                "skipFortify": function () {
                    nextAction();
                }
            },
            3: {// end turn
                "mapClick": function (e) {}
            }
        },
        "gameover": {
            0: {
                "mapClick": function (e) {}
            }
        }
    }
    
    $scope.automateSetup = function () {
        $scope.turnOrder.$automateSetup({}, function () {
            $scope.players = Player.query();
            $scope.map = Map.get();
            
        }, Toast.error);
    };
    
    $scope.automatePlacearmies = function () {
        $scope.turnOrder.$automatePlacearmies({}, function () {
            $scope.players = Player.query();
            $scope.map = Map.get();
        }, Toast.error);
    };
    
    $scope.nextTurn = function () {
        $scope.turnOrder.$nextTurn();
        $scope.players = Player.query();
        $scope.map = Map.get();
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
        
        d.promise.then(function () {
            var p = $q.defer();
            $scope.players = Player.query({}, p.resolve, p.reject);
            console.log("Update players");
            return p.promise;
            
        }).then(function () {
            var p = $q.defer();
            $scope.map = Map.get({}, p.resolve, p.reject);
            console.log("Update map");
            return p.promise;
            
        }).then(function () {
            map.draw($scope.map, $scope.players);
            if (!($scope.turnOrder.state == "play" && $scope.turnOrder.action == 1)) {// don't jump to next action if attacking (therefore R16)
                nextAction();
            }
            
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
        if (map) map.draw($scope.map, $scope.players);
    }, true);
    
});
