package edu.gatech.cs2340.risky;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import edu.gatech.cs2340.risky.database.ModelDb;

@WebListener("Load database before servlet request come in")
public class Database implements ServletContextListener {
    
    protected static ServletContext context;
    public static HashMap<String, ModelDb> databases = init();
    
    @SuppressWarnings("unchecked")
    private static HashMap<String, ModelDb> init() {
        if (context == null) {
            return null;
        }
        
        if (Database.databases == null) {
            context.setAttribute("databases", new HashMap<String, ModelDb>());
        }
        
        return (HashMap<String, ModelDb>) context.getAttribute("databases");
    }
    
    public static void clear() {
        context.setAttribute("databases", new HashMap<String, ModelDb>());
        Database.databases = (HashMap<String, ModelDb>) context.getAttribute("databases");
    }
    
    public static <T extends Model> ModelDb<T> getDb(Class c) {
        return Database.getDb(c, null);
    }

    public static <T extends Model> ModelDb<T> getDb(Class c, ModelDb<T> defaultDatabase) {
        ModelDb<T> database = Database.databases.get(c.getName());
        if (database == null && defaultDatabase != null) {
            database = defaultDatabase.getInstance();
            Database.setDb(c, database);
        }
        return database;
    }
    
    public static <T extends Model> void setDb(Class c, ModelDb<T> database) {
        Database.databases.put(c.getName(), database);
    }
    
    public static <T extends Model> T getModel(Class c, Object id) {
        return Database.getModel(c, id, null);
    }
    
    public static <T extends Model> T getModel(Class c, Object id, ModelDb<T> defaultDatabase) {
        // leave an option for a null model so the controller is responsible to create one as it pleases
        ModelDb<T> database = getDb(c, defaultDatabase);
        if (database == null) {
            return null;// or throw NoSuchDatabaseException()?
        }
        return database.read(id);
    }
    
    public static <T extends Model> boolean setModel(T model) {
        ModelDb<T> database = getDb(model.getClass());
        if (database == null) {
            return false;// or throw NoSuchDatabaseException()?
        }
        database.update(model);
        return true;
    }
    
    public static <T extends Model> boolean delete(T model) {
        ModelDb<T> database = getDb(model.getClass());
        if (database == null) {
            return false;// or throw NoSuchDatabaseException()?
        }
        database.delete(model);
        return true;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Database.context = sce.getServletContext();
        Database.databases = init();
    }
    
}
