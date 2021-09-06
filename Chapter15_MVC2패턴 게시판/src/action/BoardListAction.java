/*
 * 글 전체 목록보기 요청을 처리하는 클래스
 */
package action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.BoardListService;
import vo.ActionForward;
import vo.BoardBean;

public class BoardListAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		BoardListService boardListService = new BoardListService();
		int listCount = boardListService.getListCount(); // 게시판의 총 글의 개수를 얻어오고
		
		int page = 1; // 출력될 페이지의 기본값으로 1페이지를 설정
		int limit = 10; // 한페이지당 출력될 글의 개수를 10개로 설정
		
		//출력될 페이지가 파라미터로 전송 되었으면
		if (request.getParameter("page") != null) { // "2" 
			Integer.parseInt(request.getParameter("page")); // 문자열 page를 연산할 수 있도록 int형으로 다운캐스팅
		}
		
		// listCount를 이용하여 총 페이지 수 계산 예)11.0/10 => 1.1 + 0.95 => 2.05 => 2페이지, 21.0/10 => 2.1 + 0.95 => 3.05 => 3페이지
		int maxPage = (int)((double)listCount/limit + 0.95); // JSP책 649p 그림참조
		
		/*
		 * 현재 페이지에 보여줄 시작 페이지 수(1, 11, 21 등..)
		 * [이전] 1 2 3 4 5 6 7 8 9 10 [다음]
		 * [이전] 11 12 13 14 15 16 17 18 19 20 [다음]
		 */
		//page=2일 때 : 2.0/10 => 0.2 + 0.9 => 1.1 => 1 - 1 => 0 * 10 => 0 + 1 = 1 [이전] 1 2 ... [다음]
		//page=15일 때 : 15.0/10 => 1.5 + 0.9 => 2.4 => 2 - 1 => 1 * 10 => 10 + 1 = 11 [이전] 11 12 ... [다음]
		int startPage = (((int)((double)page/10 + 0.9)) - 1) * 10 + 1;
		
		//현재페이지에 보여줄 마지막 페이지수(10, 20, 30 등...)
		int endPage = startPage + 10 - 1; // 예) startPage=1일때 : 1 + 10 - 1 = 10
											// 예) startPage=11일때 : 11 + 10 - 1 = 20
		/*
		 * 만약 전체 페이지가 15라면 [이전] 11 12 13 14 15로 출력해야 하기때문에
		 * endPage(20) > 총페이지수 15보다 크면 endPage=15로 설정
		 */
		if (endPage > maxPage) endPage=maxPage;
		
		/* 위에서 구한 페이징에 관한 정보를 저장한 PageInfo객체(=DTO)를 생성 후 
		 * 디스패치 방식으로 포워딩함
		 */
		
		//클릭한 페이지 번호와 한 페이지당 출력될 글의 개수 10을 매개값으로 전송하여 해당 페이지의 출력될 글 목록을 얻어옴
		ArrayList<BoardBean> articleList = boardListService.getArticleList(page,limit);
		return null;
	}

}
