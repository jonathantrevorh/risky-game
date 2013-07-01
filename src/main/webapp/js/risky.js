var risky = angular.module('risky', []);
risky.service('asynchttp', function ($q,$http) {
    this.get = function(key) {
        if(key==null){
            console.error("No key for http request to get");
            return null;
        }
        var deferred = $q.defer();
        if(key=="players"){
            $http({"method":'GET',"url":"../game","params":{"info":"name"}}).then(function(r){
                deferred.resolve(angular.fromJson(r.data).players);
            });
        }
        if(key=="map") {
            $http({"method":'GET',"url":"../js/map.json"}).then(function(r){
                polygons=angular.fromJson(r.data).map;
                deferred.resolve(polygons);
            });
        }
        if(key=="turn") {
            $http({"method":'GET',"url":"../js/map.json"}).then(function(r){
                polygons=angular.fromJson(r.data).map;
                deferred.resolve(polygons);
            });
        }
        return deferred.promise;
    }
    this.set = function(data){
        //TODO: will be sending data to servlet after every turn
    }
});

risky.filter("iif", function () {// fake ternary operator in {{}}'d things
    return function(input, trueValue, falseValue) {
        return input ? trueValue : falseValue;
    };
});

// Array Remove - By John Resig (MIT Licensed)
Array.prototype.remove = function(from, to) {
    var rest = this.slice((to || from) + 1 || this.length);
    this.length = from < 0 ? this.length + from : from;
    return this.push.apply(this, rest);
};

function Map(canvas, polygons_promise, config) {
    this.canvas = canvas;
    this.context = this.canvas.getContext("2d");
    this.polygons = polygons_promise; // Only a promise, not the actual polygons object
    this.config = {
        "scale": config.scale || 10
    };
}

Map.prototype.labelPolygon = function (polygon, label) { // Do not call this without knowing you have the actual polygon, not just a promise
    //hackish?
    var xa=new Array();ya=new Array(),x=0,y=0;
    for(i=0;i<polygon.vertexes.length;i++){
        xa.push(polygon.vertexes[i][0]);
        ya.push(polygon.vertexes[i][1]);
    }
    xa.sort(function(a,b){return a-b});
    ya.sort(function(a,b){return a-b});
    x=(xa[xa.length-1]*this.config.scale+xa[0]*this.config.scale)/2;
    y=(ya[ya.length-1]*this.config.scale+ya[0]*this.config.scale)/2;
    this.context.fillStyle = "#000"
    this.context.fillText(label,x,y);
};

/*Map.prototype.inPolygon = function (polygon,x,y) { //TODO: Merge with Johnathan's functions
};*/

Map.prototype.drawPolygon = function (polygon) { // Make sure this is a polygon, not a promise
    this.context.fillStyle = (polygon.owner) ? polygon.owner.color : "#ddd"; //TODO: Owner now defined in player object
    this.context.beginPath();
    
    this.context.moveTo(polygon.vertexes[0][0]*this.config.scale - 0.5, polygon.vertexes[0][1]*this.config.scale - 0.5);
    
    for (var j=1 ; j < polygon.vertexes.length ; j++) {
        this.context.lineTo(polygon.vertexes[j][0]*this.config.scale - 0.5, polygon.vertexes[j][1]*this.config.scale - 0.5);
    }
    
    this.context.closePath();// pretends to "context.moveTo(first vertex)"
    this.context.fill();
    
    this.context.stroke();// commit the strokes to the canvas
    
    this.labelPolygon(polygon,"15");
};

Map.prototype.draw = function () {
    this.canvas.width = this.canvas.width;// clears the canvas
    this.context.strokeStyle = "#333";
    var polygon;
    that = this;
    this.polygons.then(function(ret_polygons){
        for (var i=0 ; i < ret_polygons.length ; i++) {
            that.drawPolygon(ret_polygons[i]);
        }
    });
};
