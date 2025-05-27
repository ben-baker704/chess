package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.JoinData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserData userData;
    private GameData gameData;
    private JoinData joinData;
    private AuthData authData;
    private UserService service = new UserService(new MemoryAuthDAO(), new MemoryGameDAO(), new MemoryUserDAO());

    @BeforeEach
    void initialize() {
        String hashPassword = BCrypt.hashpw("pass", BCrypt.gensalt());
        userData = new UserData("user", hashPassword, "mail");
        gameData = new GameData(1, "white",
                "black", "name", new ChessGame());
        joinData = new JoinData("WHITE", "1");
        authData = new AuthData("user", "authorized");
    }


    @Test
    void registerPositive() throws DataAccessException {
        AuthData data = service.register(userData);
        Assertions.assertEquals("user", data.username());
    }

    @Test
    void registerNegative() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () ->
                service.register(new UserData(null, null, null)));
    }

    @Test
    void loginPositive() throws DataAccessException {
        service.register(userData);
        AuthData data = service.login("user", "pass");
        Assertions.assertEquals(userData.username(), data.username());
    }

    @Test
    void loginNegative() throws DataAccessException {
        service.register(userData);
        Assertions.assertThrows(DataAccessException.class, () ->
                service.login("user", "invalid"));
    }

    @Test
    void logoutPositive() throws DataAccessException {
        AuthData data = service.register(userData);
        Assertions.assertDoesNotThrow(() -> service.logout(data.authToken()));
    }

    @Test
    void logoutNegative() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () ->
                service.logout("invalid"));
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        AuthData data = service.register(userData);
        Assertions.assertTrue(service.listGames(data.authToken()).isEmpty());
    }

    @Test
    void listGamesNegative() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () ->
                service.listGames("invalid"));
    }

    @Test
    void createGamePositive() throws DataAccessException {
        AuthData data = service.register(userData);
        Assertions.assertDoesNotThrow(() -> service.createGame(data.authToken(), "game"));
    }

    @Test
    void createGameNegative() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () ->
                service.createGame("invalid", "game"));
    }

    @Test
    void joinGamePositive() throws DataAccessException {
        AuthData data = service.register(userData);
        GameData game = service.createGame(data.authToken(), "game");
        Assertions.assertDoesNotThrow(() -> service.joinGame(data.authToken(),
                String.valueOf(game.gameID()), ChessGame.TeamColor.BLACK));
    }

    @Test
    void joinGameNegative() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () ->
                service.joinGame("Invalid", "Invalid", ChessGame.TeamColor.BLACK));
    }

    @Test
    void clear() throws DataAccessException {
        AuthData data = service.register(userData);
        Assertions.assertDoesNotThrow(() -> service.clear());
    }
}