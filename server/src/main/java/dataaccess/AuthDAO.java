package dataaccess;
import model.AuthData;

public interface AuthDAO {
    public String createAuth(String username) throws DataAccessException;
    public boolean getAuth(String authToken) throws DataAccessException;
    public void deleteAuth(String authToken) throws DataAccessException;
    public void clear() throws DataAccessException;
}
