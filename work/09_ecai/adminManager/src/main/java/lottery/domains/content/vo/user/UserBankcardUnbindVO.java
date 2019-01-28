package lottery.domains.content.vo.user;

import org.apache.commons.lang.StringUtils;

import lottery.domains.content.entity.UserBankcardUnbindRecord;
import lottery.domains.pool.LotteryDataFactory;

public class UserBankcardUnbindVO {
	private int id;	//用户ID
	private String userIds;	//用户ID
	private String cardId;//银行卡号 索引  唯一约束
	private int unbindNum;//解绑次数
	private String unbindTime;//最后一次解绑时间
	private String username;//用户名
	
	public UserBankcardUnbindVO(UserBankcardUnbindRecord entity,LotteryDataFactory df){
		this.id = entity.getId();
		this.userIds = entity.getUserIds();
		this.cardId = entity.getCardId();
		this.unbindNum = entity.getUnbindNum();
		this.unbindTime = entity.getUnbindTime();
		String usernames = "";
		if(entity.getUserIds() != null && !entity.getUserIds().equals("")){
			if(entity.getUserIds().contains("#")){
				String [] ids = entity.getUserIds().split("#");
				StringBuffer nameapp = new StringBuffer();
				for (String string : ids) {
					if (StringUtils.isNotBlank(string)) {
						UserVO user = df.getUser(Integer.parseInt(string));
						if(user != null) {
							nameapp.append(user.getUsername()).append(",");
						}
					}
				}
				String res = nameapp.toString();
				usernames = res.substring(0,res.length()-1);
			}else{
				UserVO user = df.getUser(Integer.parseInt(entity.getUserIds()));
				if(user != null) {
					usernames = user.getUsername();
				}
			}
		}
		this.username = usernames;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserIds() {
		return userIds;
	}
	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}
	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public int getUnbindNum() {
		return unbindNum;
	}

	public void setUnbindNum(int unbindNum) {
		this.unbindNum = unbindNum;
	}

	public String getUnbindTime() {
		return unbindTime;
	}

	public void setUnbindTime(String unbindTime) {
		this.unbindTime = unbindTime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
