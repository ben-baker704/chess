package dataaccess;
import model.AuthData;
import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public boolean getAuth(String token) throws DataAccessException {
        return auths.containsKey(token);
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        String auth = UUID.randomUUID().toString();
        auths.put(auth, new AuthData(username, auth));
        return auth;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        auths.clear();
    }
}
