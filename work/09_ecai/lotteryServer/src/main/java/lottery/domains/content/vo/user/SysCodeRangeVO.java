package lottery.domains.content.vo.user;

public class SysCodeRangeVO {

	private int accountCode; // 账号等级，如1960
	private int allocateCode; // 开号等级，如1960
	private int quantity; // 配额数量，如3

	public SysCodeRangeVO(int accountCode, int allocateCode, int quantity) {
		this.accountCode = accountCode;
		this.allocateCode = allocateCode;
		this.quantity = quantity;
	}

	public int getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(int accountCode) {
		this.accountCode = accountCode;
	}

	public int getAllocateCode() {
		return allocateCode;
	}

	public void setAllocateCode(int allocateCode) {
		this.allocateCode = allocateCode;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}