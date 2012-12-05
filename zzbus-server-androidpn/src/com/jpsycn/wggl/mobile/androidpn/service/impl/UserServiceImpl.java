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
package com.jpsycn.wggl.mobile.androidpn.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.jpsycn.wggl.mobile.androidpn.dao.UserDao;
import com.jpsycn.wggl.mobile.androidpn.model.User;
import com.jpsycn.wggl.mobile.androidpn.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {

    private final static Logger log = Logger.getLogger(UserServiceImpl.class);
    @Resource(name="userDao")
    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public User getUser(String userId) {
        return userDao.getUser(new Long(userId));
    }

    public List<User> getUsers() {
        return userDao.getUsers();
    }

    public void saveUser(User user)  {
        userDao.saveUser(user);
    }

    public User getUserByUsername(String username)  {
        return (User) userDao.getUserByUsername(username);
    }

    public void removeUser(Long userId) {
        log.debug("removing user: " + userId);
        User user= userDao.getUser(userId);
        userDao.removeUser(user);
    }

	@Override
	public void update(User user) {
		userDao.update(user);
	}

}
