package lottery.domains.content.entity.activity;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * 幸运大转盘
 */
public class RebateRulesWheel {
	private List<WheelRules> rules;

	public List<WheelRules> getRules() {
		return rules;
	}

	public void setRules(List<WheelRules> rules) {
		this.rules = rules;
	}

	public static class WheelRules {
		private double minCost;
		private double maxCost;
		private Integer[] amounts;

		public double getMinCost() {
			return minCost;
		}

		public void setMinCost(double minCost) {
			this.minCost = minCost;
		}

		public double getMaxCost() {
			return maxCost;
		}

		public void setMaxCost(double maxCost) {
			this.maxCost = maxCost;
		}

		public Integer[] getAmounts() {
			return amounts;
		}

		public void setAmounts(Integer[] amounts) {
			this.amounts = amounts;
		}
	}

	public static void main(String[] args) {
		WheelRules rule1 = new WheelRules();
		rule1.setMinCost(10000);
		rule1.setMaxCost(49999);
		rule1.setAmounts(new Integer[]{18,28});

		WheelRules rule2 = new WheelRules();
		rule2.setMinCost(50000);
		rule2.setMaxCost(99999);
		rule2.setAmounts(new Integer[]{88,128});

		WheelRules rule3 = new WheelRules();
		rule3.setMinCost(100000);
		rule3.setMaxCost(199999);
		rule3.setAmounts(new Integer[]{168, 288});

		WheelRules rule4 = new WheelRules();
		rule4.setMinCost(200000);
		rule4.setMaxCost(499999);
		rule4.setAmounts(new Integer[]{518, 888});

		WheelRules rule5 = new WheelRules();
		rule5.setMinCost(500000);
		rule5.setMaxCost(999999);
		rule5.setAmounts(new Integer[]{1688});

		WheelRules rule6 = new WheelRules();
		rule6.setMinCost(1000000);
		rule6.setMaxCost(-1);
		rule6.setAmounts(new Integer[]{2888});

		List<WheelRules> rules = new ArrayList<>();
		rules.add(rule1);
		rules.add(rule2);
		rules.add(rule3);
		rules.add(rule4);
		rules.add(rule5);
		rules.add(rule6);

		RebateRulesWheel rulesWheel = new RebateRulesWheel();
		rulesWheel.setRules(rules);

		System.out.println(JSON.toJSONString(rulesWheel));
	}
}