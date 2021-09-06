/*
 * 글 등록 요청을 처리하는 비지니스 로직을 구현하는 Service클래스
 */
package svc;

import vo.BoardBean;

import static db.JdbcUtil.*;

//import db.JdbcUtil;

import java.sql.Connection;

import dao.BoardDAO;

public class BoardWriteProService {
	//기본생성자
	
	//추가 메서드
	public boolean registArticle(BoardBean boardBean) {
		//DB연결작업 : 커넥션풀에서 DB연결하기 위한 Connection객체를 얻어와
		//JdbcUtil.getConnection(); // 클래스명.static메서드 호출 // 미리 임포트하면 클래스명.메서드가아닌 메서드만호출해도됨 
		Connection con = getConnection();
		
		//BoardDAO : 싱글톤 패턴 - 단 1개의 객체만 생성하여 외부클래스에서 공유하여 사용하도록
		BoardDAO boardDAO = BoardDAO.getInstance();
		
		//BoardDAO객체에서 DB작업을할 때 사용하도록 매개값으로 설정함
		boardDAO.setConnection(con);
		
		//DB의 board테이블에 사용자가 입력한 값들(BoardBean객체)로 글추가 ->성공하면 1리턴
		int insertCount = boardDAO.insertArticle(boardBean);
		
		boolean isWriteSuccess = false; // 글 등록 성공여부
		if (insertCount > 0) { // 글 등록 성공
			commit(con); // 트랜젝션 완료
			isWriteSuccess = true; //글등록성공
		} else {
			rollback(con);
		}
		
		close(con);
		return isWriteSuccess;
	}
}
