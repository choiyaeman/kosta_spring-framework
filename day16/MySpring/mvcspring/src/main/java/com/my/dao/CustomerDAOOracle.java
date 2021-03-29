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
			session.insert("mybatis.CustomerMapper.insert"
					      , c);
		}catch(Exception e) {
			Throwable causeException = e.getCause();
			if(causeException instanceof 
					SQLIntegrityConstraintViolationException) {
				SQLIntegrityConstraintViolationException scve =	(SQLIntegrityConstraintViolationException)causeException;
				if(scve.getErrorCode() == 1) {//PK중복
					throw new AddException("이미 사용중인 아이디입니다");
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
	public Customer selectById(String id) throws FindException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			Customer c = session.selectOne(
					"mybatis.CustomerMapper.selectById"
					, id);
			if(c == null) {
				throw new FindException("아이디에 해당하는 고객이 없습니다");
			}
			return c;
		}catch(Exception e) {
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) session.close();
		}
		
	}


	@Override
	public Customer delete(String id) throws RemoveException {
		Customer c;
		try {
			c = selectById(id);
		} catch (FindException e) {
			throw new RemoveException(e.getMessage());
		}
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			int rowcnt = session.delete("mybatis.CustomerMapper.delete", id);
			session.commit();
			if(rowcnt != 1) { //삭제건수가 0건
				throw new RemoveException("삭제실패: 아이디에 해당 고객이 없습니다");
			}
			
			return c;
		}catch(Exception e) {
			throw new RemoveException(e.getMessage());
		}finally {
			session.close();
		}
	}

	@Override
	public List<Customer> selectAll() throws FindException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			List<Customer> list = session.selectList("mybatis.CustomerMapper.selectAll");
			if(list.size() == 0) { 
				throw new FindException("고객이 한명도 없습니다");
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			if( session != null)
				session.close();
		}
	}

	@Override
	public Customer update(Customer c) throws ModifyException {
		//비번이름모두 수정 UPDATE customer SET pwd=?, name=? WHERE id=?
		//비번 수정            UPDATE customer SET pwd=? WHERE id=?
		//이름 수정            UPDATE customer SET name=? WHERE id=?
		if((c.getPwd() == null || c.getPwd().equals(""))&&
			(c.getName() == null || c.getName().equals(""))&&
			(c.getPostal() == null || 
			 c.getPostal().getBuildingno() == null || c.getPostal().getBuildingno().equals("")) &&
			(c.getAddr1() == null || c.getAddr1().equals(""))
			){
			throw new ModifyException("수정할 내용이 없습니다");
		}
		
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			int rowcnt = session.update("mybatis.CustomerMapper.update", c);
			session.commit();
			if(rowcnt != 1) {
				throw new ModifyException("수정되지 않았습니다"+ c);
			}
			return selectById(c.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModifyException(e.getMessage());
		}finally {
			session.close();
		}		
	}

}
