package lottery.domains.content.global;

public class RemitStatusConstants {

	public enum Status {
		SMS_REMIT_STATUS_0(0, "待处理"),// 待处理
		SMS_REMIT_STATUS_1(1, "银行处理中"),// 银行处理中
		SMS_REMIT_STATUS_2(2, "<span style=\"color: #35AA47;\">打款完成</span>"),// 打款完成
		SMS_REMIT_STATUS_3(3, "<span>第三方待处理 <i class=\"fa fa-question-circle cursor-pointer tippy\" title=\"系统自动同步第三方状态,请稍候刷新再看\"></i></span>"), // 第三方待处理
		SMS_REMIT_STATUS_4(-1, "<span style=\"color: #D84A38;\">请求失败</span>"), // 请求失败
		SMS_REMIT_STATUS_5(-2, "<span style=\"color: #D84A38;\">打款失败</span>"), // 打款失败
		SMS_REMIT_STATUS_6(-3, "<span>查询状态中 <i class=\"fa fa-question-circle cursor-pointer tippy\" title=\"指API代付过程中发生未知异常，系统尝试主动与第三方进行核对，超过10分钟后系统不再处理，此时请前往第三方后台核对数据后再手动处理\"></i></span>"),// 查询状态中
		SMS_REMIT_STATUS_7(-4, "<span style=\"color: #D84A38;\">未知状态 <i class=\"fa fa-question-circle cursor-pointer tippy\" title=\"指API代付过程中发生未知异常或第三方处理超时，系统无法确定该次代付是否成功，请前往第三方后台核对数据后再手动处理\"></i></span>"), // 未知状态
		SMS_REMIT_STATUS_8(-5, "<span>第三方处理失败 <i class=\"fa fa-question-circle cursor-pointer tippy\" title=\"第三方状态，请前往第三方后台核对数据后再手动处理\"></i></span> "),// 第三方处理失败
		SMS_REMIT_STATUS_9(-6, "<span>银行处理失败 <i class=\"fa fa-question-circle cursor-pointer tippy\" title=\"第三方状态，请前往第三方后台核对数据后再手动处理\"></i></span>"),// 银行处理失败
		SMS_REMIT_STATUS_10(-7, "<span>第三方拒绝支付 <i class=\"fa fa-question-circle cursor-pointer tippy\" title=\"第三方状态，请前往第三方后台核对数据后再手动处理\"></i></span>");//第三方拒绝支付
	
		private int type;
		private String content;

		Status(int type, String content) {
			this.type = type;
			this.content = content;
		}


		public int getType() {
			return type;
		}

		public String getContent() {
			return content;
		}


		/**
		 * 根据id获取内容
		 * @param id
		 * @return
		 */
		public static String getTypeByContent(int id) {
			String desc = null;
			for (Status st : Status.values()) {
				if (st.getType()==id) {
					desc = st.getContent();
				}
			}
			return desc;
		}
	}
}
