<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>MVC2 게시판</title>
<style>
	#writeForm {
		margin: auto;
		width: 500px;
		height: 610px;
		border: 1px solid red;
	}
	h2 {
		text-align: center;
	}
	table {
		margin: auto;
		width: 450px;
	}
	/*테이블의 왼쪽셀*/
	.td_left {
		width: 150px;
		background: orange;
	}
	
	.td_right {
		width: 300px;
		background: skyblue;
	}
</style>
</head>
<body>
<!-- JSP책 629p~ 참조 -->
	<section id="writeForm">
		<h2>게시판 글 등록</h2>
		<!-- 등록버튼을 클릭하면 boardWritePro.bo 요청하여 프론트컨트롤로 이동 -->
		<form action="boardWritePro.bo" name="boadrform" method="post" enctype="multipart/form-data"> <!-- 파일업로드를 위해 enctype꼭 넣어야함 -->
			<table>
				<tr>
					<th class="td_left">
					<label for="board_name">글쓴이</label>
					</th>
					<td class="td_right">
					<input type="text" name="board_name" id="board_name" required="required">
					</td> <!-- required : 반드시 값을 넣어야한다는 속성 -->
				</tr>
				<tr>
					<th class="td_left">
					<label for="board_pass">비밀번호</label>
					</th>
					<td class="td_right">
					<input type="password" name="board_pass" id="board_pass" size="22" required="required">
					</td>
				</tr>
				<tr>
					<th class="td_left">
					<label for="board_subject">제목</label>
					</th>
					<td class="td_right">
					<input type="text" name="board_subject" id="board_subject" required="required">
					</td>
				</tr>
				<tr>
					<th class="td_left">
					<label for="board_content">내용</label>
					</th>
					<td class="td_right">
					<textarea type="text" name="board_content" id="board_content" cols="40" rows="15" required="required"></textarea>
					</td>
				</tr>
				<tr>
					<th class="td_left">
					<label for="board_file">파일첨부</label>
					</th>
					<td class="td_right">
					<input type="file" name="board_file" id="board_file" required="required">
					</td>
				</tr>
			</table>
			<center style="padding: 10px;">
				<input type="submit" value="답변글등록">&nbsp;&nbsp;
				<input type="reset" value="다시작성">
			</center>
		</form>
	</section>
</body>
</html>