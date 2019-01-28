package lottery.domains.content.vo.config;

/**
 * 游戏红比例规则
 */
public class GameDividendConfigRule {
	private double fromLoss; // 从xx起
	private double toLoss; // 从xx起
	private double scale; // 比例

	public GameDividendConfigRule(double fromLoss, double toLoss, double scale) {
		this.fromLoss = fromLoss;
		this.toLoss = toLoss;
		this.scale = scale;
	}

	public double getFromLoss() {
		return fromLoss;
	}

	public void setFromLoss(double fromLoss) {
		this.fromLoss = fromLoss;
	}

	public double getToLoss() {
		return toLoss;
	}

	public void setToLoss(double toLoss) {
		this.toLoss = toLoss;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}
}