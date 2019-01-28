package lottery.domains.content.vo.chart;

public class ChartPieVO {

	private String[] legend;
	private PieValue[] series;
	
	public String[] getLegend() {
		return legend;
	}

	public void setLegend(String[] legend) {
		this.legend = legend;
	}

	public PieValue[] getSeries() {
		return series;
	}

	public void setSeries(PieValue[] series) {
		this.series = series;
	}

	public class PieValue {

		private String name;
		private Number value;

		public PieValue() {

		}

		public PieValue(String name, Number value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Number getValue() {
			return value;
		}

		public void setValue(Number value) {
			this.value = value;
		}

	}
}