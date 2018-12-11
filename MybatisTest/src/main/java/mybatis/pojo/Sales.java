/**
 * 
 */
package mybatis.pojo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xxq_1
 *
 */
public class Sales {

	private int salesId;
	private String salesName;
	private String phone;
	private String fax;
	private String email;
	private int isValid;
	private int reportTo;
	private Timestamp createdTime;
	private Timestamp updatedTime;
	private User userinfo;
	private List<Customer> customers;

	public Sales() {
		this.createdTime = new Timestamp(System.currentTimeMillis());
		this.setCustomers(new ArrayList<Customer>());
	}

	public int getSalesId() {
		return salesId;
	}

	public void setSalesId(int salesId) {
		this.salesId = salesId;
	}

	public String getSalesName() {
		return salesName;
	}

	public void setSalesName(String salesName) {
		this.salesName = salesName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getIsValid() {
		return isValid;
	}

	public void setIsValid(int isValid) {
		this.isValid = isValid;
	}

	public int getReportTo() {
		return reportTo;
	}

	public void setReportTo(int reportTo) {
		this.reportTo = reportTo;
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

	public User getUserinfo() {
		return userinfo;
	}

	public void setUserinfo(User userinfo) {
		this.userinfo = userinfo;
	}

	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	@Override
	public String toString() {
		return "Sales [salesId=" + salesId + ", salesName=" + salesName + ", phone=" + phone + ", fax=" + fax
				+ ", email=" + email + ", isValid=" + isValid + ", reportTo=" + reportTo + ", createdTime="
				+ createdTime + ", updatedTime=" + updatedTime + ", userinfo=" + userinfo + "]";
	}

}
