package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.google.gson.Gson;

public class MySqlGameAccess implements GameDAO {

    @Override
    public GameData buildGame(String gameName) throws DataAccessException {
        String sql = "INSERT INTO game (gameName, whiteUsername, blackUsername) VALUES (?, NULL, NULL)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, gameName);
            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    int gameID = rs.getInt(1);
                    return new GameData(gameID, null, null, gameName, new ChessGame());
                }
            }
        }
        catch (Exception e) {
            throw new DataAccessException("Can't create game");
        }
        return null;
    }

    @Override
    public GameData getGame(String gameID) throws DataAccessException {
        String sql = "SELECT * FROM game WHERE gameID = ?";
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, Integer.parseInt(gameID));
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("gameID");
                    String white = rs.getString("whiteUsername");
                    String black = rs.getString("blackUsername");
                    String name = rs.getString("gameName");
                    String json = rs.getString("json");
                    ChessGame game = new Gson().fromJson(json, ChessGame.class);
                    return new GameData(id, white, black, name, game);
                }
            }
        }
        catch (Exception e) {
            throw new DataAccessException("Can't find game");
        }
        return null;
    }

    @Override
    public Collection<GameData> gamesList() throws DataAccessException {
        String sql = "SELECT * FROM game";
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery()) {

            List<GameData> games = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("gameID");
                String white = rs.getString("whiteUsername");
                String black = rs.getString("blackUsername");
                String name = rs.getString("gameName");
                String json = rs.getString("json");
                ChessGame game = new Gson().fromJson(json, ChessGame.class);
                games.add(new GameData(id, white, black, name, game));
            }
            return games;
        }
        catch (Exception e) {
            throw new DataAccessException("Unable to list games");
        }
    }

    @Override
    public void updateGame(String gameID, ChessGame.TeamColor color, String username) throws DataAccessException {
        String column = (color == ChessGame.TeamColor.WHITE) ? "whiteUsername" : "blackUsername";
        String sql = "UPDATE game SET " + column + " = ? WHERE gameID = ?";
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setInt(2, Integer.parseInt(gameID));
            statement.executeUpdate();

        } catch (Exception e) {
            throw new DataAccessException("Can't update game");
        }

    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM game";
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Can't clear games");
        }
    }
}
