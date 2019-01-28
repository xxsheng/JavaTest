package lottery.domains.content.entity.activity;

import java.util.List;

/**
 * 抢红包活动
 */
public class RebateRulesGrab {
	
	private Common common;
	
	private List<Package> packages;
	
	public RebateRulesGrab(){
		
	}
	
	public Common getCommon() {
		return common;
	}

	public void setCommon(Common common) {
		this.common = common;
	}

	public List<Package> getPackages() {
		return packages;
	}

	public void setPackages(List<Package> packages) {
		this.packages = packages;
	}

	
	
	public class Package{
		
		private double amount;
		
		private double count;

		public Package(){
			
		}
		
		public double getAmount() {
			return amount;
		}

		public void setAmount(double amount) {
			this.amount = amount;
		}

		public double getCount() {
			return count;
		}

		public void setCount(double count) {
			this.count = count;
		}
		
		
	}

}