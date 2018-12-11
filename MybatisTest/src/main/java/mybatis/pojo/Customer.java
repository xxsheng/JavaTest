/**
 * 
 */
package mybatis.pojo;

import java.sql.Timestamp;

/**
 * @author xxq_1
 *
 */
public class Customer {

	private int customerId;
	private String customerName;
	private User userInfo;
	private int isValid;
	private Timestamp createdTime;
	private Timestamp updatedTime;

	public Customer() {
		this.createdTime = new Timestamp(System.currentTimeMillis());
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public User getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(User userInfo) {
		this.userInfo = userInfo;
	}

	public int getIsValid() {
		return isValid;
	}

	public void setIsValid(int isValid) {
		this.isValid = isValid;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public Timestamp getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Timestamp updatedTime) {
		this.updatedTime = updatedTime;
	}

	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", customerName=" + customerName + ", userInfo=" + userInfo
				+ ", isValid=" + isValid + ", createdTime=" + createdTime + ", updatedTime=" + updatedTime + "]";
	}

}
