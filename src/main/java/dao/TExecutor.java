package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TExecutor {
    public <T> T execQuery(Connection connection, String query, TResultHandler<T> handler) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(query);
        ResultSet resultSet = stmt.getResultSet();
        T value = handler.handle(resultSet);
        resultSet.close();
        stmt.close();
        return value;
    }
}
