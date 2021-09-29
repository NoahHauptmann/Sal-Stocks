package nhauptma_CSCI201L_Assignment4;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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

import com.google.gson.*;

/**
 * Servlet implementation class GetFavorites
 */
@WebServlet("/GetFavorites")
public class GetFavorites extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static java.sql.Connection conn = null;
	
    public GetFavorites() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			PrintWriter out = response.getWriter();
//			Cookie [] cookies = request.getCookies();
			Gson gs = new GsonBuilder().setPrettyPrinting().create();
			response.setContentType("application/json");
//			String idString = null;
//			for (Cookie cookie : cookies) {
//			     if ("id".equals(cookie.getName())) {
//			          idString = cookie.getValue();
//			     }
			String idString = request.getParameter("userID");
			if(idString.equals(null)) {
				String responseString = gs.toJson(new FavoritesMessage(false, "nonvalidated user", null));
				out.print(responseString);
			}
			int id = Integer.parseInt(idString);
			if(conn == null) {
				Class.forName("com.mysql.jdbc.Driver"); //https://javarevisited.blogspot.com/2016/09/javasqlsqlexception-no-suitable-driver-mysql-jdbc-localhost.html#axzz6s5NQNbKz
				conn = DriverManager.getConnection("");
			}
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM favorites WHERE userID=?");
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			Vector<String> favs = new Vector<String>();
			while(rs.next()) {
				favs.add(rs.getString("StockID"));
			}
			String responseString = gs.toJson(new FavoritesMessage(true, "success!", favs)); //return stockID or TIINGO JSon
			out.print(responseString);
		} catch(SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class FavoritesMessage {
		public boolean valid;
		public String message;
		public Vector<Favorite> fv;
		public FavoritesMessage(boolean val, String mes, Vector<String> id) {
			fv = new Vector<Favorite>();
			valid = val;
			message = mes;
			for(int i=0; i<id.size(); i++) {
				Favorite temp = new Favorite(id.get(i));
				fv.add(temp);
			}
		}
	}
}

class Favorite{
	public String ticker;
	public String name;
	public double lastprice;
	public double prevclose;
	public Favorite(String ticker) {
		this.ticker=ticker;
		try {
			System.out.println(ticker);
			URL url = new URL("https://api.tiingo.com/tiingo/daily/"+ticker+"?token=0c3234edaf9c26f625c6f5433dc421bc5b59d467");
			URL url3 = new URL("https://api.tiingo.com/iex?tickers="+ticker+"&token=0c3234edaf9c26f625c6f5433dc421bc5b59d467");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			HttpURLConnection conn3 = (HttpURLConnection) url3.openConnection();
			conn.setRequestMethod("GET");
			conn3.setRequestMethod("GET");
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			BufferedReader br3 = new BufferedReader(new InputStreamReader(conn3.getInputStream()));
			String line = br.readLine();
			JSONObject js = new JSONObject(line);
			System.out.println(line);
			System.out.println();
			String line2 = br3.readLine();
			line2 = line2.substring(1, line2.length()-1);
			System.out.println(line2);
			System.out.println();
			JSONObject js2 = new JSONObject(line2);
			name = js.getString("name");
			lastprice = js2.getDouble("last");
			System.out.println(lastprice);
			prevclose = js2.getDouble("prevClose");
			System.out.println(prevclose);
		} catch(FileNotFoundException e) {
			System.out.print(e.getMessage());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
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
