package lottery.domains.content.vo.config;

import lottery.domains.content.vo.user.SysCodeRangeVO;

import java.util.ArrayList;
import java.util.List;

public class CodeConfig {

	private int sysCode;
	private double sysLp;
	private double sysNlp;
	private int sysMinCode;
	private double sysMinLp;

	private List<SysCodeRangeVO> sysCodeRange = new ArrayList<>();

	public CodeConfig() {

	}

	public int getSysCode() {
		return sysCode;
	}

	public void setSysCode(int sysCode) {
		this.sysCode = sysCode;
	}

	public double getSysLp() {
		return sysLp;
	}

	public void setSysLp(double sysLp) {
		this.sysLp = sysLp;
	}

	public double getSysNlp() {
		return sysNlp;
	}

	public void setSysNlp(double sysNlp) {
		this.sysNlp = sysNlp;
	}

	public List<SysCodeRangeVO> getSysCodeRange() {
		return sysCodeRange;
	}

	public void setSysCodeRange(List<SysCodeRangeVO> sysCodeRange) {
		this.sysCodeRange = sysCodeRange;
	}

	public int getSysMinCode() {
		return sysMinCode;
	}

	public void setSysMinCode(int sysMinCode) {
		this.sysMinCode = sysMinCode;
	}

	public double getSysMinLp() {
		return sysMinLp;
	}

	public void setSysMinLp(double sysMinLp) {
		this.sysMinLp = sysMinLp;
	}

}