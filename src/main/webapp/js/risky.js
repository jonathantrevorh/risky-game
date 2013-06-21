var risky = angular.module('risky', []);
risky.service('modelloader', function () {
    // loads properties of <script type="text/model-data">{this: "object"}</script> into the local data
    var data = {};
    
    this.get = function (key) {
        var modelData = document.querySelectorAll("script[type='text/model-data'][for='" + key + "']")[0];
        try {
            modelData = JSON.parse(modelData.innerText);
        } catch (e) {
            if (e instanceof SyntaxError) console.error('Could not parse model-data starting with \'' + modelData.innerText.replace(/\s+/, '').substring(0, 100) + '\'');
            else throw e;
        }
        data[key] = modelData;
        return data[key];
    };
});

risky.filter("iif", function () {// fake ternary operator in {{}}'d things
    //we must go deeper!
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

function Map(canvas, polygons, config) {
    this.canvas = canvas;
    this.context = this.canvas.getContext("2d");
    this.polygons = polygons;
    this.config = {
        "scale": config.scale || 10
    };
}

Map.prototype.labelPolygon = function (polygon) {
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
    this.context.fillText(polygon.owner.armies,x,y);

};

/*Map.prototype.inPolygon = function (polygon,x,y) { //Mouse coords
    var crossing=0;
    for(i=0;i<polygon.vertexes.length;i++) { //add check for last/first points
        if(y<polygon.vertexes[i][1] && y>polygon.vertexes[i+1][1] || y>polygon.vertexes[i][1] && y<polygon.vertexes[i+1][1]) { //if y-axis is within the edge range
            if(true) //if point intersects an edge along the x-axis
                crossing++;
    }
};*/

Map.prototype.drawPolygon = function (polygon) {
    this.context.fillStyle = (polygon.owner) ? polygon.owner.color : "#ddd";
    this.context.beginPath();
    
    this.context.moveTo(polygon.vertexes[0][0]*this.config.scale - 0.5, polygon.vertexes[0][1]*this.config.scale - 0.5);
    
    for (var j=1 ; j < polygon.vertexes.length ; j++) {;
        this.context.lineTo(polygon.vertexes[j][0]*this.config.scale - 0.5, polygon.vertexes[j][1]*this.config.scale - 0.5);
    }
    
    this.context.closePath();// pretends to "context.moveTo(first vertex)"
    this.context.fill();
    
    this.context.stroke();// commit the strokes to the canvas
    
    this.labelPolygon(polygon);
};

Map.prototype.draw = function () {
    this.canvas.width = this.canvas.width;// clears the canvas
    this.context.strokeStyle = "#333";
    var polygon;
    for (var i=0 ; i < this.polygons.length ; i++) {
        this.drawPolygon(this.polygons[i]);
    }
};
