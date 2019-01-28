package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import lottery.domains.content.biz.UserMessageService;
import lottery.domains.content.dao.UserMessageDao;
import lottery.domains.content.entity.UserMessage;
import lottery.domains.content.global.Global;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserMessageServiceImpl implements UserMessageService {

	@Autowired
	private UserMessageDao uMessageDao;

	@Override
	public boolean send(int type, int toUid, int fromUid, String subject, String content) {
		String time = new Moment().toSimpleTime();
		int toStatus = Global.USER_MESSAGE_STATUS_UNREAD;
		int fromStatus = Global.USER_MESSAGE_STATUS_UNREAD;
		UserMessage entity = new UserMessage(toUid, fromUid, type, subject, content, time, toStatus, fromStatus);
		return uMessageDao.add(entity);
	}

	@Override
	public boolean updateInboxMessage(int userId, int[] ids, int status) {
		return uMessageDao.updateInboxMessage(userId, ids, status);
	}

	@Override
	public boolean updateOutboxMessage(int userId, int[] ids, int status) {
		return uMessageDao.updateOutboxMessage(userId, ids, status);
	}
}