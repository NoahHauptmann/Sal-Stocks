package nhauptma_CSCI201L_Assignment4;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class PortfolioServlet
 */
@WebServlet("/PortfolioServlet")
public class PortfolioServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static java.sql.Connection conn = null;
    public PortfolioServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			PrintWriter out = response.getWriter();
//			Cookie [] cookies = request.getCookies();
			Gson gs = new GsonBuilder().setPrettyPrinting().create();
//			response.setContentType("application/json");
//			String idString = null;
//			for (Cookie cookie : cookies) {
//			     if ("id".equals(cookie.getName())) {
//			          idString = cookie.getValue();
//			     }
//			}
			String idString = request.getParameter("userID");
			if(idString.equals(null)) {
				String responseString = gs.toJson(new PortfolioMessage(false, "nonvalidated user", null, 0));
				out.print(responseString);
				return;
			}
			int id = Integer.parseInt(idString);
			if(conn == null) {
				Class.forName("com.mysql.jdbc.Driver"); //https://javarevisited.blogspot.com/2016/09/javasqlsqlexception-no-suitable-driver-mysql-jdbc-localhost.html#axzz6s5NQNbKz
				conn = DriverManager.getConnection("");
			}
			PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT stockID FROM portfolio WHERE userID=?");
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			PreparedStatement ps2 = conn.prepareStatement("SELECT balance FROM users WHERE userID=?");
			ps2.setInt(1, id);
			ResultSet rs2 = ps2.executeQuery();
			int balance=0;
			if(rs2.next()) {
				balance = rs2.getInt("balance");
			}
			Vector<PortfolioMember> port = new Vector<PortfolioMember>();
			while(rs.next()) {
				String company = rs.getString("stockID");
				PortfolioMember add = new PortfolioMember(company, id);
				port.add(add);
			}
			String responseString = gs.toJson(new PortfolioMessage(true, "success!", port, balance)); //return stockID or TIINGO JSon

			out.print(responseString);
			out.flush();
		} catch(SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

class PortfolioMessage {
	public boolean valid;
	public String message;
	public int balance;
	public int value;
	public Vector<PortfolioMember> fv;
	public PortfolioMessage(boolean val, String mes, Vector<PortfolioMember> id, int bal) {
		valid = val;
		message = mes;
		fv= id;
		balance = bal;
		value = bal;
		for(int i=0; i<fv.size(); i++) {
			value += fv.get(i).totalCost;
		}
	}
}

class PortfolioMember {
	public String ticker;
	public String name;
	public int quantity=0;
	public int totalCost=0;
	public double averageCost;
	public double currValue;
	public double change;
	public double marketValue;
	public PortfolioMember(String c, int id) {
		try {
			ticker = c;
			System.out.println(c);
			PreparedStatement ps = PortfolioServlet.conn.prepareStatement("SELECT * FROM portfolio WHERE userID=? AND stockID=?");
			ps.setInt(1, id);
			ps.setString(2, c);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				quantity += rs.getInt("quantity");
				totalCost += (rs.getInt("quantity")*rs.getInt("purchasePrice"));
			}
			System.out.println(quantity);
			System.out.println(totalCost);
			averageCost = totalCost/quantity;
			URL url = new URL("https://api.tiingo.com/tiingo/daily/"+ticker+"?token=0c3234edaf9c26f625c6f5433dc421bc5b59d467");
			URL url3 = new URL("https://api.tiingo.com/iex?tickers="+ticker+"&token=0c3234edaf9c26f625c6f5433dc421bc5b59d467");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			HttpURLConnection conn3 = (HttpURLConnection) url3.openConnection();
			conn.setRequestMethod("GET");
			conn3.setRequestMethod("GET");
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			BufferedReader br3 = new BufferedReader(new InputStreamReader(conn3.getInputStream()));
			String line = br.readLine();
			String line2 = br3.readLine();
			line2 = line2.substring(1, line2.length()-1);
			JSONObject js = new JSONObject(line);
			JSONObject js2 = new JSONObject(line2);
			name = js.getString("name");
			currValue = js2.getDouble("last");
			change = currValue-averageCost;
			marketValue = quantity*currValue;
		} catch(SQLException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
