package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public class UserService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public AuthData register(UserData data) throws DataAccessException {
        userDAO.createUser(data.username(), data.password(), data.email());
        String token = authDAO.createAuth(data.username());
        return new AuthData(data.username(), token);
    }

    public AuthData login(String username, String password) throws DataAccessException{
        UserData user = userDAO.getUser(username);
        // Password is wrong
        if (!password.equals(user.password())) {
            throw new DataAccessException("Error: unauthorized");
        }
        String auth = authDAO.createAuth(username);
        return new AuthData(username, auth);
    }

    public void logout(String auth) throws DataAccessException {
        if (authDAO.getAuth(auth)) {
            authDAO.deleteAuth(auth);
        }
        else {
            throw new DataAccessException("Error: Auth does not exist");
        }
    }

    public Collection<GameData> listGames(String auth) throws DataAccessException {
        if (authDAO.getAuth(auth)) {
            return gameDAO.gamesList();
        }
        else {
            throw new DataAccessException("Error: Auth does not exist");
        }
    }

    public GameData createGame(String auth, String gameName) throws DataAccessException {
        if (authDAO.getAuth(auth)) {
            return gameDAO.buildGame(gameName);
        }
        else {
            throw new DataAccessException("Error: Auth does not exist");
        }
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
//    public LoginResult login(LoginRequest loginRequest) {}
//    public void logout(LogoutRequest logoutRequest) {}
}
