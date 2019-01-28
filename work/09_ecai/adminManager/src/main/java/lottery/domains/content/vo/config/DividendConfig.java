package lottery.domains.content.vo.config;

import java.util.ArrayList;
import java.util.List;

public class DividendConfig {
	private List<DividendConfigRule> zhaoShangScaleConfigs = new ArrayList<>(); // 招商契约分红配置，按亏损计算
	private List<DividendConfigRule> cjZhaoShangScaleConfigs = new ArrayList<>(); // 超级招商契约分红配置，按亏损计算

	private int zhaoShangMinValidUser; // 招商最小会员要求（含招商或超级招商）

	private String zhaoShangScaleLevels; // 招商契约分红比例
	private String zhaoShangSalesLevels; // 招商契约分红销量
	private String zhaoShangLossLevels; // 招商契约分红亏损

	private double cjZhaoShangMinScale; // 超级招商最小契约分红比例
	private double cjZhaoShangMaxScale; // 超级招商最小契约分红比例

	private double zhaoShangBelowMinScale; // 招商以下最小契约分红比例
	private double zhaoShangBelowMaxScale; // 招商以下最大契约分红比例

	private boolean enable; // 是否开启契约分红
	private double minBillingOrder; // 有效会员最低消费

	private int startLevel;// 允许签订契约账户等级
	private int maxSignLevel;// 允许签订契约最多条款数目
	private int minValidUserl;// 平台要求最小有效用户
	private boolean checkLoss;// 分红结算 是否检查亏损
	private double[] levelsLoss;// 亏损范围定义
	private double[] levelsSales;// 销量范围定义
	private double[] levelsScale;// 分红比率范围定义
	private int fixedType;// 结算类型 0=浮动 1=固定

	public List<DividendConfigRule> getZhaoShangScaleConfigs() {
		return zhaoShangScaleConfigs;
	}

	public void setZhaoShangScaleConfigs(List<DividendConfigRule> zhaoShangScaleConfigs) {
		this.zhaoShangScaleConfigs = zhaoShangScaleConfigs;
	}

	public List<DividendConfigRule> getCjZhaoShangScaleConfigs() {
		return cjZhaoShangScaleConfigs;
	}

	public void setCjZhaoShangScaleConfigs(List<DividendConfigRule> cjZhaoShangScaleConfigs) {
		this.cjZhaoShangScaleConfigs = cjZhaoShangScaleConfigs;
	}

	public int getZhaoShangMinValidUser() {
		return zhaoShangMinValidUser;
	}

	public void setZhaoShangMinValidUser(int zhaoShangMinValidUser) {
		this.zhaoShangMinValidUser = zhaoShangMinValidUser;
	}

	public double getCjZhaoShangMinScale() {
		return cjZhaoShangMinScale;
	}

	public void setCjZhaoShangMinScale(double cjZhaoShangMinScale) {
		this.cjZhaoShangMinScale = cjZhaoShangMinScale;
	}

	public double getCjZhaoShangMaxScale() {
		return cjZhaoShangMaxScale;
	}

	public void setCjZhaoShangMaxScale(double cjZhaoShangMaxScale) {
		this.cjZhaoShangMaxScale = cjZhaoShangMaxScale;
	}

	public double getZhaoShangBelowMinScale() {
		return zhaoShangBelowMinScale;
	}

	public void setZhaoShangBelowMinScale(double zhaoShangBelowMinScale) {
		this.zhaoShangBelowMinScale = zhaoShangBelowMinScale;
	}

	public double getZhaoShangBelowMaxScale() {
		return zhaoShangBelowMaxScale;
	}

	public void setZhaoShangBelowMaxScale(double zhaoShangBelowMaxScale) {
		this.zhaoShangBelowMaxScale = zhaoShangBelowMaxScale;
	}

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

	/**
	 * 添加招商契约分红配置
	 */
	public void addZhaoShangScaleConfig(double fromDailyBilling, double toDailyBilling, double scale) {
		DividendConfigRule rule = new DividendConfigRule(fromDailyBilling, toDailyBilling, scale);
		zhaoShangScaleConfigs.add(rule);
		// setZhaoShangMinMax();
	}

