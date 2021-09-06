/*
 * DB로 SQL문을 전송하는 클래스
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static db.JdbcUtil.*; // static : 모든 메서드들 미리 메모리에 올림

import vo.BoardBean;

public class BoardDAO {
	Connection con = null; // 멤버변수(전역변수 : 전체메서드에서 사용가능)
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	/* 싱글톤 패턴 : BoardDAO객체를 단 1개만 생성
	 * 이유? 외부클래스에서 처음 생성된 BoardDAO객체를 공유해서 사용하도록 하기 위해
	 * 
	 */
	private BoardDAO() {}
	
	private static BoardDAO boardDAO;
	//static 이유? 객체를 생성하기 전에 이미 메모리에 올라간 getInstance()메서드를 통해서만 BoardDAO객체를 1개만 만들도록 하기위해
	public static BoardDAO getInstance() {
		if(boardDAO == null) { // 객체가 없으면
			boardDAO = new BoardDAO(); // 객체 생성
		}
		return boardDAO; // 객체가 있으면 기존 객체의 주소 리턴
	}
	
	public void setConnection (Connection con) { // Connection객체를 받아 DB연결
		this.con = con;
	}
	
	// 1. 글 등록
	public int insertArticle(BoardBean article) {
		//PreparedStatement pstmt = null;
		//ResultSet rs = null;
		int num = 0;
		String sql = "";
		int insertCount = 0;
		try {
			//pstmt = con.prepareStatement("select max(board_num) from board"); // 교재
			                              //오라클 : nvl(), nvl2()
			pstmt = con.prepareStatement("select ifnull(max(board_num),0)+1 from board"); // 수정
			rs = pstmt.executeQuery();
			
			//if (rs.next()) num = rs.getInt(1)+1; // 10 + 1 = 11. 교재
			//else num = 1; // null이면 1. 교재
			
			if (rs.next()) num = rs.getInt(1); // 수정
			
			sql += "insert into board values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())"; // now() = 오라클 sysdate ★주의 : sysdate는 ()없음
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setString(2, article.getBoard_name()); // 작성자
			pstmt.setString(3, article.getBoard_pass()); // 비밀번호
			pstmt.setString(4, article.getBoard_subject()); // 제목
			pstmt.setString(5, article.getBoard_content()); // 내용
			pstmt.setString(6, article.getBoard_file()); // 첨부 파일명
			pstmt.setInt(7, num); // 답변글 등록할 때 원글과 답변글을 같은그룹으로 묶기위해 사용함(그룹번호가 같으면 같은그룹임)
			pstmt.setInt(8, 0); // 얼마만큼 안쪽으로 들어가 글이 시작될것인지를 결정해주는 값(0으로 초기화. 0은 원글)
			pstmt.setInt(9, 0); // 원글에서 답변글이 몇번째 아래에 놓일것인지를 결정해주는 값(0으로 초기화. 0은 원글)
			pstmt.setInt(10, 0); // 조회수 (0으로 초기화)
			
			insertCount = pstmt.executeUpdate(); // 업데이트가 성공하면 1을 리턴
		} catch(Exception e) {
			//e.printStackTrace();
			System.out.println("boardInsert 에러 : " + e); // e:예외종류 + 예외메세지
		} finally {
			close(rs);
			close(pstmt);
		}
		return insertCount;
	}
	
	// 2. 게시판 전체 글의 개수를 구하여 반환
	public int selectListCount() {
		int listCount = 0;
		try {
			pstmt = con.prepareStatement("select count(*) from board");
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				listCount = rs.getInt(1); // 조회한 전체 글의 수
			}
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("getListCount 에러 : " + e); // e:예외종류 + 예외메세지
		} finally {
			close(rs);
			close(pstmt);
		}
		
		return listCount;
	}

	public ArrayList<BoardBean> selectArticleList(int page, int limit) {
		ArrayList<BoardBean> articleList = new ArrayList<BoardBean>();
		/* board_re_ref : 같은 수는 같은 그룹을 의미
		 * 				  (원글이 3이면 답변글도 모두 3)
		 * board_re_seq : 원글에서 답변글이 몇번째 아래에놓일것인지 위치를 결정해주는 값
		 */
		
																					//limit 10,10 : 11행부터 10개의행을 가져옴 MYSQL에만 있는 명령어. 오라클에는없음
		String sql = "select * from board order by board_re_ref desc, board_re_seq asc limit ?,10";
		/*
		 * startrow 변수에 해당 페이지에서 출력되어야 할 시작 레코드의 INDEX번호를 구함
		 * 예) 아래 페이지 번호 중 2를 클릭하면 page가 2가되어 (2-1)*10 = 10
		 */
		
		int startrow=(page-1)*10; // 예를 참조 - 10이지만 읽기시작하는 row번호는 11이 됨
		
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, startrow); // 11부터 10개의 레코드조회(답변글 포함해서)
			rs = pstmt.executeQuery();
			
			BoardBean boardBean = null;
			while(rs.next()) {
				boardBean = new BoardBean(); // 기본값으로 채워진것을 아래코드 - 조회한 결과값으로 변경함
				
				boardBean.setBoard_num(rs.getInt("board_num")); // 글번호
				boardBean.setBoard_name(rs.getString("board_name")); // 작성자 (★주의 : 글비밀번호는 제외)
				boardBean.setBoard_subject(rs.getString("board_subject")); // 글제목
				boardBean.setBoard_content(rs.getString("board_content")); // 글내용
				boardBean.setBoard_file(rs.getString("board_file")); // 첨부파일
				
				boardBean.setBoard_re_ref(rs.getInt("board_re_ref")); // 관련글번호
				boardBean.setBoard_re_lev(rs.getInt("board_re_lev")); // 답글레벨
				boardBean.setBoard_re_seq(rs.getInt("board_re_seq")); // 관련글중 출력순서
				
				boardBean.setBoard_readcount(rs.getInt("board_readcount")); // 조회수
				boardBean.setBoard_date(rs.getDate("board_date")); // 작성일
				
				articleList.add(boardBean);
			}
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("getBoardList 에러 : " + e); // e:예외종류 + 예외메세지
		} finally {
			close(rs);
			close(pstmt);
		}
		return articleList;
	}
	
}
