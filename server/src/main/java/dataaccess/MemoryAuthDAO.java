package dataaccess;

public class MemoryAuthDAO implements AuthDAO{

    @Override
    public String getAuth(String authToken) throws DataAccessException {
        return "";
    }

    @Override
    public String createAuth() throws DataAccessException {
        return "";
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }
}
