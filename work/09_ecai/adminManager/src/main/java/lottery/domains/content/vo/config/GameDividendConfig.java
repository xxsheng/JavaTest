package lottery.domains.content.vo.config;

import java.util.ArrayList;
import java.util.List;

public class GameDividendConfig {
	private List<GameDividendConfigRule> zhuGuanScaleConfigs = new ArrayList<>(); // 主管游戏分红配置，按亏损计算
	private int zhuGuanMinValidUser; // 主管最小会员要求
	private double zhuGuanMinScale; // 主管最小游戏分红比例
	private double zhuGuanMaxScale; // 主管最小游戏分红比例
	private double zhuGuanBelowMinScale; // 主管以下最小契约分红比例
	private double zhuGuanBelowMaxScale; // 主管以下最大契约分红比例
	private boolean enable; // 是否开启游戏分红
	private double minBillingOrder; // 有效会员最低消费

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public double getMinBillingOrder() {
		return minBillingOrder;
	}

	public void setMinBillingOrder(double minBillingOrder) {
		this.minBillingOrder = minBillingOrder;
	}

	public double getZhuGuanBelowMaxScale() {
		return zhuGuanBelowMaxScale;
	}

	public void setZhuGuanBelowMaxScale(double zhuGuanBelowMaxScale) {
		this.zhuGuanBelowMaxScale = zhuGuanBelowMaxScale;
	}

	public double getZhuGuanBelowMinScale() {
		return zhuGuanBelowMinScale;
	}

	public void setZhuGuanBelowMinScale(double zhuGuanBelowMinScale) {
		this.zhuGuanBelowMinScale = zhuGuanBelowMinScale;
	}

	public double getZhuGuanMaxScale() {
		return zhuGuanMaxScale;
	}

	public void setZhuGuanMaxScale(double zhuGuanMaxScale) {
		this.zhuGuanMaxScale = zhuGuanMaxScale;
	}

	public double getZhuGuanMinScale() {
		return zhuGuanMinScale;
	}

	public void setZhuGuanMinScale(double zhuGuanMinScale) {
		this.zhuGuanMinScale = zhuGuanMinScale;
	}

	public int getZhuGuanMinValidUser() {
		return zhuGuanMinValidUser;
	}

	public void setZhuGuanMinValidUser(int zhuGuanMinValidUser) {
		this.zhuGuanMinValidUser = zhuGuanMinValidUser;
	}

	public List<GameDividendConfigRule> getZhuGuanScaleConfigs() {
		return zhuGuanScaleConfigs;
	}

	public void setZhuGuanScaleConfigs(List<GameDividendConfigRule> zhuGuanScaleConfigs) {
		this.zhuGuanScaleConfigs = zhuGuanScaleConfigs;
	}

	/**
	 * 添加主管契约分红配置
	 */
	public void addZhuGuanScaleConfig(double fromLoss, double toLoss, double scale) {
		GameDividendConfigRule rule = new GameDividendConfigRule(fromLoss, toLoss, scale);
		zhuGuanScaleConfigs.add(rule);
		setZhuGuanMinMax();
	}

	/**
	 * 设置主管契约分红最小最大值
	 */
	private void setZhuGuanMinMax() {
		for (GameDividendConfigRule configRule : zhuGuanScaleConfigs) {
			if (this.zhuGuanMinScale == 0) {
				this.zhuGuanMinScale = configRule.getScale();
			}
			else if (this.zhuGuanMinScale > configRule.getScale()) {
				this.zhuGuanMinScale = configRule.getScale();
			}

			if (this.zhuGuanMaxScale == 0) {
				this.zhuGuanMaxScale = configRule.getScale();
			}
			else if (this.zhuGuanMaxScale < configRule.getScale()) {
				this.zhuGuanMaxScale = configRule.getScale();
			}
		}
	}

	public GameDividendConfigRule determineZhuGuanRule(double loss) {
		return determineRule(loss, zhuGuanScaleConfigs);
	}

	private GameDividendConfigRule determineRule(double billingOrder, List<GameDividendConfigRule> configsRules) {
		GameDividendConfigRule billingRule = null;
		// 判断销量，有效人数由调用息自己处理
		for (GameDividendConfigRule rule : configsRules) {
			if (rule.getToLoss() < 0) {
				if (billingOrder >= rule.getFromLoss()) {
					billingRule = rule;
					break;
				}
			}
			else {
				if (billingOrder >= rule.getFromLoss() && billingOrder <= rule.getToLoss()) {
					billingRule = rule;
					break;
				}
			}
		}

		return billingRule;
	}
}