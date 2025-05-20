package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
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
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.delete("/db", this::clear);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);

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

    private Object login(Request req, Response res) throws DataAccessException {
        try {
            var log = new Gson().fromJson(req.body(), UserData.class);
            // Missing data
            if (log.username() == null || log.password() == null) {
                res.status(400);
                return new Gson().toJson(error.errorMessage("Error: Missing Data"));
            }
            var auth = service.login(log.username(), log.password());
            res.status(200);
            return new Gson().toJson(auth);
        }
        catch (Exception e) {
            res.status(401);
            return new Gson().toJson(error.errorMessage("Error: Invalid password"));
        }
    }

    private Object logout(Request req, Response res) throws DataAccessException {
        try {
            String auth = req.headers("authorization");
            service.logout(auth);
            res.status(200);
            return "{}";
        }
        catch (Exception e) {
            res.status(401);
            return new Gson().toJson(error.errorMessage("Error: not authorized"));
        }
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        try {
            String auth = req.headers("authorization");
            var games = service.listGames(auth);
            var putGames = new HashMap<String, Object>();
            putGames.put("games", games);
            res.status(200);
            return new Gson().toJson(putGames);
        }
        catch (Exception e) {
            res.status(401);
            return new Gson().toJson(error.errorMessage("Error: not authorized"));
        }
    }

    private Object createGame(Request req, Response res) throws DataAccessException {
        try {
            String auth = req.headers("authorization");
            var gameName = new Gson().fromJson(req.body(), GameData.class);
            if (gameName.gameName() == null) {
                res.status(400);
                return new Gson().toJson(error.errorMessage("Error: Missing Data"));
            }
            HashMap<String, Integer> result = new HashMap<>();
            GameData game = service.createGame(auth, gameName.toString());
            result.put("gameID", game.gameID());
            res.status(200);
            return new Gson().toJson(result);
        }
        catch (Exception e) {
            res.status(401);
            return new Gson().toJson(error.errorMessage("Error: not authorized"));
        }
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        service.clear();
        res.status(200);
        return "{}";
    }
}
