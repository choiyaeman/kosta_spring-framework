# day02

![day02%20c09936827aee4f788c1ff4677352cca1/Untitled.png](day02%20c09936827aee4f788c1ff4677352cca1/Untitled.png)

ClientSide입장으로 html에서의 경로를 주는 곳은 html 태그중에서 img태그, a태그들이 경로를 설정하는 태그가 된다.  javascript에서의 경로를 주는 곳은 ajax의 url설정이다.

경로 설정하는 방법으로는 절대경로와 상대경로가 있다. 상대경로는 지금 사용 중인 브라우저의 주소 기준이다. 

![day02%20c09936827aee4f788c1ff4677352cca1/Untitled%201.png](day02%20c09936827aee4f788c1ff4677352cca1/Untitled%201.png)

ServerSide에서 경로 설정 방법은 절대경로와 상대경로가 있는데 request.getRqequestDispatcher 쓰면 상대경로 설정은 가능하나 절대경로 설정은 불가능하다. RqequestDispatcher의 forward라는 메서드가 하는 일은 용어는 요청을 다른 페이지에게 전달한다라는 의미이다. 서버차원에서의 이동은 한가지 주의해야 할 점이 현재 사용 중인 웹컨텍스트 이동은 가능하나 다른 웹컨텍스트의 이동은 할 수 가 없다. 현재 이동 되는 영역은 예를 들어 myback 웹컨텍스의 서블릿에서 myback안에있는 자원으로만 이동 할 수 있다. 다른 프로젝트의 jsp페이지로 이동한다? 못한다.. 같은 웹 컨텍스트로만 자원 이동한다. 그러므로 절대경로 사용 불가 하다!

![day02%20c09936827aee4f788c1ff4677352cca1/Untitled%202.png](day02%20c09936827aee4f788c1ff4677352cca1/Untitled%202.png)

response.sendRedirect는 절대경로 사용가능하다. 왜냐하면 response.sendRedirect메서드는 클라이언트 차원에서의 재요청이므로 가능하다. 경우에 따라서 절대경로를 사용할 수 있고 없을 수 있으니까 주의하자~!

서버사이드에서 상대경로 사용은 지금 사용중인 웹컨텍스트 기준이다.

사용권장 방법은 서블릿 쪽에서 jsp페이지 쪽으로 forward한다면 앞에 /부터 쓰는 게 가장 좋은 방법이다. 서블릿에서의 경로 설정은 /부터 시작하자 

![day02%20c09936827aee4f788c1ff4677352cca1/Untitled%203.png](day02%20c09936827aee4f788c1ff4677352cca1/Untitled%203.png)

이 두 가지만 잘 기억해두자~! 웹컨텍스부터, 웹컨텍스트 내부로 시작하는 점 기억해두기

---

![day02%20c09936827aee4f788c1ff4677352cca1/Untitled%204.png](day02%20c09936827aee4f788c1ff4677352cca1/Untitled%204.png)

글 번호에 해당하는 자동 값이 될 수 있도록 시퀀스 설정. 자료형은 시퀀스 값이 들어가려면 글 번호, 조회수의 자료형은 number가 되어야 한다. 제목, 작성자 명, 비밀번호은 문자 형태로 varchar2, 작성 일자는 작성 시간 값이 들어가야 하므로 Date가 된다.

- sqlplus

