package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserDailySettle;
import lottery.domains.pool.DataFactory;

/**
 * Created by Nick on 2016/11/01
 */
public class UserDailySettleVO {
	private int id;
	private String username; // 用户名
	private String scaleLevel; // 分红比率，阶梯 逗号隔开 x.xx，0.3代表30%
	private String lossLevel; // 亏损，多阶梯逗号隔开 xx.xx 单位万
	private String salesLevel; // 销量，阶梯 逗号隔开 单位万 xx.xx
	private String userLevel;// 人数阶梯，逗号隔开
	private int minValidUser; // 最低有效会员要求
	private String createTime; // 接受时间
	private String agreeTime; // 生效时间
	private int status; // 1：生效。2：待同意。3：已过期。4：无效
	private int fixed; // 0：浮动比例。1：固定比例
	private double minScale; // 浮动最小比例
	private double maxScale; // 浮动最大比例

	public UserDailySettleVO(UserDailySettle bean, DataFactory dataFactory) {
		this.id = bean.getId();
		UserVO userVO = dataFactory.getUser(bean.getUserId());
		if (userVO != null) {
			this.username = userVO.getUsername();
		}
		this.scaleLevel = bean.getScaleLevel();
		this.lossLevel = bean.getLossLevel();
		this.salesLevel = bean.getSalesLevel();
		this.minValidUser = bean.getMinValidUser();
		this.createTime = bean.getCreateTime();
		this.agreeTime = bean.getAgreeTime();
		this.status = bean.getStatus();
		this.fixed = bean.getFixed();
		this.minScale = bean.getMinScale();
		this.maxScale = bean.getMaxScale();
		this.userLevel = bean.getUserLevel();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getScaleLevel() {
		return scaleLevel;
	}

	public void setScaleLevel(String scaleLevel) {
		this.scaleLevel = scaleLevel;
	}

	public String getLossLevel() {
		return lossLevel;
	}

	public void setLossLevel(String lossLevel) {
		this.lossLevel = lossLevel;
	}

	public String getSalesLevel() {
		return salesLevel;
	}

	public void setSalesLevel(String salesLevel) {
		this.salesLevel = salesLevel;
	}

	public int getMinValidUser() {
		return minValidUser;
	}

	public void setMinValidUser(int minValidUser) {
		this.minValidUser = minValidUser;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getAgreeTime() {
		return agreeTime;
	}

	public void setAgreeTime(String agreeTime) {
		this.agreeTime = agreeTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getFixed() {
		return fixed;
	}

	public void setFixed(int fixed) {
		this.fixed = fixed;
	}

	public double getMinScale() {
		return minScale;
	}

	public void setMinScale(double minScale) {
		this.minScale = minScale;
	}

	public double getMaxScale() {
		return maxScale;
	}

	public void setMaxScale(double maxScale) {
		this.maxScale = maxScale;
	}

	public String getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(String userLevel) {
		this.userLevel = userLevel;
	}
}
