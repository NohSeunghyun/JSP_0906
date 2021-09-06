package controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.Action;
import action.BoardListAction;
import action.BoardWriteProAction;
import vo.ActionForward;

/**
 * Servlet implementation class BoardFrontController
 */
// 확장자가 bo이면 무조건 BoardFrontController로 이동하여 doProcess()메서드 실행함
@WebServlet("*.bo") // 마지막 url이 *.bo로 끝나는 요청을 맵핑
public class BoardFrontController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BoardFrontController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doProcess(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doProcess(request, response);
	}

	// 1. 이 서블릿으로 들어오는 post나 get방식의 모든 요청은 doProcess()를 호출하여 처리
	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); // 반드시 첫줄에 코드삽입. post방식으로 받으면 한글깨짐 처리를 해야함 get방식으로 받은것에도 코드 넣어도 되므로
		
		// 요청 객체로부터 프로젝트명+파일경로 까지 가져옴 예)/project(프로젝트명)/boardWriteForm.bo
		String requestURI = request.getRequestURI();
		// 요청 URL : http://localhost:8090/project(프로젝트명)/boardWriteForm.bo
		// 요청 URI : /project(프로젝트명)/boardWriteForm.bo
		
		// 요청 객체로부터 프로젝트 path만 가져옴 예)/project(프로젝트명)
		String contextPath = request.getContextPath();
		
		/* URI에서 ContextPath길이 만큼 잘라낸 나머지 문자열
		 * /project(프로젝트명)/boardWriteForm.bo - /project(프로젝트명) = /boardWriteForm.bo
		 * "/"도 하나의 문자열로 취급하여 /이 0인덱스. /project는 8개 문자열. 그다음 /boardWriteForm.bo의 /는 8인덱스
		 */ 
		String command = requestURI.substring(contextPath.length()); // index 8~끝까지 부분문자열 반환
		
		/* 요청이 파악되면 해당 요청을 처리하는 각 Action클래스를 사용하여 요청을 처리함
		 * 각 요청에 해당하는 Action클래스 객체들을 다형성을 이용해서 동일한 타입(Action)으로 참조하기 위해
		 * Action 인터페이스 타입의 변수 선언 (JSP책 574p참조)
		 */
		Action action = null;
		ActionForward forward = null;
		
		/* 글쓰기 페이지를 열어주는 요청인 경우 특별한 비지니스 로직을 실행할 필요없이
		 * 글쓰기 할 수 있는 뷰 페이지로만 포워딩하면 됨
		 */
		if (command.equals("/boardWriteForm.bo")) { // 사용자가 글 등록하는 폼화면 요청이면
			forward = new ActionForward();
			forward.setPath("/board/qna_board_write.jsp"); // 디스패치방식. ActionForward의 isRedirect 변수값이 false(기본값)이므로 디스패치방식
		} 
		else if (command.equals("/boardWritePro.bo")) { // 사용자가 입력한 자료들을 DB에 추가하는 요청이면
			action = new BoardWriteProAction();
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (command.equals("/boardList.bo")) { // 테이블 전체 목록 조회 요청이면
			action = new BoardListAction();
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/*
		 * 포워딩
		 */
		if (forward != null) {
			if (forward.isRedirect()) { // isRedirect=trye : 주소변경(새요청), request공유 못함
				response.sendRedirect(forward.getPath()); // 응답 - 리다이렉트방식
			} else { // isRedirect = false : 디스패치 방식
				//RequestDispatcher dispatcher = request.getRequestDispatcher(forward.getPath());
				//dispatcher.forward(request, response); // 기존의 요청,기존의 응답 그대로 보내므로 주소 그대로
				request.getRequestDispatcher(forward.getPath()).forward(request, response); // 위 두코드를 한줄로
			}
		}
		
	}
	
}
