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
 * Servlet implementation class SellServlet
 */
@WebServlet("/SellServlet")
public class SellServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection conn = null;   
    
    public SellServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	@SuppressWarnings("unused")
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
	
			int quantInt = Integer.parseInt(quant);
			int sell = quantInt;
			URL url3 = new URL("https://api.tiingo.com/iex?tickers="+company+"&token=0c3234edaf9c26f625c6f5433dc421bc5b59d467");
			HttpURLConnection conn3 = (HttpURLConnection) url3.openConnection();
			conn3.setRequestMethod("GET");
			BufferedReader br3 = new BufferedReader(new InputStreamReader(conn3.getInputStream()));
			String line2 = br3.readLine();
			line2 = line2.substring(1, line2.length()-1);
			JSONObject js2 = new JSONObject(line2);
			double price = js2.getDouble("bidPrice");
			double sellValue = quantInt*price;
			if(conn == null) {
				Class.forName("com.mysql.jdbc.Driver"); //https://javarevisited.blogspot.com/2016/09/javasqlsqlexception-no-suitable-driver-mysql-jdbc-localhost.html#axzz6s5NQNbKz
				conn = DriverManager.getConnection("");
			}
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE userID=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM portfolio WHERE userID=? AND stockID=? ORDER BY purchaseTime", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ps2.setInt(1, id);
			ps2.setString(2, company);
			ResultSet rs2 = ps2.executeQuery();
			int totalQuant = 0;
			while(rs2.next()) {
				totalQuant += rs2.getInt("quantity");
			}
			if(totalQuant < quantInt) {
				out.print("FAILED: Do not have enough stocks to sell.");
				return;
			}
			rs2 = ps2.executeQuery();
			while(rs2.next() && quantInt > 0) {
				int currQuant = rs2.getInt("quantity");
				int newQuant = currQuant - quantInt;
				if(newQuant <= 0) {
					rs2.deleteRow();
				}
				else {
					rs2.updateInt("quantity", newQuant);
					rs2.updateRow();
				}
				quantInt -= currQuant;
			}
			if(rs.next()) {
				double currBalance = rs.getDouble("balance");
				currBalance += sellValue;
				rs.updateDouble("balance", currBalance);
				rs.updateRow();
			}
			out.print("SUCCESS: Executed sale of "+sell+" shares of "+company+" for $"+sellValue);
			
			
		} catch(SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			out.print("FAILED: Cannot sell stock after the market has closed");
		}
	}

}
