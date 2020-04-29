package service;

import dao.BankClientDAO;
import exception.DBException;
import model.BankClient;
import java.sql.SQLException;
import java.util.List;

public class BankClientService {
    private static BankClientService instance;
    private BankClientDAO dao = BankClientDAO.getINSTANCE();

    private BankClientService() {
    }

    public static BankClientService getInstance() {
        if (instance == null) {
            instance = new BankClientService();
        }
        return instance;
    }

    public BankClient getClientByName(String name) {
        try {
            return dao.getClientByName(name);
        } catch (SQLException e) {
            return null;
        }
    }

    public List<BankClient> getAllClient() {
        return  dao.getAllBankClient();
    }

    public boolean deleteClient(String name) {
        return false;
    }

    public boolean addClient(BankClient client) throws DBException {
        try {
            BankClient clientBase = dao.getClientByName(client.getName());
            if (clientBase != null) {
                return false;
            } else {
                dao.addClient(client);
                return true;
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public boolean sendMoneyToClient(BankClient sender, String password, String nameTo, Long value) {
        try {
            if (dao.validateClient(sender.getName(), password) && (dao.isClientHasSum(sender.getName(), value))) {
                BankClient clientTo = dao.getClientByName(nameTo);
                dao.updateClientsMoney(sender.getName(), clientTo.getName(), value);
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }
/*Проверка наличия средств у отправителя  обязательна при совершении транзакции,
так же обязательна валидация логина и пароля в методе validateUser().
*/
    public void cleanUp() throws DBException {
        try {
            dao.dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void createTable() throws DBException{
        try {
            dao.createTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }
}