```jsx
Windows PowerShell
Copyright (C) Microsoft Corporation. All rights reserved.

새로운 크로스 플랫폼 PowerShell 사용 https://aka.ms/pscore6

PS C:\Users\User> sqlplus scott/tiger

SQL*Plus: Release 11.2.0.2.0 Production on 화 3월 9 15:45:50 2021

Copyright (c) 1982, 2014, Oracle.  All rights reserved.

Connected to:
Oracle Database 11g Express Edition Release 11.2.0.2.0 - 64bit Production

SQL> CREATE TABLE repboard(
  2  board_no number,
  3  parent_no number,
  4  board_title varchar2(30),
  5  board_writer varchar2(15),
  6  board_dt date,
  7  board_pwd varchar2(4),
  8  board_cnt number(5),
  9  constraint repboard_no_pk PRIMARY KEY(board_no)
 10  );

Table created.

SQL> INSERT INTO repboard(
  2   board_no, parent_no, board_title, board_writer, board_dt, board_pwd, board_cnt)
  3  VALUES
  4     (1,           0,            '글1',         NULL,          '21/03/01',   '1',   0);

1 row created.

SQL> INSERT INTO repboard(
  2   board_no, parent_no, board_title, board_writer, board_dt, board_pwd, board_cnt)
  3  VALUES
  4     (2,           0,            '글2',         '최군',          '21/03/02',   '2',   0);

1 row created.

SQL> INSERT INTO repboard(
  2   board_no, parent_no, board_title, board_writer, board_dt, board_pwd, board_cnt)
  3  VALUES
  4     (3,           2,         '글3-답글2',  '최군',          '21/03/03',   '3',   0);

1 row created.

SQL> INSERT INTO repboard(
  2   board_no, parent_no, board_title, board_writer, board_dt, board_pwd, board_cnt)
  3  VALUES
  4     (4,           2,         '글4-답글2',  '김군',          '21/03/04',   '4',   0);

1 row created.

SQL> INSERT INTO repboard(
  2   board_no, parent_no, board_title, board_writer, board_dt, board_pwd, board_cnt)
  3  VALUES
  4     (5,           3,         '글5-답글3',  '신군',          '21/03/05',   '5',   0);

1 row created.

SQL> COMMIT;

SQL> select * from repboard;

  BOARD_NO  PARENT_NO
---------- ----------
BOARD_TITLE
------------------------------------------------------------
BOARD_WRITER                   BOARD_DT BOARD_PW  BOARD_CNT
------------------------------ -------- -------- ----------
         1          0
글1
                               21/03/01 1                 0

         2          0
글2
최군                           21/03/02 2                 0

  BOARD_NO  PARENT_NO
---------- ----------
BOARD_TITLE
------------------------------------------------------------
BOARD_WRITER                   BOARD_DT BOARD_PW  BOARD_CNT
------------------------------ -------- -------- ----------

         3          2
글3-답글2
최군                           21/03/03 3                 0

         4          2
글4-답글2

  BOARD_NO  PARENT_NO
---------- ----------
BOARD_TITLE
------------------------------------------------------------
BOARD_WRITER                   BOARD_DT BOARD_PW  BOARD_CNT
------------------------------ -------- -------- ----------
김군                           21/03/04 4                 0

         5          3
글5-답글3
신군                           21/03/05 5                 0

SQL> SELECT board_no
  2  FROM repboard
  3  START WITH parent_no=0
  4  CONNECT BY PRIOR board_no = parent_no; // parent_no는 현재행의 부모글번호와 같은값을 갖는 board_no(글 번호) 행을 찾는다

  BOARD_NO
----------
         1
         2
         3
         5
         4

SQL> SELECT board_no
  2  FROM repboard
  3  START WITH parent_no = 0
  4  CONNECT BY PRIOR board_no = parent_no
  5  ORDER SIBLINGS BY board_no DESC;

  BOARD_NO
----------
         2
         4
         3
         5
         1

SQL> SELECT level, repboard.*
  2  FROM repboard
  3  START WITH parent_no = 0
  4  CONNECT BY PRIOR board_no = parent_no
  5  ORDER SIBLINGS BY board_no DESC;

     LEVEL   BOARD_NO  PARENT_NO
---------- ---------- ----------
BOARD_TITLE
------------------------------------------------------------
BOARD_WRITER                   BOARD_DT BOARD_PW  BOARD_CNT
------------------------------ -------- -------- ----------
         1          2          0
글2
최군                           21/03/02 2                 0

         2          4          2
글4-답글2
김군                           21/03/04 4                 0

     LEVEL   BOARD_NO  PARENT_NO
---------- ---------- ----------
BOARD_TITLE
------------------------------------------------------------
BOARD_WRITER                   BOARD_DT BOARD_PW  BOARD_CNT
------------------------------ -------- -------- ----------

         2          3          2
글3-답글2
최군                           21/03/03 3                 0

         3          5          3
글5-답글3

     LEVEL   BOARD_NO  PARENT_NO
---------- ---------- ----------
BOARD_TITLE
------------------------------------------------------------
BOARD_WRITER                   BOARD_DT BOARD_PW  BOARD_CNT
------------------------------ -------- -------- ----------
신군                           21/03/05 5                 0

         1          1          0
글1
                               21/03/01 1                 0
```

