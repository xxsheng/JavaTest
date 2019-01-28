package lottery.domains.utils.lottery.open;

public class OpenTime {

	private String expect;
	private String startTime;
	private String stopTime;
	private String openTime;
	
	public OpenTime() {
		
	}
	
	public OpenTime(String expect, String startTime, String stopTime,
			String openTime) {
		this.expect = expect;
		this.startTime = startTime;
		this.stopTime = stopTime;
		this.openTime = openTime;
	}

	public String getExpect() {
		return expect;
	}

	public void setExpect(String expect) {
		this.expect = expect;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStopTime() {
		return stopTime;
	}

	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

}
