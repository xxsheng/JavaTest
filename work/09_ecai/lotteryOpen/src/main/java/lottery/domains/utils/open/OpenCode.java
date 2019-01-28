package lottery.domains.utils.open;

import lottery.domains.content.entity.LotteryOpenCode;

public class OpenCode {

	private String code;
	private String expect;

	public OpenCode(LotteryOpenCode bean) {
		this.code = bean.getCode();
		this.expect = bean.getExpect();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getExpect() {
		return expect;
	}

	public void setExpect(String expect) {
		this.expect = expect;
	}

}