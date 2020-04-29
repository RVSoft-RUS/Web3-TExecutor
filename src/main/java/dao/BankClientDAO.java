package dao;

import model.BankClient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankClientDAO {
    private static BankClientDAO INSTANCE;
    private Connection connection;

    private BankClientDAO()
    {
        this.connection = getMysqlConnection();
    }

    private static Connection getMysqlConnection() {
        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.cj.jdbc.Driver").newInstance());

            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").        //db type
                    append("localhost:").           //host name
                    append("3306/").                //port
                    append("db_example?").          //db name
                    append("user=root&").          //login
                    append("password=root").       //password
                    append("&serverTimezone=UTC");   //setup server time

            System.out.println("URL: " + url + "\n");

            Connection connection = DriverManager.getConnection(url.toString());
            return connection;
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    public static BankClientDAO getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new BankClientDAO();
        }
        return INSTANCE;
    }

    public List<BankClient> getAllBankClient() {
        List<BankClient> list = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            stmt.executeQuery("select * from bank_client");
            ResultSet result = stmt.getResultSet();
            while (result.next()) {
                long id = result.getLong(1);
                String name = result.getString(2);
                String password = result.getString(3);
                long money = result.getLong(4);
                BankClient client = new BankClient(id, name, password, money);
                list.add(client);
            }
        } catch (SQLException e) {

        }
        return list;
    }

    public boolean validateClient(String name, String password) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client where name='" + name + "'");
        ResultSet result = stmt.getResultSet();
        result.next();
        try {
            String passwordBase = result.getString(3);
            if (password.equals(passwordBase)) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        } finally {
            result.close();
            stmt.close();
        }
        return false;
    }

    public void updateClientsMoney(String nameFrom, String nameTo, Long transactValue) throws SQLException {
        try {
            connection.setAutoCommit(false);
            Statement stmt = connection.createStatement();
            stmt.execute("select * from bank_client where name='" + nameFrom + "'");
            ResultSet result = stmt.getResultSet();
            result.next();
            long moneyFrom = result.getLong(4);
            stmt.execute("select * from bank_client where name='" + nameTo + "'");
            result = stmt.getResultSet();
            result.next();
            long moneyTo = result.getLong(4);
            stmt.close();
            PreparedStatement pstmt = connection.prepareStatement
                    ("update bank_client set money = ? where name = ?");
            pstmt.setString(2, nameFrom);
            pstmt.setLong(1, moneyFrom - transactValue);
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = connection.prepareStatement
                    ("update bank_client set money = ? where name = ?");
            pstmt.setString(2, nameTo);
            pstmt.setLong(1, moneyTo + transactValue);
            pstmt.executeUpdate();
            pstmt.close();


            connection.commit();
        } finally {
            connection.setAutoCommit(true);
        }

    }

    public boolean isClientHasSum(String name, Long expectedSum) throws SQLException {
//        Statement stmt = connection.createStatement();
//        stmt.execute("select * from bank_client where name='" + name + "'");
//        ResultSet result = stmt.getResultSet();
//        result.next();
//        Long sum = result.getLong(4);
//        result.close();
//        stmt.close();
//        return sum >= expectedSum;
        TExecutor execT = new TExecutor();
        String query = "select * from bank_client where name='" + name + "'";
        return execT.execQuery(connection, query, result -> {
            result.next();
            return result.getLong(4) >= expectedSum;
                });
    }

    public long getClientIdByName(String name) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client where name='" + name + "'");
        ResultSet result = stmt.getResultSet();
        result.next();
        Long id = result.getLong(1);
        result.close();
        stmt.close();
        return id;
    }

    public BankClient getClientByName(String name) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client where name='" + name + "'");
        ResultSet result = stmt.getResultSet();
        try {
            result.next();
            Long id = result.getLong(1);
            name = result.getString(2);
            String password = result.getNString(3);
            long money = result.getLong(4);
            return new BankClient(id, name, password, money);
        } catch (SQLException e) {
            return  null;
        } finally {
            result.close();
            stmt.close();
        }
    }

    public void addClient(BankClient client) throws SQLException {
        String name = client.getName();
        String password = client.getPassword();
        long money = client.getMoney();

        Statement stmt = connection.createStatement();
        stmt.execute("insert into bank_client (name, password, money) values('" +
                name + "', '" + password + "', '" + money + "')");
        stmt.close();
    }

    public void createTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("create table if not exists bank_client (id bigint auto_increment, name varchar(256), password varchar(256), money bigint, primary key (id))");
        stmt.close();
    }

    public void dropTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS bank_client");
        stmt.close();
    }
}
