package dataaccess;

import chess.InvalidMoveException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlUserAccess implements UserDAO {

    public MySqlUserAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data");
        }
        return null;
    }

    @Override
    public UserData createUser(String username, String password, String email) throws DataAccessException {
        String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.setString(3, email);
            statement.executeUpdate();
            return new UserData(username, hashedPassword, email);
        }
        catch (Exception e) {
            if ("Failed to get connection".equals(e.getMessage())) {
                throw new DataAccessException("SQL Error");
            }
            throw new DataAccessException("Can't create user");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM user";
        executeUpdate(statement);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
              username VARCHAR(256) PRIMARY KEY,
              password VARCHAR(256) NOT NULL,
              email VARCHAR(256) NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
              authToken VARCHAR(256) PRIMARY KEY,
              username VARCHAR(256),
              FOREIGN KEY (username) REFERENCES user(username) ON DELETE CASCADE
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS game (
              gameID INT PRIMARY KEY AUTO_INCREMENT,
              gameName VARCHAR(256) NOT NULL,
              whiteUsername VARCHAR(256),
              blackUsername VARCHAR(256),
              json TEXT
            )
            """
    };

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) {ps.setString(i + 1, p);}
                    else if (param instanceof Integer p) {ps.setInt(i + 1, p);}
                    else if (param == null) {ps.setNull(i + 1, NULL);}
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to update database");
        }
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database");
        }
    }
}
