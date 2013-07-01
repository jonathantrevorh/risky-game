risky.controller('LobbyController', function ($scope,$http,$q){
    $scope.playerCount = 0;
    $scope.players = [];
    $scope.polys = 23; //Safety value
    var deferred=$q.defer();
    $http.get("js/map.json").success(function(r){deferred.resolve(r);});
    deferred.promise.then(function(r){jsonmap=angular.fromJson(r);$scope.polys=jsonmap.map.length;});
    /*$scope.lobby = {
        'title': ''
    };*/
     
    $scope.addPlayer = function () {
        if ($scope.playerName.length <= 0) return;
        var name = $scope.playerName;
        for (var id in $scope.players) {
            if ($scope.players[id] === name) return;// disallow players with the same name
        }
        $scope.players.push(name);
        $scope.playerName = '';
    };
    
    $scope.removePlayer = function (index) {
        $scope.players.remove(index);
    };
    
    $scope.buildDefaultLobby = function () {
        $scope.lobby.title = 'House of the Pizza Power';
        $scope.players = ['Lenny', 'Ralph', 'Don', 'Mikey'];
    };
    
    $scope.startMatch = function () {
        var polyobj = document.getElementById('submitForm').appendChild(document.createElement("input"));
        polyobj.setAttribute("type","hidden");
        polyobj.setAttribute("name","polys");
        polyobj.setAttribute("value",$scope.polys);
        document.getElementById('submitForm').submit();
    };
});
