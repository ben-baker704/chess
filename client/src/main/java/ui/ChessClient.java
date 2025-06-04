package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ChessClient {

    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.PRELOGIN;
    private final Map<String, String> auths = new HashMap<>();
    private final Map<Integer, String> games = new HashMap<>();
    private String userAuth = null;
    private final Map<Integer, Integer> gameIDs = new HashMap<>();

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "quit" -> "quit";
                default -> help();
            };
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    public String register(String... params) throws Exception {
        assertSignedOut();
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            UserData userData = new UserData(username, password, email);
            AuthData authData = server.register(userData);
            auths.put(username, authData.authToken());
            userAuth = authData.authToken();
            visitorName = username;
            this.state = State.POSTLOGIN;
            return String.format("Registered as %s", visitorName);
        }
        throw new Exception("Error: expected three parameters");
    }

    public String login(String... params) throws Exception {
        assertSignedOut();
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            AuthData authData = server.login(username, password);
            auths.put(username, authData.authToken());
            userAuth = authData.authToken();
            visitorName = username;
            this.state = State.POSTLOGIN;
            return String.format("You signed in as %s.", visitorName);
        }
        throw new Exception("Error: expected two parameters");
    }

    public String logout() throws Exception {
        assertSignedIn();
        server.logout(userAuth);
        auths.remove(visitorName);
        userAuth = null;
        visitorName = null;
        this.state = State.PRELOGIN;
        return "Logged out";
    }

    public String create(String... params) throws Exception {
        assertSignedIn();
        if (params.length == 1) {
            String gameName = params[0];
            GameData game = server.createGame(userAuth, gameName);
            games.put(game.gameID(), gameName);
            return String.format("Created game '%s'", gameName);
        }
        throw new Exception("Error: expected one parameter");
    }

    public String list() throws Exception {
        assertSignedIn();
        var games = server.listGames(userAuth);
        var result = new StringBuilder();
        int counter = 1;
        gameIDs.clear();
        for (var game : games) {
            result.append(String.format("%d: %s\n ", counter, game.gameName()));
            gameIDs.put(counter, game.gameID());
            counter++;
        }
        return result.toString();
    }

    public String join(String... params) throws Exception {
        assertSignedIn();
        if (params.length == 2) {
            int index = Integer.parseInt(params[0]);
            if (!gameIDs.containsKey(index)) {
                throw new Exception("Error: Game does not exist");
            }
            int gameID = gameIDs.get(index);
            ChessGame.TeamColor color;
            if (params[1].equalsIgnoreCase("BLACK")) {
                color = ChessGame.TeamColor.BLACK;
            }
            else if (params[1].equalsIgnoreCase("WHITE")) {
                color = ChessGame.TeamColor.WHITE;
            }
            else {
                throw new Exception("Error: color does not exist");
            }
            server.joinGame(userAuth, String.valueOf(gameID), color);
            ChessDisplay display = new ChessDisplay();
            display.draw(new ChessBoard(), color);
            return "Successfully joined game";
        }
        throw new Exception("Error: expected two parameters");
    }

    public String observe(String... params) throws Exception {
        assertSignedIn();
        if (params.length == 1) {
            int index = Integer.parseInt(params[0]);
            if (!gameIDs.containsKey(index)) {
                throw new Exception("Error: Game does not exist");
            }
            int gameID = gameIDs.get(index);
            ChessDisplay display = new ChessDisplay();
            display.draw(new ChessBoard(), ChessGame.TeamColor.WHITE);
            return "Observing game";
        }
        throw new Exception("Error: expected one parameter");
    }

    public String help() {
        if (state == State.PRELOGIN) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        }
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }

    private void assertSignedIn() throws Exception {
        if (state == State.PRELOGIN) {
            throw new Exception("Not signed in");
        }
    }

    private void assertSignedOut() throws Exception {
        if (state == State.POSTLOGIN) {
            throw new Exception("Already signed in");
        }
    }
}
