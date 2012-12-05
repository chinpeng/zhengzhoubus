/**
 * 
 */
package com.jpsycn.wggl.mobile.androidpn.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.jpsycn.wggl.mobile.androidpn.dao.NotificationDao;
import com.jpsycn.wggl.mobile.androidpn.model.NotificationMO;
import com.jpsycn.wggl.mobile.androidpn.service.NotificationService;

@Service("notificationService")
public class NotificationServiceImpl implements NotificationService {
	private final static Logger log = Logger.getLogger(NotificationServiceImpl.class);
	@Resource(name="notificationDao")
	private NotificationDao notificationDao;
	
	public void setNotificationDao(NotificationDao notificationDao) {
		this.notificationDao = notificationDao;
	}

	public void deleteNotification(Long id) {
		log.info(" delete notification:id =" + id);
		notificationDao.queryNotificationById(id);
	}


	public NotificationMO queryNotificationById(Long id) {
		log.info(" query notification:id =" + id);
		return notificationDao.queryNotificationById(id);
	}


	public void saveNotification(NotificationMO notificationMO) {
		log.info(" create a new notification:username = " + notificationMO.getUsername());
		notificationDao.saveNotification(notificationMO);
	}


	public void updateNotification(NotificationMO notificationMO) {
		log.info(" update notification : stauts = " +notificationMO.getStatus());
		notificationDao.updateNotification(notificationMO);
	}

	public void createNotifications(List<NotificationMO> notificationMOs) {
		for (NotificationMO notificationMO : notificationMOs) {
			saveNotification(notificationMO);
		}
	}
	
	public NotificationMO queryNotificationByUserName(String userName,String messageId){
		List<NotificationMO> list = notificationDao.queryNotificationByUserName(userName, messageId);
		return list.isEmpty() ? null : list.get(0);
	}

	public List<NotificationMO> queryNotification(NotificationMO mo) {
		List<NotificationMO> list = notificationDao.queryNotification(mo);
		if(list.isEmpty()){
			log.info(" query notifications : list is null");
		}else{
			log.info(" query notifications : size = " + list.size());
		}
		return list;
	}

}
