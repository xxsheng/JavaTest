package lottery.domains.content.vo.user;

public class SysCodeRangeVO {

	// 比如13.2的用户能开出13.1到13.1的用户15个

	private double point; // 指定用户等级
	private double minPoint; // 最大开多少等级
	private double maxPoint; // 最小开多少等级
	private int defaultQuantity; // 默认数量
	
	public SysCodeRangeVO() {
		
	}

	public double getMinPoint() {
		return minPoint;
	}

	public void setMinPoint(double minPoint) {
		this.minPoint = minPoint;
	}

	public double getMaxPoint() {
		return maxPoint;
	}

	public void setMaxPoint(double maxPoint) {
		this.maxPoint = maxPoint;
	}

	public double getPoint() {
		return point;
	}

	public void setPoint(double point) {
		this.point = point;
	}

	public int getDefaultQuantity() {
		return defaultQuantity;
	}

	public void setDefaultQuantity(int defaultQuantity) {
		this.defaultQuantity = defaultQuantity;
	}
}