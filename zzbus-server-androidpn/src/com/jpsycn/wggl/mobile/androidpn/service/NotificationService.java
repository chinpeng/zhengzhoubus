/**
 * 
 */
package com.jpsycn.wggl.mobile.androidpn.service;

import java.util.List;

import com.jpsycn.wggl.mobile.androidpn.model.NotificationMO;

/**
 * @author chengqiang.liu
 *
 */
public interface NotificationService {
	
	/**
	 * ����֪ͨ��Ϣ
	 * @param notificationMO
	 */
	public void saveNotification(NotificationMO notificationMO);
	
	/**
	 * �޸�֪ͨ��Ϣ
	 * @param notificationMO
	 */
	public void updateNotification(NotificationMO notificationMO);
	
	/**
	 * ɾ��֪ͨ
	 * @param id
	 */
	public void deleteNotification(Long id);
	
	/**
	 * �鿴֪ͨ
	 * @param id
	 * @return NotificationMO
	 */
	public NotificationMO queryNotificationById(Long id);
	
	/**
	 * ��������֪ͨ
	 * @param notificationMOs
	 */
	public void createNotifications(List<NotificationMO> notificationMOs);
	
	/**
	 * ����û����֪ͨID��ѯ֪ͨ
	 * @param userName	�û���
	 * @param messageId	֪ͨID
	 * @return NotificationMO
	 */
	public NotificationMO queryNotificationByUserName(String userName,String messageId);
	
	/**
	 * ���������ѯ֪ͨ�б�
	 * @param mo ��ѯ����
	 * @return List<NotificationMO>
	 */
	public List<NotificationMO> queryNotification(NotificationMO mo);
}
