package action;

import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 파일 업로드하기 위해 MultipartRequest를 사용하려면 반드시 cos.jar를 lib폴더에 추가해야한다.
import com.oreilly.servlet.MultipartRequest;
// 중복 파일명이 있다면 중복된것 처리하기위해 DefaultFileRenamePolicy를 추가해야함
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import svc.BoardWriteProService;
import vo.ActionForward;
import vo.BoardBean;

public class BoardWriteProAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		ActionForward forward = null;
		
		int fileSize = 5 * 1024 * 1024; // 한번에 업로드 할 수 있는 파일크기 5M(파일이 여러개 업로드되면 합쳐서 5M. 넘으면 예외발생)
		
		// 파일이 업로드 될 서버상의 실제 디렉토리 경로
		ServletContext context = request.getServletContext();
		String realFolder = context.getRealPath("/boardUpload");
		
		MultipartRequest multi = new MultipartRequest(request, realFolder, fileSize, "UTF-8", 
				new DefaultFileRenamePolicy()); // 파일이름 중복 처리를 위한 객체 예)a.txt가 있다면 자동으로 인덱스번호가 붙어서 a1.txt로 변경하여 업로드됨
		
		// 새로 등록할 글 정보를 저장할 BoardBean클래스
		BoardBean boardBean = new BoardBean(); // 기본값으로 채워진 BoardBean객체
		
		// 기본값으로 채워진 BoardBean객체를 사용자가 입력한 정보로 채움
		// ★주의. request.getParameter로 못가져옴. 그래서 변수에 담아 가져와야함
		boardBean.setBoard_name(multi.getParameter("board_name"));
		boardBean.setBoard_pass(multi.getParameter("board_pass"));
		boardBean.setBoard_subject(multi.getParameter("board_subject"));
		boardBean.setBoard_content(multi.getParameter("board_content"));
		boardBean.setBoard_file(multi.getOriginalFileName((String)multi.getFileNames().nextElement())); // 어렵다면 Chapter10 참조
		
		// 새로운 글(boardBean)을 등록하는 BoardWriteProService 객체 생성 후
		BoardWriteProService boardWriteProService = new BoardWriteProService();
		
		// 객체안의 registArticle()메서드로 DB연결, BoardBean객체 DB의 board테이블에 추가
		boolean isWriteSuccess = boardWriteProService.registArticle(boardBean);
		
		// 추가 후 성공하면 true, 실패하면 false를 리턴
		if (!isWriteSuccess) { // isWriteSuccess == false 글등록이 실패하면
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('등록실패');");
			out.println("history.back();");
			out.println("</script>");
		} else { // 글등록이 성공하면
			forward = new ActionForward();
			forward.setRedirect(true);//기본값 false : 디스패치방식. true : 리다이렉트(=새요청)
			forward.setPath("boardList.bo"); // 글전체 목록보기 요청하면 다시 프론트컨트롤로 이동
		}	
		return forward;
	}
	
}
