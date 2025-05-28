package dataaccess;
import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.JoinData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.UserService;

public class DatabaseUnitTest {

    private UserData userData;
    private GameData gameData;
    private JoinData joinData;
    private AuthData authData;
    private MySqlUserAccess sqlUser;
    private MySqlGameAccess sqlGame;
    private MySqlAuthAccess sqlAuth;

    @BeforeEach
    void initialize() {
        String hashPassword = BCrypt.hashpw("pass", BCrypt.gensalt());
        userData = new UserData("user", hashPassword, "mail");
        gameData = new GameData(1, "white",
                "black", "name", new ChessGame());
        joinData = new JoinData("WHITE", "1");
        authData = new AuthData("user", "authorized");
        sqlAuth = new MySqlAuthAccess();
        sqlGame = new MySqlGameAccess();
        try {
            sqlUser = new MySqlUserAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void deleteData() {
        try {
            sqlAuth.clear();
            sqlUser.clear();
            sqlGame.clear();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createUserPositive() throws DataAccessException {
        Assertions.assertDoesNotThrow(() ->
                sqlUser.createUser("user", "password", "mail"));
    }

    @Test
    void createUserNegative() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () ->
                sqlUser.createUser(null, "password", "mail"));
    }

    @Test
    void getUserPositive() throws DataAccessException {
        sqlUser.createUser("user", "password", "mail");
        Assertions.assertDoesNotThrow(() ->
                sqlUser.getUser("user"));
    }

    @Test
    void getUserNegative() throws DataAccessException {
        Assertions.assertDoesNotThrow(() ->
                sqlUser.getUser("user"));
    }

    @Test
    void createAuthPositive() throws DataAccessException {
        sqlUser.createUser("user", "password", "mail");
        Assertions.assertDoesNotThrow(() ->
                sqlAuth.createAuth("user"));
    }

    @Test
    void createAuthNegative() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () ->
                sqlAuth.createAuth("user"));
    }

    @Test
    void getAuthPositive() throws DataAccessException {
        sqlUser.createUser("user", "password", "mail");
        Assertions.assertDoesNotThrow(() ->
                sqlAuth.getAuth("user"));
    }

    @Test
    void getAuthNegative() throws DataAccessException {
        Assertions.assertDoesNotThrow(() ->
                sqlAuth.getAuth("invalid"));
    }

    @Test
    void getUsernamePositive() throws DataAccessException {
        sqlUser.createUser("user", "password", "mail");
        Assertions.assertDoesNotThrow(() ->
                sqlAuth.getUsername("user"));
    }

    @Test
    void getUsernameNegative() throws DataAccessException {
        Assertions.assertDoesNotThrow(() ->
                sqlAuth.getUsername("user"));
    }

    @Test
    void deleteAuthPositive() throws DataAccessException {
        sqlUser.createUser("user", "password", "mail");
        Assertions.assertDoesNotThrow(() ->
                sqlAuth.deleteAuth("user"));
    }

    @Test
    void deleteAuthNegative() throws DataAccessException {
        Assertions.assertDoesNotThrow(() ->
                sqlAuth.deleteAuth("user"));
    }

    @Test
    void buildGamePositive() throws DataAccessException {
        sqlUser.createUser("user", "password", "mail");
        Assertions.assertDoesNotThrow(() ->
                sqlGame.buildGame("game"));
    }

    @Test
    void buildGameNegative() throws DataAccessException {
        Assertions.assertDoesNotThrow(() ->
                sqlGame.buildGame("user"));
    }

    @Test
    void getGamePositive() throws DataAccessException {
        sqlGame.buildGame("game");
        Assertions.assertDoesNotThrow(() ->
                sqlGame.getGame("1"));
    }

    @Test
    void getGameNegative() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () ->
                sqlGame.getGame("user"));
    }

    @Test
    void gamesListPositive() throws DataAccessException {
        sqlGame.buildGame("game");
        Assertions.assertDoesNotThrow(() ->
                sqlGame.gamesList());
    }

    @Test
    void gamesListNegative() throws DataAccessException {
        Assertions.assertDoesNotThrow(() ->
                sqlGame.gamesList());
    }

    @Test
    void updateGamePositive() throws DataAccessException {
        sqlUser.createUser("user", "password", "mail");
        GameData game = sqlGame.buildGame("game");
        Assertions.assertDoesNotThrow(() ->
                sqlGame.updateGame(String.valueOf(game.gameID()), ChessGame.TeamColor.BLACK, "user"));
    }

    @Test
    void updateGameNegative() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () ->
                sqlGame.updateGame("1", ChessGame.TeamColor.BLACK, "user"));
    }

    @Test
    void clearPositive() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> sqlAuth.clear());
        Assertions.assertDoesNotThrow(() -> sqlGame.clear());
        Assertions.assertDoesNotThrow(() -> sqlUser.clear());
    }
}
