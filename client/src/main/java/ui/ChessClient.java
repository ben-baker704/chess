package ui;

import server.ServerFacade;

import java.util.Arrays;

public class ChessClient {

    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.PRELOGIN;

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
                default -> help();
            };
        }
        catch (Exception e) {
            return e.getMessage();
        }
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
