package nhauptma_CSCI201L_Assignment4;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class LogoutServlet
 */
@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
    public LogoutServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Logout!");
		PrintWriter out = response.getWriter();
		Gson gs = new GsonBuilder().setPrettyPrinting().create();
		Cookie cookie2 = new Cookie("G_AUTHUSER_H", "");
		cookie2.setMaxAge(0);
		response.addCookie(cookie2);
		Cookie cookie = new Cookie("id", "");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	    String responseString = gs.toJson(new LogoutMessage(true, "logout success!"));
		out.print(responseString);
		out.flush();
	}
}

class LogoutMessage {
	public boolean valid;
	public String message;
	public LogoutMessage(boolean val, String mes) {
		valid = val;
		message = mes;
	}
}
	
