package lottery.domains.content.vo.config;

/**
 * 浮动日结比例
 */
public class DailySettleConfigRule {
	private double from; // 从xx起
	private double to; // 从xx起
	private double scale; // 比例

	public DailySettleConfigRule(double from, double to, double scale) {
		this.from = from;
		this.to = to;
		this.scale = scale;
	}

	public double getFrom() {
		return from;
	}

	public void setFrom(double from) {
		this.from = from;
	}

	public double getTo() {
		return to;
	}

	public void setTo(double to) {
		this.to = to;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}
}