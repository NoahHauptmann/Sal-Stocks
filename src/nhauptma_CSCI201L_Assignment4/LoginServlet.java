package nhauptma_CSCI201L_Assignment4;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    static java.sql.Connection conn = null;
    
    public LoginServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Login Servlet!");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		Gson gs = new GsonBuilder().setPrettyPrinting().create();
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		try {
			if(conn == null) {
				Class.forName("com.mysql.jdbc.Driver"); //https://javarevisited.blogspot.com/2016/09/javasqlsqlexception-no-suitable-driver-mysql-jdbc-localhost.html#axzz6s5NQNbKz
				conn = DriverManager.getConnection("");
			}
			PreparedStatement ps = conn.prepareStatement("SELECT userID FROM users WHERE username=? AND password=?");
			ps.setString(1, username);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				int userID = rs.getInt("userID");
				String responseString = gs.toJson(new LoginMessage(true, "success!", userID));
				out.print(responseString);
				String idCookie = String.valueOf(userID); //http://tutorials.jenkov.com/java-servlets/cookies.html#:~:text=(or%20JSPs).-,Java%20Cookie%20Example,a%20value%2C%20%22%20myCookieValue%20%22.
				Cookie authCookie = new Cookie("id", idCookie);
				authCookie.setPath(request.getContextPath());
				response.addCookie(authCookie);
			}
			else {
				String responseString = gs.toJson(new LoginMessage(false, "incorrect username or password", -1));
				out.print(responseString);
			}
			out.flush();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

class LoginMessage {
	public boolean valid;
	public String message;
	public int id;
	public LoginMessage(boolean val, String mes, int id) {
		valid = val;
		message = mes;
		this.id = id;
	}
}
