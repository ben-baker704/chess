package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import model.JoinData;
import model.UserData;
import service.UserService;
import spark.*;

import java.util.HashMap;

public class Server {
    private ErrorMessage error = new ErrorMessage();
    private UserService service;
    private AuthDAO authDAO = new MySqlAuthAccess();
    private GameDAO gameDAO = new MySqlGameAccess();
    private UserDAO userDAO;

    public Server() {
        try {
            userDAO = new MySqlUserAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        this.service = new UserService(authDAO, gameDAO, userDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.webSocket("/ws", new WebSocketHandler(authDAO, gameDAO));

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.delete("/db", this::clear);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object register(Request req, Response res) {
        try {
            var user = new Gson().fromJson(req.body(), UserData.class);
            var auth = service.register(user);
            res.status(200);
            return new Gson().toJson(auth);}
        catch (DataAccessException e) {
            if (e.getMessage().equals("Error: Empty field")) {
                res.status(400);
                return new Gson().toJson(error.errorMessage("Error: Empty field"));
            }
            else if (e.getMessage().equals("Can't create user")) {
                res.status(403);
                return new Gson().toJson(error.errorMessage("Error: Bad Request"));
            }
            res.status(500);
            return new Gson().toJson(error.errorMessage("SQL Error Register"));

        }
        catch (Exception e) {
            res.status(500);
            return new Gson().toJson(error.errorMessage("Server Error"));
        }
    }

    private Object login(Request req, Response res) {
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
        catch (DataAccessException e) {
            if (!"Error: unauthorized".equals(e.getMessage())) {
                res.status(500);
                return new Gson().toJson(error.errorMessage("SQL Error Login"));}
            res.status(401);
            return new Gson().toJson(error.errorMessage("Error: Invalid password"));
        }
        catch (Exception e) {
            res.status(500);
            return new Gson().toJson(error.errorMessage("Server Error"));
        }
    }

    private Object logout(Request req, Response res) {
        try {
            String auth = req.headers("authorization");
            service.logout(auth);
            res.status(200);
            return "{}";
        }
        catch (DataAccessException e) {
            if ("SQL Error".equals(e.getMessage())) {
                res.status(500);
                return new Gson().toJson(error.errorMessage("SQL Error Logout"));}
            res.status(401);
            return new Gson().toJson(error.errorMessage("Error: not authorized"));
        }
        catch (Exception e) {
            res.status(500);
            return new Gson().toJson(error.errorMessage("Server Error"));
        }
    }

    private Object listGames(Request req, Response res) {
        try {
            String auth = req.headers("authorization");
            var games = service.listGames(auth);
            var putGames = new HashMap<String, Object>();
            putGames.put("games", games);
            res.status(200);
            return new Gson().toJson(putGames);
        }
        catch (DataAccessException e) {
            if (!"Error: Auth does not exist".equals(e.getMessage())) {
                res.status(500);
                return new Gson().toJson(error.errorMessage("SQL Error List Games"));}
            res.status(401);
            return new Gson().toJson(error.errorMessage("Error: not authorized"));
        }
        catch (Exception e) {
            res.status(500);
            return new Gson().toJson(error.errorMessage("Server Error"));
        }
    }

    private Object createGame(Request req, Response res) {
        try {
            String auth = req.headers("authorization");
            var gameName = new Gson().fromJson(req.body(), GameData.class);
            if (gameName.gameName() == null) {
                res.status(400);
                return new Gson().toJson(error.errorMessage("Error: Missing Data"));
            }
            HashMap<String, Integer> result = new HashMap<>();
            GameData game = service.createGame(auth, gameName.gameName());
            result.put("gameID", game.gameID());
            res.status(200);
            return new Gson().toJson(result);
        }
        catch (DataAccessException e) {
            if (!"Error: Auth does not exist".equals(e.getMessage())) {
                res.status(500);
                return new Gson().toJson(error.errorMessage("SQL Error Create Game"));}
            res.status(401);
            return new Gson().toJson(error.errorMessage("Error: not authorized"));
        }
        catch (Exception e) {
            res.status(500);
            return new Gson().toJson(error.errorMessage("Server Error"));
        }
    }

    private Object joinGame(Request req, Response res) {
        try {
            String auth = req.headers("authorization");
            var playerJoin = new Gson().fromJson(req.body(), JoinData.class);
            // Missing data
            if (!authDAO.getAuth(auth)) {
                res.status(401);
                return new Gson().toJson(error.errorMessage("Error: not authorized"));
            }
            if (playerJoin.gameID() == null || playerJoin.playerColor() == null) {
                res.status(400);
                return new Gson().toJson(error.errorMessage("Error: Missing Data"));
            }
            if (playerJoin.playerColor().equals("WHITE")) {
                service.joinGame(auth, playerJoin.gameID(), ChessGame.TeamColor.WHITE);}
            else if (playerJoin.playerColor().equals("BLACK")) {
                service.joinGame(auth, playerJoin.gameID(), ChessGame.TeamColor.BLACK);}
            else {
                res.status(400);
                return new Gson().toJson(error.errorMessage("Error: Missing Data"));
            }
            res.status(200);
            return "{}";
        }
        catch (Exception e) {
            if (!"SQL Error".equals(e.getMessage())) {
                res.status(403);
                return new Gson().toJson(error.errorMessage("Error: Already taken"));
            } else {
                res.status(500);
                return new Gson().toJson(error.errorMessage("Server Error"));
            }
        }

    }

    private Object clear(Request req, Response res) {
        try {
            service.clear();
            res.status(200);
            return "{}";
        }
        catch (Exception e) {
            res.status(500);
            return new Gson().toJson(error.errorMessage("Error: Could not clear"));
        }
    }
}
