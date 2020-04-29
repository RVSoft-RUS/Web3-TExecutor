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

public class MoneyTransactionServlet extends HttpServlet {
    private BankClientService bankClientService = BankClientService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(PageGenerator.getInstance().getPage("moneyTransactionPage.html", new HashMap<>()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String senderName = req.getParameter("senderName");
            String senderPass = req.getParameter("senderPass");
            String countStr = req.getParameter("count");
            long count = Long.parseLong(countStr);
            String nameTo = req.getParameter("nameTo");

            BankClient sender = bankClientService.getClientByName(senderName);

            if (bankClientService.sendMoneyToClient(sender, senderPass, nameTo, count)) {
                resp.getWriter().println("The transaction was successful");
            } else {
                resp.getWriter().println("transaction rejected");
            }
        } catch (Exception e) {
            resp.getWriter().println("transaction rejected");
        }
    }
}
