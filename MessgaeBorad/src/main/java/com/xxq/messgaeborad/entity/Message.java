package com.xxq.messgaeborad.entity;

import java.util.Date;

public class Message {
	private String id;

	private String fullName;

	private String phoneNumber;

	private String emailAddress;

	private String subject;

	private String message;

	private String userIp;

	private String createUser;

	private Date createdTime;

	private Integer delFlag;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id == null ? null : id.trim();
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName == null ? null : fullName.trim();
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber == null ? null : phoneNumber.trim();
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress == null ? null : emailAddress.trim();
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject == null ? null : subject.trim();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message == null ? null : message.trim();
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp == null ? null : userIp.trim();
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser == null ? null : createUser.trim();
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", fullName=" + fullName + ", phoneNumber=" + phoneNumber + ", emailAddress="
				+ emailAddress + ", subject=" + subject + ", message=" + message + ", userIp=" + userIp
				+ ", createUser=" + createUser + ", createdTime=" + createdTime + ", delFlag=" + delFlag + "]";
	}
}