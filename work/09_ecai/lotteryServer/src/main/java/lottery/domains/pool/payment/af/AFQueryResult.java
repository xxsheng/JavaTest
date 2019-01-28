package lottery.domains.pool.payment.af;

public class AFQueryResult {
	private String result_code;
    private String result_msg; // 响应码 00-成功,其他失败
    private String merchant_no; // 响应消息
    private String order_no;
    private String order_amount;
    private String original_amount;
    private String upstream_settle;
    private String result;
    private String pay_time;
    private String trace_id;
    private String reserve;
    private String sign;
	public String getResult_code() {
		return result_code;
	}
	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}
	public String getResult_msg() {
		return result_msg;
	}
	public void setResult_msg(String result_msg) {
		this.result_msg = result_msg;
	}
	public String getMerchant_no() {
		return merchant_no;
	}
	public void setMerchant_no(String merchant_no) {
		this.merchant_no = merchant_no;
	}
	public String getOrder_no() {
		return order_no;
	}
	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}
	public String getOrder_amount() {
		return order_amount;
	}
	public void setOrder_amount(String order_amount) {
		this.order_amount = order_amount;
	}
	public String getOriginal_amount() {
		return original_amount;
	}
	public void setOriginal_amount(String original_amount) {
		this.original_amount = original_amount;
	}
	public String getUpstream_settle() {
		return upstream_settle;
	}
	public void setUpstream_settle(String upstream_settle) {
		this.upstream_settle = upstream_settle;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getPay_time() {
		return pay_time;
	}
	public void setPay_time(String pay_time) {
		this.pay_time = pay_time;
	}
	public String getTrace_id() {
		return trace_id;
	}
	public void setTrace_id(String trace_id) {
		this.trace_id = trace_id;
	}
	public String getReserve() {
		return reserve;
	}
	public void setReserve(String reserve) {
		this.reserve = reserve;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
    

}
