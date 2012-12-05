package com.jpsycn.wggl.mobile.androidpn.dao;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public abstract class BaseDao extends HibernateDaoSupport{

	@Resource(name="sessionFactory")
	//通过springResource注解加载session工厂指向spirng.xml中sessionFactory
	private SessionFactory mySessionFactory;

	@PostConstruct
	public void injectSessionFactory() {
		super.setSessionFactory(mySessionFactory);
	}

	public void deleteCarryOutById(Integer id) throws DataAccessException {
		// TODO Auto-generated method stub
		return;
	}
}
