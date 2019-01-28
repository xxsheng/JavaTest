package lottery.domains.content.vo.user;

public class UserCodeQuotaVO {
	private int userId; // 用户ID
	private int code; // 开号等级
	private int quantity; // 总量
	private int sysAllocateQuantity; // 系统分配数量
	private int upAllocateQuantity; // 上级分配数量
	private int surplus; // 剩余可用

	public UserCodeQuotaVO() {
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getQuantity() {
		return sysAllocateQuantity + upAllocateQuantity;
	}

	public int getSysAllocateQuantity() {
		return sysAllocateQuantity;
	}

	public void setSysAllocateQuantity(int sysAllocateQuantity) {
		this.sysAllocateQuantity = sysAllocateQuantity;
	}

	public int getUpAllocateQuantity() {
		return upAllocateQuantity;
	}

	public void setUpAllocateQuantity(int upAllocateQuantity) {
		this.upAllocateQuantity = upAllocateQuantity;
	}


	public int getSurplus() {
		return surplus;
	}

	public void setSurplus(int surplus) {
		if (surplus < 0) {
			this.surplus = 0;
		}
		else {
			this.surplus = surplus;
		}
	}
}