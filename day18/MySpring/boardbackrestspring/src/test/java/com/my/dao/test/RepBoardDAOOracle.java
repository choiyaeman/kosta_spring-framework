package com.my.dao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.my.dao.RepBoardDAO;
import com.my.exception.FindException;
import com.my.exception.ModifyException;
import com.my.exception.RemoveException;
import com.my.vo.RepBoard;

import lombok.extern.log4j.Log4j;

//Spring용 단위테스트
//@WebAppConfiguration //JUnit5인 경우 
@RunWith(SpringJUnit4ClassRunner.class) //Juni4인 경우

//Spring 컨테이너용 XML파일 설정
@ContextConfiguration(locations={
		"file:src/main/webapp/WEB-INF/spring/root-context.xml", 
		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml"})
@Log4j
public class RepBoardDAOOracle {
	@Autowired
	@Qualifier("oracle")
	private RepBoardDAO dao;
	//private RepBoardDAO dao = new com.my.dao.RepBoardDAOOracle();
	
	@Test
	public void selectByBoard_no() {
		int board_no = 25; 
		int expParent_no = 0;
		String expBoard_title = "MVC";
		String expBoard_writer = "test"; 
		try {
			RepBoard b = dao.selectByBoard_no(board_no);
			assertNotNull(b);			
//			assertEquals(expParent_no, b.getParent_no());
			assertTrue(expParent_no  == b.getParent_no());
			assertEquals(expBoard_title, b.getBoard_title());
			assertEquals(expBoard_writer, b.getBoard_writer());			
		} catch (FindException e) {
			e.printStackTrace();
		}
	}	
//	@Test
	public void selectAll() throws FindException {		
		List<RepBoard> list = dao.selectAll();
		int expListSize = 21;
		assertTrue(expListSize == list.size());
	}
	
//	@Test
	public void update() throws FindException, ModifyException {
		int board_no = 2;
		
		RepBoard board = dao.selectByBoard_no(board_no);
		String board_pwd = board.getBoard_pwd();
		//------제목과 비번 모두 변경----------
		log.info("제목과 비번 모두 변경");
		String expectedTitle = "upd-mybatis3";
		String expectedPwd = "upd3";
		board.setBoard_title(expectedTitle);
		board.setBoard_pwd(expectedPwd);		
		dao.update(board, board_pwd);
		
		board = dao.selectByBoard_no(board_no);
		assertEquals(expectedTitle, board.getBoard_title());
		assertEquals(expectedPwd, board.getBoard_pwd());
		
		
		//-----제목만 변경----------
		log.info("제목만 변경");
		board_pwd = board.getBoard_pwd();
		
		expectedTitle = "upd-mybatis3";
		board.setBoard_title(expectedTitle);
		board.setBoard_pwd(null);
		log.error(board.getBoard_title() + ":" + board.getBoard_pwd());
		dao.update(board, board_pwd);
		board = dao.selectByBoard_no(board_no);		
		assertEquals(expectedTitle, board.getBoard_title());
		assertEquals(expectedPwd, board.getBoard_pwd());
		
		//-----비번만 변경----------
		log.info("비번만 변경");
		board_pwd = board.getBoard_pwd();
		expectedPwd = "upd2";
		board.setBoard_title(null);
		board.setBoard_pwd(expectedPwd);
		dao.update(board, board_pwd);
		board = dao.selectByBoard_no(board_no);		
		assertEquals(expectedTitle, board.getBoard_title());
		assertEquals(expectedPwd, board.getBoard_pwd());
	}
	
	//@Test
	public void updateCnt() throws ModifyException, FindException {
		int board_no = 1;
		int beforeCnt = dao.selectByBoard_no(board_no).getBoard_cnt();
		dao.updateBoardCnt(board_no);
		
		RepBoard board = dao.selectByBoard_no(board_no);
		int expectedCnt = beforeCnt+1;
		assertEquals(expectedCnt, board.getBoard_cnt());
	}
	
	//@Test
	public void delete() throws FindException, RemoveException {
		int board_no = 60;
		String board_pwd = dao.selectByBoard_no(board_no).getBoard_pwd();
		dao.delete(board_no, board_pwd);
	}
}