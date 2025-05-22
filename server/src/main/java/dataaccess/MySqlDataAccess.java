package dataaccess;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess {

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
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
              blackUsername VARCHAR(256)
            )
            """
    };

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
