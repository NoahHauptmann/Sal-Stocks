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
 * Servlet implementation class SignupServlet
 */
@WebServlet("/SignupServlet")
public class SignupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static java.sql.Connection conn = null;
    
    public SignupServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String password2 = request.getParameter("password2");
		Gson gs = new GsonBuilder().setPrettyPrinting().create();
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");

		if(password.equals("") || email.equals("") || username.equals("") || password2.equals("")) {
			String responseString = gs.toJson(new SignupMessage(false, "Invalid. Please fill out all signup fields", -1, false));
			out.print(responseString);
			return;
		}
		if(!password.equals(password2)) {
			String responseString = gs.toJson(new SignupMessage(false, "Invalid. Passwords do not match", -1, false));
			out.print(responseString);
			return;
		}
		
		try {
			if(conn == null) {
				Class.forName("com.mysql.jdbc.Driver"); //https://javarevisited.blogspot.com/2016/09/javasqlsqlexception-no-suitable-driver-mysql-jdbc-localhost.html#axzz6s5NQNbKz
				conn = DriverManager.getConnection("");
			}
			PreparedStatement ps = conn.prepareStatement("SELECT userID FROM users WHERE email=?");
			PreparedStatement ps2 = conn.prepareStatement("INSERT INTO users (username, email, password, balance) VALUES (?,?,?,50000)");
			
			ps.setString(1, email);
			ps2.setString(1, username);
			ps2.setString(2, email);
			ps2.setString(3, password);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				String responseString = gs.toJson(new SignupMessage(false, "an account already exists with that email", -1, true));
				out.print(responseString);
			}
			else {
				ps2.execute();
				rs = ps.executeQuery();
				int id=0;
				if(rs.next()) {
					id=rs.getInt("userID");
				}
				int userID = rs.getInt("userID");
				String responseString = gs.toJson(new SignupMessage(true, "success!", id, false));
				out.print(responseString);
				String idCookie = String.valueOf(userID); //http://tutorials.jenkov.com/java-servlets/cookies.html#:~:text=(or%20JSPs).-,Java%20Cookie%20Example,a%20value%2C%20%22%20myCookieValue%20%22.
				Cookie authCookie = new Cookie("id", idCookie);
				authCookie.setPath(request.getContextPath());
				response.addCookie(authCookie);	
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class SignupMessage {
	public boolean valid;
	public boolean gExists;
	public String message;
	public int id;
	public SignupMessage(boolean val, String mes, int id, boolean g) {
		valid = val;
		message = mes;
		this.id = id;
		gExists = g;
	}
}