	/**
	 * 添加超级招商契约分红配置
	 */
	public void addCJZhaoShangScaleConfig(double fromDailyBilling, double toDailyBilling, double scale) {
		DividendConfigRule rule = new DividendConfigRule(fromDailyBilling, toDailyBilling, scale);
		cjZhaoShangScaleConfigs.add(rule);
		setCJZhaoShangMinMax();
	}

	public String getZhaoShangScaleLevels() {
		return zhaoShangScaleLevels;
	}

	public void setZhaoShangScaleLevels(String zhaoShangScaleLevels) {
		this.zhaoShangScaleLevels = zhaoShangScaleLevels;
	}

	public String getZhaoShangSalesLevels() {
		return zhaoShangSalesLevels;
	}

	public void setZhaoShangSalesLevels(String zhaoShangSalesLevels) {
		this.zhaoShangSalesLevels = zhaoShangSalesLevels;
	}

	public String getZhaoShangLossLevels() {
		return zhaoShangLossLevels;
	}

	public void setZhaoShangLossLevels(String zhaoShangLossLevels) {
		this.zhaoShangLossLevels = zhaoShangLossLevels;
	}

	/**
	 * 设置超级招商契约分红最小最大值
	 */
	private void setCJZhaoShangMinMax() {
		for (DividendConfigRule configRule : cjZhaoShangScaleConfigs) {
			if (this.cjZhaoShangMinScale == 0) {
				this.cjZhaoShangMinScale = configRule.getScale();
			} else if (this.cjZhaoShangMinScale > configRule.getScale()) {
				this.cjZhaoShangMinScale = configRule.getScale();
			}

			if (this.cjZhaoShangMaxScale == 0) {
				this.cjZhaoShangMaxScale = configRule.getScale();
			} else if (this.cjZhaoShangMaxScale < configRule.getScale()) {
				this.cjZhaoShangMaxScale = configRule.getScale();
			}
		}
	}

	public DividendConfigRule determineZhaoShangRule(double dailyBilling) {
		return determineRule(dailyBilling, zhaoShangScaleConfigs);
	}

	public DividendConfigRule determineCJZhaoShangRule(double dailyBilling) {
		return determineRule(dailyBilling, cjZhaoShangScaleConfigs);
	}

	private DividendConfigRule determineRule(double dailyBilling, List<DividendConfigRule> configsRules) {
		DividendConfigRule billingRule = null;
		// 判断销量，有效人数由调用处自己处理
		for (DividendConfigRule rule : configsRules) {
			if (rule.getTo() < 0) {
				if (dailyBilling >= rule.getFrom()) {
					billingRule = rule;
					break;
				}
			} else {
				if (dailyBilling >= rule.getFrom() && dailyBilling <= rule.getTo()) {
					billingRule = rule;
					break;
				}
			}
		}

		return billingRule;
	}

	public int getStartLevel() {
		return startLevel;
	}

	public void setStartLevel(int startLevel) {
		this.startLevel = startLevel;
	}

	public int getMaxSignLevel() {
		return maxSignLevel;
	}

	public void setMaxSignLevel(int maxSignLevel) {
		this.maxSignLevel = maxSignLevel;
	}

	public int getMinValidUserl() {
		return minValidUserl;
	}

	public void setMinValidUserl(int minValidUserl) {
		this.minValidUserl = minValidUserl;
	}

	public boolean isCheckLoss() {
		return checkLoss;
	}

	public void setCheckLoss(boolean checkLoss) {
		this.checkLoss = checkLoss;
	}

	public double[] getLevelsLoss() {
		return levelsLoss;
	}

	public void setLevelsLoss(double[] levelsLoss) {
		this.levelsLoss = levelsLoss;
	}

	public double[] getLevelsSales() {
		return levelsSales;
	}

	public void setLevelsSales(double[] levelsSales) {
		this.levelsSales = levelsSales;
	}

	public double[] getLevelsScale() {
		return levelsScale;
	}

	public void setLevelsScale(double[] levelsScale) {
		this.levelsScale = levelsScale;
	}

	public int getFixedType() {
		return fixedType;
	}

	public void setFixedType(int fixedType) {
		this.fixedType = fixedType;
	}

}