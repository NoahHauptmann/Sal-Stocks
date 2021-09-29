package nhauptma_CSCI201L_Assignment4;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TiingoServlet
 */
@WebServlet("/TiingoServlet")
public class TiingoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public TiingoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			PrintWriter out = response.getWriter();
			System.out.println("Servlet!");
			response.setContentType("application/json");
			String company = request.getParameter("company");
			System.out.println(company);
			URL url = new URL("https://api.tiingo.com/tiingo/daily/"+company+"?token=0c3234edaf9c26f625c6f5433dc421bc5b59d467");
			URL url2 = new URL("https://api.tiingo.com/tiingo/daily/"+company+"/prices?token=0c3234edaf9c26f625c6f5433dc421bc5b59d467");
			URL url3 = new URL("https://api.tiingo.com/iex?tickers="+company+"&token=0c3234edaf9c26f625c6f5433dc421bc5b59d467");
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
			HttpURLConnection conn3 = (HttpURLConnection) url3.openConnection();
			conn.setRequestMethod("GET");
			conn2.setRequestMethod("GET");
			conn3.setRequestMethod("GET");
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			BufferedReader br2 = new BufferedReader(new InputStreamReader(conn2.getInputStream()));
			BufferedReader br3 = new BufferedReader(new InputStreamReader(conn3.getInputStream()));
			String line = br.readLine();
			String line1 = line.replace('}', ',');
			System.out.print(line1);
			String line2 = br2.readLine();
			String v = line2.replace('[', ' ');
			String v4 = v.replace(']',' ');
			String v1 = v4.replace('{', ' ');
			String v2 = v1.replace('}', ',');
			System.out.print(v2);
			String line3 = br3.readLine(); 
			String q = line3.replaceAll("open", "open2");
			String qq = q.replaceAll("high", "high2");
			String qqq = qq.replaceAll("low", "low2");
			String q4 = qqq.replaceAll("ticker", "ticker2");
			String q5 = q4.replaceAll("volume", "volume2");
			String q1 = q5.replace('[', ' ');
			String q2 = q1.replace(']', ' ');
			String v3 = q2.replace('{', ' ');
			System.out.println(line3);
			out.print(line1+v2+v3);
			out.close();
		} catch(FileNotFoundException e) {
			System.out.println("Invalid stock");
		}
	}

}
