package mybatis.pojo;

import java.sql.Timestamp;

/**
 * POJO User
 * @author xxq_1
 *
 */
public class User {

	private int userId;
	private String userPassword;
	private String userName;
	private String nickName;
	private int userTypeId;
	private String email;
	private int isValid;
	private Timestamp createdTime;
	private Timestamp updatedTime;

	public User() {

		this.createdTime = new Timestamp(System.currentTimeMillis());
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getUserTypeId() {
		return userTypeId;
	}

	public void setUserTypeId(int userTypeId) {
		this.userTypeId = userTypeId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public int getIsValid() {
		return isValid;
	}

	public void setIsValid(int isValid) {
		this.isValid = isValid;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", userPassword=" + userPassword + ", userName=" + userName + ", nickName="
				+ nickName + ", userTypeId=" + userTypeId + ", email=" + email + ", isValid=" + isValid
				+ ", createdTime=" + createdTime + ", updatedTime=" + updatedTime + "]";
	}



}
