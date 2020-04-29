package servlet;

import model.BankClient;
import service.BankClientService;
import util.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public class RegistrationServlet extends HttpServlet {
    private BankClientService bankClientService = BankClientService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(PageGenerator.getInstance().getPage("registrationPage.html", new HashMap<>()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String name = req.getParameter("name");
            String password = req.getParameter("password");
            String moneyStr = req.getParameter("money");
            long money = Long.parseLong(moneyStr);
            BankClient client = new BankClient(name, password, money);
            if (bankClientService.addClient(client)) {
                resp.getWriter().println("Add client successful");
            } else {
                resp.getWriter().println("Client not add");
            }
        } catch (Exception e) {
            resp.getWriter().println("Client not add");
        }
    }
}