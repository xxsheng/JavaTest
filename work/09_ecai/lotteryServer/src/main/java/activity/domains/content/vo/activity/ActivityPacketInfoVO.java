// package activity.domains.content.vo.activity;
//
// import lottery.domains.content.dao.UserInfoDao;
// import lottery.domains.content.entity.UserInfo;
// import lottery.domains.pool.DataFactory;
// import activity.domains.content.entity.ActivityPacketInfo;
//
// public class ActivityPacketInfoVO {
//
// 	private String username;
// 	private ActivityPacketInfo bean;
// 	private String avatar = "000";//默认为系统小图标
//
// 	public ActivityPacketInfoVO(ActivityPacketInfo bean, DataFactory dataFactory, UserInfoDao dao){
// 		this.bean = bean;
// 		if(bean.getUserId() != -1){
// 			this.username = dataFactory.getUser(bean.getUserId()).getUsername();
// 			UserInfo userInfo = dao.get(bean.getUserId());
// 			if(userInfo != null){
// 				this.avatar = userInfo.getAvatar()+"";
// 			}
// 		}else{
// 			//系统红包
// 			this.username = "红杉系统福利";
// 		}
// 	}
//
// 	public String getAvatar() {
// 		return avatar;
// 	}
//
// 	public void setAvatar(String avatar) {
// 		this.avatar = avatar;
// 	}
// 	public String getUsername() {
// 		return username;
// 	}
// 	public void setUsername(String username) {
// 		this.username = username;
// 	}
// 	public ActivityPacketInfo getBean() {
// 		return bean;
// 	}
// 	public void setBean(ActivityPacketInfo bean) {
// 		this.bean = bean;
// 	}
//
// }
