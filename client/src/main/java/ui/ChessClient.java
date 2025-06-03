package ui;

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
