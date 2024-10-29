package dataaccess;

import java.sql.SQLException;

//Defines function to configure database to use in each DAO depending on the SQL statements given
public class ConfigureDatabase {
    public static void configureDatabase(String[] createStatements) throws SQLException, DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new SQLException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
