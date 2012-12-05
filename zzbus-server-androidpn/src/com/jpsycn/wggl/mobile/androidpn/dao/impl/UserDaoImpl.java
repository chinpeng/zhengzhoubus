/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.jpsycn.wggl.mobile.androidpn.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import com.jpsycn.wggl.mobile.androidpn.dao.BaseDao;
import com.jpsycn.wggl.mobile.androidpn.dao.UserDao;
import com.jpsycn.wggl.mobile.androidpn.model.User;

@Repository("userDao")
public class UserDaoImpl extends BaseDao implements UserDao {

	public User getUser(Long id) {
		return (User) getHibernateTemplate().get(User.class, id);
	}

	public void saveUser(User user) {
		getHibernateTemplate().save(user);
	}

	public void removeUser(User user) {
		getHibernateTemplate().delete(user);
	}

	public boolean exists(Long id) {
		User user = (User) getHibernateTemplate().get(User.class, id);
		return user != null;
	}

	@SuppressWarnings("unchecked")
	public List<User> getUsers() {
		return getHibernateTemplate().executeFind(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				List list = session.createQuery(
						"from User u order by u.createdDate desc").list();
				return list;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public User getUserByUsername(final String username)
			{
		List users = getHibernateTemplate().executeFind(
				new HibernateCallback() {

					@Override
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						List list = session
								.createQuery(
										"from User where username=:username")
								.setString("username", username).list();

						return list;
					}
				});
		
		
		if (users == null || users.isEmpty()) {
			return null;
		} else {
			return (User) users.get(0);
		}
	}

	@Override
	public void update(User user) {
		getHibernateTemplate().update(user);
	}

}
