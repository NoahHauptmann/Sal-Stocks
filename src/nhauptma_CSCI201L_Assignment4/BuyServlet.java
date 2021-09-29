package nhauptma_CSCI201L_Assignment4;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class BuyServlet
 */
@WebServlet("/BuyServlet")
public class BuyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Connection conn = null;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BuyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
//			Cookie [] cookies = request.getCookies();
//			String idString = null;
//			for (Cookie cookie : cookies) {
//			     if ("id".equals(cookie.getName())) {
//			          idString = cookie.getValue();
//			     }
//			}
			String idString = request.getParameter("userID");
			if(idString.equals(null)) {
				out.print("Failure, non-validated user");
				return;
			}
			int id = Integer.parseInt(idString);
			String company = request.getParameter("company");
			String quant = request.getParameter("quantity");
			int quantInt = Integer.valueOf(quant);
	
			URL url3 = new URL("https://api.tiingo.com/iex?tickers="+company+"&token=0c3234edaf9c26f625c6f5433dc421bc5b59d467");
			HttpURLConnection conn3 = (HttpURLConnection) url3.openConnection();
			conn3.setRequestMethod("GET");
			BufferedReader br3 = new BufferedReader(new InputStreamReader(conn3.getInputStream()));
			String line2 = br3.readLine();
			line2 = line2.substring(1, line2.length()-1);
			JSONObject js2 = new JSONObject(line2);
			double price = js2.getDouble("askPrice");
			
			
			
			if(conn == null) {
				Class.forName("com.mysql.jdbc.Driver"); //https://javarevisited.blogspot.com/2016/09/javasqlsqlexception-no-suitable-driver-mysql-jdbc-localhost.html#axzz6s5NQNbKz
				conn = DriverManager.getConnection("");
			}
			
			PreparedStatement ps = conn.prepareStatement("INSERT INTO portfolio (userID, stockID, quantity, purchasePrice, purchaseTime) VALUES (?,?,?,?, CURRENT_TIMESTAMP)");
			ps.setInt(1, id);
			ps.setString(2, company);
			ps.setInt(3, quantInt);
			ps.setDouble(4, price);
			double totalCost = quantInt*price;

			
			PreparedStatement ps2 = conn.prepareStatement("SELECT balance FROM users WHERE userID=?");
			ps2.setInt(1, id);
			ResultSet rs2 = ps2.executeQuery();
			int balance=0;
			if(rs2.next()) {
				balance = rs2.getInt("balance");
			}
			if(balance < totalCost) {
				out.print("FAILED: User does not have enough money in their account");
				out.flush();
				return;
			}
			double newBalance = balance-totalCost;
			PreparedStatement ps3 = conn.prepareStatement("UPDATE users SET balance=? WHERE userID=?");
			ps3.setDouble(1, newBalance);
			ps3.setInt(2, id);
			ps3.execute();
			ps.execute();
			out.print("SUCCESS: Executed purchase of "+quantInt+" shares of "+company+" for $"+totalCost);
			
			
		} catch(SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			out.print("FAILED: Cannot purchase stock after the market has closed");
		}
	}

}
