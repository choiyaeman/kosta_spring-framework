package com.my.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.vo.OrderInfo;
import com.my.vo.OrderLine;
@Repository
public class OrderDAOOracle implements OrderDAO {
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	@Override
	@Transactional (rollbackFor = AddException.class) //AddException 발생했을때 롤백하겠다 의미
	public void insert(OrderInfo info) throws AddException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			
			//info추가
			insertInfo(session, info);
			
			//info의 lines추가
			insertLines(session, info.getLines());
			
			//session.commit();
		}catch(Exception e) {
			//session.rollback();
			throw new AddException(e.getMessage());
		}finally {
			if(session != null) session.close();
		}
	}
	
	private void insertInfo(SqlSession session, OrderInfo info)  throws AddException{
		session.insert("mybatis.OrderMapper.insertInfo", info);
	}
	private void insertLines(SqlSession session, List<OrderLine> lines)  throws AddException{
		for(OrderLine line: lines) {
			session.insert("mybatis.OrderMapper.insertLine", line);
		}
	}
	
	@Override
	public List<OrderInfo> selectById(String order_id) throws FindException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			List<OrderInfo> list = session.selectList("mybatis.OrderMapper.selectById", order_id);
			if(list.size() == 0) {
				throw new FindException("주문내역이 없습니다.");
			}
			return list;
		}catch(Exception e){
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) session.close();
		}
	}

}
