package edu.gatech.cs2340.risky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.gatech.cs2340.risky.Util.Methods;
import edu.gatech.cs2340.risky.Util.Objects;
import edu.gatech.cs2340.risky.Util.Strings;
import edu.gatech.cs2340.risky.api.Error;
import edu.gatech.cs2340.risky.api.annotations.ApiParams;

public abstract class ApiServlet extends RiskyServlet {
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse httpResponse) {
        System.out.println("---------------------------------------------------------");
        Object response = null;
        
        try {
            String methodName = this.getMethodName(request);
            
            System.out.println("Got method: " + methodName);
            System.out.print("Segments: ");// just for debugging
            for (String segment : this.getPathSegments(request)) { System.out.print(segment + " "); }
            System.out.println();
            
            Map<String, Object> arguments = this.getArguments(request);// get the arguments from the request
            Method method = this.getMethod(methodName, arguments);// find the method with similar signature
            Object[] mappedArguments = Methods.mapArguments(arguments, method.getAnnotation(ApiParams.class).value());// order the arguments to match
            response = method.invoke(this, mappedArguments);
            
        } catch (Exception e) {
            response = this.handleException(request, e);
        }
        
        this.dispatch(httpResponse, response);
    }
    
    protected Map<String, Object> getArguments(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        Map<String, Object> arguments = new HashMap<String, Object>();
        String[] values;
        
        for (Map.Entry<String, String[]> parameter : parameters.entrySet()) {
            values = parameter.getValue();
            arguments.put(parameter.getKey(), values[values.length-1]);
        }
        
        arguments.put("request", request);
        
        return arguments;
    }
    
    protected Method getMethod(String methodName, Map<String, Object> availableArguments) throws NoSuchMethodException {
        Method[] methods = Objects.getMethodsByName(this.getClass(), methodName);
        
        if (methods.length == 0) {
            throw new NoSuchMethodException("No methods by name " + methodName);
        }
        
        int leastDifference = Integer.MAX_VALUE, optionDifference;
        Method bestMatch = null;
        
        for (String s : availableArguments.keySet()) { System.out.print(s + " "); } System.out.println(" <- available arguments");
        
        for (Method method : methods) {
            ApiParams params = method.getAnnotation(ApiParams.class);
            if (params == null) {
                System.out.println(method.getName() + " has no apiparams");
            } else {
                for (String s : params.value()) { System.out.print(s + " "); } System.out.println(" <- apiparams");
            }
            optionDifference = Methods.getArgumentDifference(availableArguments, params.value());
            if (this.isOneOfCrud(methodName)) {
                optionDifference = 1;
            }
            if (optionDifference < leastDifference) {
                bestMatch = method;
                leastDifference = optionDifference;
            }
        }
        System.out.println(leastDifference);
        if (bestMatch == null || leastDifference > 2) {
            throw new NoSuchMethodException("No method " + methodName + " with similar enough arguments");
        }
        
        return bestMatch;
    }
    
    public String getPayload(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            return "{}";
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    return "{}";
                }
            }
        }
        
        return stringBuilder.toString();
    }
    
    protected Object getPayloadObject(HttpServletRequest request, Class objectClass) {
        try {
            return new ObjectMapper().readValue(this.getPayload(request), objectClass);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new Object();
        }
    }
    
    protected Map<String, Object> getPayloadFieldMap(HttpServletRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        
        String payload = getPayload(request);
        
        if (payload == null || "".equals(payload) || "{}".equals(payload)) {
            return fieldMap;
        }
        
        try {
            fieldMap = mapper.readValue(payload, Map.class);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return fieldMap;
    }
    
    protected void dispatch(HttpServletResponse response, Object model) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(this.discoverResponseCode(model));
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            //gson.registerTypeAdapter(Lobby.class, new LobbyAdapter());
            String responseText = mapper.writeValueAsString(model);
            response.getWriter().write(responseText);
            
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
    
    protected int discoverResponseCode(Object model) {
        if (model == null) {// avoid having to null-check on elses below
            
        } else if (model.getClass().equals(Error.class)) {
            return ((Error) model).code;
            
        } else if (Throwable.class.isAssignableFrom(model.getClass())) {
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
        
        return HttpServletResponse.SC_OK;
    }
    
    protected Object handleException(HttpServletRequest request, Throwable e) {
        
        // remap e into something more debugging friendly
        
        e.printStackTrace();
        return e;
    }
    
    protected String getMethodName(HttpServletRequest request) {
        
        String[] segments = this.getPathSegments(request);
        String method = null;
        
        /*if (segments.length > 0) {
            method = segments[segments.length-1];
            method = Strings.dashedToCamel(method);
        } else */if (request.getParameter("action") != null) {
            method = request.getParameter("action");
            method = Strings.dashedToCamel(method);
        } else {
            method = request.getMethod().toLowerCase();
            if (method.equals("post")) {
                method = "create";
                
            } else if (method.equals("put")) {
                method = "update";
                
            } else if (!method.equals("delete")) {
                method = "read";// for get and anything else
            }
        }
        
        return method;
    }
    
    protected boolean isOneOfCrud(String s) {
        if ("create".equalsIgnoreCase(s) || "read".equalsIgnoreCase(s) || "update".equalsIgnoreCase(s) || "delete".equalsIgnoreCase(s)) {
            return true;
        }
        return false;
    }
    
    /*
     * Boilerplate method mapping, so the API is more CRUD-y
     */
    public Object read(HttpServletRequest request) throws Exception {
        return null;
    }
    
    public Object create(HttpServletRequest request) throws Exception {
        return null;
    }
    
    public Object update(HttpServletRequest request) throws Exception {
        return null;
    }
    
    public Object delete(HttpServletRequest request) throws Exception {
        return null;
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        this.service(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        this.service(request, response);
    }
    
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        this.service(request, response);
    }
    
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        this.service(request, response);
    }
    
}
