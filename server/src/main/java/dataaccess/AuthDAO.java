package dataaccess;
import model.AuthData;

public interface AuthDAO {
    public String createAuth() throws DataAccessException;
    public boolean getAuth(String authToken) throws DataAccessException;
    public void deleteAuth(String authToken) throws DataAccessException;
    public void clear() throws DataAccessException;
}
