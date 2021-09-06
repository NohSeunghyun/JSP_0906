/* MySQL에서는 /**/로 주석처리 --사용못함 */

drop table board;

create table board (
board_num int, /*1.글 번호*/
/*----------------------------------------------------------------------*/
board_name varchar(20) not null, /*2.글 작성자*/
board_pass varchar(15) not null, /*3.글 비밀번호*/
board_subject varchar(50) not null, /*4.글 제목*/
board_content varchar(2000) not null, /*5.글 내용*/
board_file varchar(50) not null, /*6.첨부 파일*/
/*----------------------------------------------------------------------*/
board_re_ref int not null, /*7.관련 글 번호*/
board_re_lev int not null, /*8.답글 레벨*/
board_re_seq int not null, /*9.관련글 중 출력순서*/
/*----------------------------------------------------------------------*/
board_readcount int default 0, /*10.조회수*/
board_date date, /*작성일*/
/*----------------------------------------------------------------------*/
primary key(board_num) /*기본키*/
);

/*
board_re_ref : 같은 수는 같은 그룹을 의미
(원글의 board_re_Ref 숫자가 3이면 답변글 모두 board_re_ref 숫자가 3이되어 원글과 답변글을 하나로 묶을 수 있는 값)

board_re_lev : 얼마만큼 안쪽으로 들어가 글이 시작될 것인지 결정해주는 값
답변레벨로 0이 아니면 답변글이다.
답변레벨로 0이 아니면 즉,답변글이면 답변레벨*2 하여 답변레벨이 하나 증가할 때 마다 
공백 (&nbsp;)을 두번씩 더 출력하여 들여쓰기 한 후 답변글이 출력되도록 로직처리함

board_re_seq : 원글에서 답변글이 몇번째 아래에놓일것인지 위치를 결정해주는 값
*/

select * from board;
select ifnull(max(board_num),0) from board; /*오라클:NVL(max(board_num),0)*/

-- 1. 글 등록
insert into board values(?,?,?,?,?,?,?,?,?,?,now());