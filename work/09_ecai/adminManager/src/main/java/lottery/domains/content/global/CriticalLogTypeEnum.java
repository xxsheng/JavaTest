package lottery.domains.content.global;

public class CriticalLogTypeEnum {

	/**
	 * 关键日志类型
	 * @author cavan
	 *
	 */
	public enum CriticalLogType {
		CRITICAL_LOG_TYPE_0(0, "修改用户登录密码"),
		CRITICAL_LOG_TYPE_1(1, "修改用户资金密码"),
		CRITICAL_LOG_TYPE_2(2, "修改用户绑定取款人"),
		CRITICAL_LOG_TYPE_3(3, "修改用户上下级转账权限"), 
		CRITICAL_LOG_TYPE_4(4, "重置用户绑定邮箱"), 
		CRITICAL_LOG_TYPE_5(5, "修改用户绑定邮箱"), 
		CRITICAL_LOG_TYPE_6(6, "操作用户转账"),
		CRITICAL_LOG_TYPE_7(7, "充值"), 
		CRITICAL_LOG_TYPE_8(-5, "<span>第三方处理失败 <i class=\"fa fa-question-circle cursor-pointer\" title=\"第三方状态，请前往第三方后台核对数据后再手动处理\" class=\"tippy\"></i></span> "),// 第三方处理失败
		CRITICAL_LOG_TYPE_9(-6, "<span>银行处理失败 <i class=\"fa fa-question-circle cursor-pointer\" title=\"第三方状态，请前往第三方后台核对数据后再手动处理\" class=\"tippy\"></i></span>"),// 银行处理失败
		CRITICAL_LOG_TYPE_10(-7, "<span>第三方拒绝支付 <i class=\"fa fa-question-circle cursor-pointer\" title=\"第三方状态，请前往第三方后台核对数据后再手动处理\" class=\"tippy\"></i></span>");//第三方拒绝支付
	
		private int type;
		private String content;

		CriticalLogType(int type, String content) {
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
			for (CriticalLogType st : CriticalLogType.values()) {
				if (st.getType()==id) {
					desc = st.getContent();
				}
			}
			return desc;
		}
	}
}
