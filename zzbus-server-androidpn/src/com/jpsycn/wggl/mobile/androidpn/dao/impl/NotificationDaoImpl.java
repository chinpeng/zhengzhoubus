/**
 * 
 */
package com.jpsycn.wggl.mobile.androidpn.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jpsycn.wggl.mobile.androidpn.dao.BaseDao;
import com.jpsycn.wggl.mobile.androidpn.dao.NotificationDao;
import com.jpsycn.wggl.mobile.androidpn.model.NotificationMO;
import com.jpsycn.wggl.mobile.androidpn.model.ReportVO;

@Repository("notificationDao")
public class NotificationDaoImpl extends BaseDao implements
		NotificationDao {

	public void deleteNotification(Long id) {
		getHibernateTemplate().delete(queryNotificationById(id));
	}

	public NotificationMO queryNotificationById(Long id) {
		NotificationMO notificationMO = (NotificationMO) getHibernateTemplate()
				.get(NotificationMO.class, id);
		return notificationMO;
	}

	public void saveNotification(NotificationMO notificationMO) {
		getHibernateTemplate().saveOrUpdate(notificationMO);
		getHibernateTemplate().flush();
	}

	public void updateNotification(NotificationMO notificationMO) {
		getHibernateTemplate().update(notificationMO);
		getHibernateTemplate().flush();
	}

	@SuppressWarnings("unchecked")
	public List<NotificationMO> queryNotificationByUserName(String userName,
			String messageId) {
		Object[] params = new Object[] {userName, messageId};
		return getHibernateTemplate()
				.find(
						"from NotificationMO n where n.username=? and n.messageId=? order by n.createTime desc",
						params);
	}

	public int queryCountByStatus(String status, String messageId) {
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<NotificationMO> queryNotification(NotificationMO mo) {
		return getHibernateTemplate().findByExample(mo);
	}

	public List<ReportVO> queryReportVO(NotificationMO mo) {
		// TODO Auto-generated method stub
		return null;
	}

}
