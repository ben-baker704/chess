package dataaccess;

import java.sql.*;
import java.util.UUID;

public class MySqlAuthAccess implements AuthDAO {

    @Override
    public String createAuth(String username) throws DataAccessException {
        String auth = UUID.randomUUID().toString();
        String sql = "INSERT INTO auth (auth, username) VALUES (?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, auth);
            statement.setString(2, username);
            statement.executeUpdate();
            return auth;
        }
        catch (Exception e) {
            throw new DataAccessException("Can't create auth token");
        }
    }

    @Override
    public boolean getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT COUNT(*) FROM auth WHERE authToken = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, authToken);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        catch (Exception e) {
            throw new DataAccessException("Can't verify auth token");
        }
        return false;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth WHERE authToken = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, authToken);
            statement.executeUpdate();
        }
        catch (Exception e) {
            throw new DataAccessException("Can't delete auth token");
        }

    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auth";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
        catch (Exception e) {
            throw new DataAccessException("Can't clear auth table");
        }
    }

    @Override
    public String getUsername(String authToken) throws DataAccessException {
        String sql = "SELECT username FROM auth WHERE authToken = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, authToken);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        }
        catch (Exception e) {
            throw new DataAccessException("Can't find username");
        }
        return null;
    }
}
