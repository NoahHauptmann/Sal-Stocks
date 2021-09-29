package nhauptma_CSCI201L_Assignment4;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UnfavoriteServlet
 */
@WebServlet("/UnfavoriteServlet")
public class UnfavoriteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static java.sql.Connection conn = null;
	
    public UnfavoriteServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			PrintWriter out = response.getWriter();
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
			}
			int id = Integer.parseInt(idString);
			String company = request.getParameter("company");
			if(conn == null) {
				Class.forName("com.mysql.jdbc.Driver"); //https://javarevisited.blogspot.com/2016/09/javasqlsqlexception-no-suitable-driver-mysql-jdbc-localhost.html#axzz6s5NQNbKz
				conn = DriverManager.getConnection("");
			}
			PreparedStatement ps = conn.prepareStatement("DELETE FROM favorites WHERE userID=? AND stockID=?");
			PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM favorites WHERE userID=? AND stockID=?");
			ps.setInt(1, id);
			ps.setString(2, company);
			ps2.setInt(1, id);
			ps2.setString(2, company);
			ResultSet rs = ps2.executeQuery();
			if(rs.next()) {
				ps.execute();
				out.print("Success! "+company+" removed from favorites.");
			}
			else {
				out.print("Failure, stock already favorited");
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
