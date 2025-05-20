package dataaccess;
import model.AuthData;
import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private int nextId = 1;
    final private HashMap<Integer, String> auths = new HashMap<>();

    @Override
    public boolean getAuth(String authToken) throws DataAccessException {
        return auths.containsValue(authToken);
    }

    @Override
    public String createAuth() throws DataAccessException {
        String auth = UUID.randomUUID().toString();
        auths.put(nextId++, auth);
        return auth;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }
}
