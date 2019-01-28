package lottery.domains.content.vo.user;

import lottery.domains.content.entity.User;

public class UserCodeVO {

	private String username;
	private int code;
	private double lp;
	private double nlp;

	public UserCodeVO() {

	}

	public UserCodeVO(User bean) {
		this.username = bean.getUsername();
		this.code = bean.getCode();
		this.lp = bean.getLocatePoint();
		this.nlp = bean.getNotLocatePoint();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public double getLp() {
		return lp;
	}

	public void setLp(double lp) {
		this.lp = lp;
	}

	public double getNlp() {
		return nlp;
	}

	public void setNlp(double nlp) {
		this.nlp = nlp;
	}

}