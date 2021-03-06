var risky = angular.module("risky", ["ngResource"]);
risky.service("Toast", function ($rootScope, $q) {
    var toast = {};
    if (!$rootScope.toasts) $rootScope.toasts = {};
    toast.send = function (id, type, message, bundle) {
        bundle = bundle || {};
        
        if (arguments.length === 1) {
            console.log(id);
        } else if (message === undefined) {
            message = id;// if no message is given, assume id is message, and they don't want to be able to overwrite it later
            id = undefined;
        }
        
        id = id || $rootScope.toasts.length;
        
        var type = (type === "error") ? "error" : "log";
        //console[type](message);
        type = (type === "error") ? "danger" : "info";
        
        if (message.data && message.data.cause && message.data.cause.message) message = message.data.cause.message;
        if (message.data && message.data.message) message = message.data.message;
        if (message.message) message = message.message;
        
        var q = $q.defer();
        
        bundle.type = type;
        bundle.message = message;
        bundle.q = q;
        bundle.hasEgo = (bundle.hasEgo != void 0) ? bundle.hasEgo : false;
        bundle.closeable = (bundle.closeable != void 0) ? bundle.closeable : true;
        bundle.close = function () {
            delete $rootScope.toasts[id];
        };
        bundle.respond = function (value) {
            bundle.q.resolve(value);
            $rootScope.occlude = false;
            bundle.close();
        }
        
        if (bundle.hasEgo) {
            $rootScope.occlude = true;
        }
        $rootScope.toasts[id] = bundle;
        
        return q.promise;
     };
    toast.notify = function (id, message) {
        return toast.send(id, "notice", message);
    };
    toast.error = function (id, message) {
        return toast.send(id, "error", message);
    };
    toast.request = function (message, bundle) {
        bundle = bundle || {};
        bundle.isRequest = true;
        bundle.timeout = 0;
        bundle.closeable = false;
        bundle.hasEgo = true;
        return toast.send(void 0, "warning", message, bundle);
    };
    return toast;
    
}).directive("swatch", function ($timeout) {
    return {// <swatch color="#00ff00"></swatch> to <span class="color-swatch" background-color="#00ff00"></span>
        restrict: "E",
        replace: true,
        template: "<span class=\"color-swatch\"></span>",
        link: function ($scope, $element, $attrs) {
            $scope.$watch($attrs.color, function () {
                $element.css("backgroundColor", $attrs.color);
            });
        }
    };
    
}).factory("Map", function ($resource) {
    return $resource("/risky/api/map");
    
}).factory("Player", function ($resource) {
    return $resource("/risky/api/player/:id", {
        id: "@id"
    }, {
        "update": {method: "PUT"},
        "attack": {method: "POST", params: {action: "attack", attacking: "", defending: "", attackingDie: 0, defendingDie: 0}},
        "updateArmies": {method: "POST", params: {action: "updateArmies", armies: 0}},
        "fortify": {method: "POST", params: {action: "fortifyTerritory", from: "", to: "", armies: 0}},
        "seize": {method: "POST", params: {action: "seizeTerritory", territory: ""}},
        "quit": {method: "POST", params: {action: "quit"}}
    });
    
}).factory("Lobby", function ($resource) {
    return $resource("/risky/api/lobby/:id", {
        id: "@id"
    }, {
        "update": {method: "PUT"},
        "automateTerritorySelection": {method: "POST", params: {action: "automateTerritorySelection"}}
    });
    
}).factory("TurnOrder", function ($resource) {
    return $resource("/risky/api/turnOrder", {}, {
        "nextAction": {method: "POST", params: {action: "nextAction"}},
        "nextTurn": {method: "POST", params: {action: "nextTurn"}},
        "automateSetup": {method: "POST", params: {action: "automateSetup"}},
        "automatePlacearmies": {method: "POST", params: {action: "automatePlacearmies"}}
    });
});

risky.filter("iif", function () {// ternary operator for {{}}'d things
    return function(input, trueValue, falseValue) {
        return input ? trueValue : falseValue;
    };
}).filter("oor", function () {// for something || default
    return function(input, elseValue) {
        return (input != void 0) ? input : elseValue;
    };
});

// Array Remove - By John Resig (MIT Licensed)
Array.prototype.remove = function(from, to) {
    var rest = this.slice((to || from) + 1 || this.length);
    this.length = from < 0 ? this.length + from : from;
    return this.push.apply(this, rest);
};

function generateRandomColor() {
    return "#"+Math.floor(Math.random()*16777215).toString(16);
}

function pointInPoly(point, polygon) {
    var i, j, c = false, vertexes = polygon.vertexes;
    for (i = 0, j = vertexes.length - 1; i < vertexes.length; j = i++) {
        if (((vertexes[i][1] > point[1]) != (vertexes[j][1] > point[1])) && (point[0] < (vertexes[j][0] - vertexes[i][0]) * (point[1] - vertexes[i][1]) / (vertexes[j][1] - vertexes[i][1]) + vertexes[i][0])) {
            c = !c;
        }
    }
    return c;
}
