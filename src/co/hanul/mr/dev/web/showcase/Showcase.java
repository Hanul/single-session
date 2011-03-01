package co.hanul.mr.dev.web.showcase;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import co.hanul.mr.dev.web.SingleSession;

@WebServlet("/Showcase")
public class Showcase extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAny(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAny(request, response);
	}
	
	private void doAny(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SingleSession ss = SingleSession.getSession(request, response);
		
		Object obj = ss.getAttribute("test");
		System.out.println(obj);
		
		ss.setAttribute("test", "테스트 메시지 입니다.");
	}

}