![day02%20c09936827aee4f788c1ff4677352cca1/Untitled%205.png](day02%20c09936827aee4f788c1ff4677352cca1/Untitled%205.png)

글 번호가 최신 글이 보이도록 해야 한다. 화면에 보여주는 글 번호의 순서는 2번 글이 먼저 2번 글에 대한 답변인 3,4번 글 중에서도 최신 글인 4번 글이 나오고 그 다음 최신 글인 3번이 나오도록 2번 글이 최신 글 1번 글이 예전 글로 설계를 해보자~ 오라클에서는 계층형 쿼리라는게 있다 이것을 이용하게 되면 테이블 하나에 있는 원글과 답글들을 계층형으로 만들어 낼 수 있다. 

![day02%20c09936827aee4f788c1ff4677352cca1/Untitled%206.png](day02%20c09936827aee4f788c1ff4677352cca1/Untitled%206.png)

```jsx
SELECT [컬럼]...
FROM [테이블]
WHERE [조건]
START WITH [최상위 조건]
CONNECT BY [NOCYCLE][PRIOR 계층형 구조 조건];
```

최상위 조건 → 가장 먼저 나와야 될 자료에 대한 조건을 주면 된다. 즉 원글이라는 조건이 나와야 된다. 원글과 답변을 구분하는 조건으로는 parent_no가 0이면 원글이다. 

첫 번째 레벨의 조건이 START WITH에 오면 된다.

CONNECT BY PRIOR는 부모와 자식을 연결 시키는 것

![day02%20c09936827aee4f788c1ff4677352cca1/Untitled%207.png](day02%20c09936827aee4f788c1ff4677352cca1/Untitled%207.png)

예전 글부터 보이므로 최신 글이라는 내림차순을 하려면 인라인뷰를 이용해서 정렬 작업을 미리 한번 해줄 필요가 있다. 

![day02%20c09936827aee4f788c1ff4677352cca1/Untitled%208.png](day02%20c09936827aee4f788c1ff4677352cca1/Untitled%208.png)

SIBLINGS를 쓰게 되면 내림차순 정렬을 할 때 동일 레벨 즉 같은 레벨의 행들 중에서 내림차순 하겠다라는 의미.

---

![day02%20c09936827aee4f788c1ff4677352cca1/Untitled%209.png](day02%20c09936827aee4f788c1ff4677352cca1/Untitled%209.png)

3.0버전부터는 web.xml문서를 안만들어도 된다.

설정 부분들은 3.0버전에서는 이런 설정을 annotation이라는 표시를 할 수 있다.

@Target은 클래스 선언 앞에만 쓸 수 있는 어노테이션이다.

@Retention 실행시에 처리되는 어노테이션이다.

web.xml이 없으면 전체 구조를 한눈에 볼 수 없으나 장점으로는 설정에 대한 부분도 있고 소스코드도 있기 때문에 핸들링할 수 있는 장점이 있다.

메인화면이 가장 먼저 와야 한다. 그래야 색상이라던가 레이아웃을 통일화 시킬 수 있다. 그 사이에 vo, DAO, Service만들고 각 화면의 서블릿을 구성해야 한다.

![day02%20c09936827aee4f788c1ff4677352cca1/Untitled%2010.png](day02%20c09936827aee4f788c1ff4677352cca1/Untitled%2010.png)

회사마다 또는 팀마다 실행 설계서라는게 있다. 실행 설계서에 맞게 만들어야 한다.

메인페이지 index.jsp가 요청이 될 것이고 게시판이 클릭이 되면 어떤 url이 요청을 할 것이고 요청 방식은 무엇이고 요청 시 에 전달할 데이터가 있는가 서블릿 클래스 이름이 무엇인가 다 정리가 되고 난 다음에 즉 시작점에서부터 마무리까지 결정이 되어야 코딩을 하는 것이다.

jsp 윗단에 header, 아래에 footer가 들어가는 구성으로 해보자