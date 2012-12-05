package com.jpsycn.wggl.mobile.androidpn.dao;

import java.util.List;

import com.jpsycn.wggl.mobile.androidpn.model.NotificationMO;
import com.jpsycn.wggl.mobile.androidpn.model.ReportVO;

/**
 * @author chengqiang.liu
 *
 */
public interface NotificationDao {
	
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
	 * ����û����֪ͨID��ѯ֪ͨ
	 * @param userName	�û���
	 * @param messageId	֪ͨID
	 * @return List<NotificationMO
	 */
	public List<NotificationMO> queryNotificationByUserName(String userName,String messageId);

	/**
	 * ���״̬��IDͳ��֪ͨ����
	 * @param status	״̬
	 * @param messageId ID
	 * @return
	 */
	public int queryCountByStatus(String status,String messageId);
	
	/**
	 * ���������ѯ֪ͨ�б�
	 * @param mo ��ѯ����
	 * @return List<ReportVO>
	 */
	public List<ReportVO> queryReportVO(NotificationMO mo);
	
	/**
	 * ���������ѯ֪ͨ�б�
	 * @param mo ��ѯ����
	 * @return List<NotificationMO>
	 */
	public List<NotificationMO> queryNotification(NotificationMO mo);

}
