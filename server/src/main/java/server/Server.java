package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.*;

import java.util.HashMap;

public class Server {
    private ErrorMessage error = new ErrorMessage();
    private UserService service;
    private AuthDAO authDAO = new MemoryAuthDAO();
    private GameDAO gameDAO = new MemoryGameDAO();
    private UserDAO userDAO = new MemoryUserDAO();

    public Server() {
        this.service = new UserService(authDAO, gameDAO, userDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.delete("/db", this::clear);
        //This line initializes the server and can be removed once you have a functioning endpoint 
//        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object register(Request req, Response res) throws DataAccessException {
        try {
            var user = new Gson().fromJson(req.body(), UserData.class);
            // Missing data
            if (user.username() == null || user.email() == null || user.password() == null) {
                res.status(400);
                return new Gson().toJson(error.errorMessage("Error: Missing Data"));
            }
            var auth = service.register(user);
            res.status(200);
            return new Gson().toJson(auth);}
        catch (Exception e) {
            res.status(403);
            return new Gson().toJson(error.errorMessage("Error: Bad Request"));
        }
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        service.clear();
        res.status(200);
        return "{}";
    }
}
