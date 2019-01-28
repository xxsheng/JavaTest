package lottery.domains.content.vo.user;

public class UserCodeRangeVO {
	
	private int code;
	private double maxLocatePoint;
	private double maxNotLocatePoint;
	private int maxLocatePointCode;
	public UserCodeRangeVO() {
		
	}
	
	public UserCodeRangeVO(int code, double maxLocatePoint, double maxNotLocatePoint,int maxLocatePointCode) {
		this.code = code;
		this.maxLocatePoint = maxLocatePoint;
		this.maxNotLocatePoint = maxNotLocatePoint;
		this.maxLocatePointCode =maxLocatePointCode;
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public double getMaxLocatePoint() {
		return maxLocatePoint;
	}
	public void setMaxLocatePoint(double maxLocatePoint) {
		this.maxLocatePoint = maxLocatePoint;
	}
	public double getMaxNotLocatePoint() {
		return maxNotLocatePoint;
	}
	public void setMaxNotLocatePoint(double maxNotLocatePoint) {
		this.maxNotLocatePoint = maxNotLocatePoint;
	}

	public int getMaxLocatePointCode() {
		return maxLocatePointCode;
	}

	public void setMaxLocatePointCode(int maxLocatePointCode) {
		this.maxLocatePointCode = maxLocatePointCode;
	}
	
}
