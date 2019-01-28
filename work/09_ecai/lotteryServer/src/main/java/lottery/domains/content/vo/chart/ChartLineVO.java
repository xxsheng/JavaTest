package lottery.domains.content.vo.chart;

import java.util.ArrayList;
import java.util.List;

public class ChartLineVO {
	
	private String[] xAxis;
	private List<Number[]> yAxis = new ArrayList<>();
	
	public ChartLineVO() {
	
	}
	
	public String[] getxAxis() {
		return xAxis;
	}
	public void setxAxis(String[] xAxis) {
		this.xAxis = xAxis;
	}
	public List<Number[]> getyAxis() {
		return yAxis;
	}
	public void setyAxis(List<Number[]> yAxis) {
		this.yAxis = yAxis;
	}

}
