<%@ page import="edu.gatech.cs2340.risky.model.Game" %>
<%@ page import="edu.gatech.cs2340.risky.model.Player" %>
<%@ page import="java.util.*" %>

<% Game game = (Game) request.getAttribute("game"); %>

<html ng-app="risky">
<head>
    <link rel="stylesheet" type="text/css" href="/risky/css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="/risky/css/style.css">
    <script type="text/javascript" src="/risky/js/angular.min.js"></script>
    <script type="text/javascript" src="/risky/js/risky.js"></script>
    <script type="text/javascript" src="/risky/js/game.js"></script>
    <title>Risky Web App</title>
</head>
<body ng-controller="GameController">
    <% if (game == null) { %>
        <a href="/risky/lobby">Create a lobby first</a>
    <% } else { %>
        <canvas id="map" width="800" height="500"></canvas>
        <div class="row-fluid">
        <hr>
            <div class="span2">
                <div ng-repeat="player in players">
                    <span class="color-swatch" style="background-color: {{player.color}}"></span>
                    <span ng-class="$index == turnOwner | iif : 'label' : ''">{{player.name}}</span>
                </div>
            </div>
            <div class="span10 row-fluid no-left-gutter action-bar" ng-show="state == 'play'" ng-class="action{{currentAction}}">
                <div class="span3">
                    <h4>Place armies</h4>
                    <div>Click territories to place an army</div>
                    <div>Hit 'u' to undo a placement</div>
                </div>
                <div class="span3">
                    <h4>Attack</h4>
                    <div>Attack who?</div>
                    <div>Where from?</div>
                    <div>Repeat as necessary</div>
                </div>
                <div class="span3">
                    <h4>Fortify</h4>
                    <div>You have one shot to seize everything you ever wanted. Capture it; don't let it slip!</div>
                </div>
                <div class="span3">
                    <h4>End turn</h4>
                    <div>Call it quits already</div>
                    <a class="btn btn-primary btn-mini" ng-click="nextTurn()">End</a>
                </div>
            </div>
        </div>
    <% } %>
</body>
</html>
