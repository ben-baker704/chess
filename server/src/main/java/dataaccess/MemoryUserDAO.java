package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    private int nextId = 1;
    private final HashMap<String, UserData> users = new HashMap<>();


    @Override
    public UserData createUser(String username, String password, String email) throws DataAccessException{
        UserData data;
        if (!users.containsKey(username)) {
            data = new UserData(username, password, email);
            users.put(username, data);}
        else {
            throw new DataAccessException("User already exists");
        }
        return data;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        return users.get(username);
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }
}
