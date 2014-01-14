risky
========================
Simplified version of the Hasbro favorite, as tasked by CS2340. 

CSS and JavaScript constitute significant portions of the projects thanks to Bootstrap and AngularJS.

The application is partitioned into business-logic-oriented backend Java API, and an AngularJS single-page site sitting on top. The game is played at one computer with multiple players. The game state is stored in in-memory "database"-y gloried hashmaps or arrays mutable through the CRUDish API. Angular receives the JSON state, and handles updating models and re-evaluating markup.

Per classroom assignment, the business logic was required to be handle on the server, but we could have a hay as to how far the front-end could reach. 

Requires
--------
- Environment Varilables
    - CATALINA\_HOME (from localhost:8080, usually /usr/share/tomcat7)
    - CATALINA\_BASE (from localhost:8080, usually /var/lib/tomcat7)

Fair warning
-----------
For the sake of simplicity, 
- "databases" are hashmaps kept in the servlet
- Business logic has been wrapped in API calls
- AngularJS Resources are pretty good for purely (and only purely) CRUD endpoints
