package com.my.dao;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.exception.ModifyException;
import com.my.exception.RemoveException;
import com.my.vo.Customer;

@Repository
public class CustomerDAOOracle implements CustomerDAO {
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	@Override
	public void insert(Customer c) throws AddException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			session.insert("mybatis.customerMapper.insert", c);
		}catch(Exception e) {
			Throwable causeException = e.getCause(); //warning예외를 얻어내는 방법
			if(causeException instanceof 
					SQLIntegrityConstraintViolationException) {
				SQLIntegrityConstraintViolationException scve = (SQLIntegrityConstraintViolationException)causeException;
				if(scve.getErrorCode() == 1) {//PK중복
					throw new AddException("이미 사용중인 아이디입니다.");
				}else { //CK, NOT NULL, FK
					throw new AddException(e.getMessage());
				}
			}else {
				throw new AddException(e.getMessage());
			}
		}finally {
			if(session != null) session.close();
		}
	}

	@Override
	public List<Customer> selectAll() throws FindException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Customer selectById(String id) throws FindException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			Customer c = session.selectOne("mybatis.customerMapper.selectById", id);
			if(c == null) {
				throw new FindException("아이디에 해당 고객이 없습니다.");
			}
			return c;
		}catch(Exception e) {
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) session.close();
		}
	}

	@Override
	public Customer update(Customer c) throws ModifyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Customer delete(String id) throws RemoveException {
		// TODO Auto-generated method stub
		return null;
	}

}
