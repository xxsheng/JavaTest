package lottery.domains.pool.play;

public interface HConstants {
	
	/**
	 *SSC大小单双
	 */
	interface TicketSeriesType {
		String SSC_DA = "大";// ssc玩法大
		String SSC_ZH_DA = "总和大";// ssc玩法总和大
		String SSC_XIAO = "小";// ssc玩法小
		String SSC_ZH_XIAO = "总和小";// ssc玩法总和小
		String SSC_DAN = "单";// ssc玩法单
		String SSC_ZH_DAN = "总和单";// ssc玩法总和单
		String SSC_SHUANG = "双";// ssc玩法双
		String SSC_ZH_SHUANG = "总和双";// ssc玩法双

		String KL8_HZDAN ="单";//快乐8单双玩法
		String KL8_HZSHUANG ="双";
		
		int HZX_SRART = 210;
		int HZX_END = 809;
		int HZH = 810;
		int HZD_SRART = 811;
		int HZD_END = 1410;
		
		String KL8_HZXIAO = "小";
		String KL8_HZHE = "和";
		String KL8_HZDA = "大";
		
		String KL8_HZJI = "奇";
		String KL8_HZOU = "偶";
		
		String KL8_HZSHANG = "上";
		String KL8_HZZHONG = "中";
		String KL8_HZXIA = "下";
		
		int KL8_WX_JIN_START=210;
		int KL8_WX_JIN_END=695;
		int KL8_WX_MU_START=696;
		int KL8_WX_MU_END=763;
		int KL8_WX_SHUI_START=764;
		int KL8_WX_SHUI_END=855;
		int KL8_WX_HUO_START=856;
		int KL8_WX_HUO_END=923;
		int KL8_WX_TU_START=924;
		int KL8_WX_TU_END=1410;
		
		String KL8_JIN ="金";
		String KL8_MU ="木";
		String KL8_SHUI ="水";
		String KL8_HUO ="火";
		String KL8_TU ="土";
	}
	
	
	interface playPrize{
		//11X5定单双
		String [] dds11x5 = new String[]{"5单0双","4单1双", "3单2双", "2单3双","1单4双","0单5双"};
		String [] sscniuniu = new String[]{"牛大","牛小", "牛单", "牛双", "无牛", "牛牛", "牛1", "牛2", "牛3", "牛4", "牛5", "牛6", "牛7", "牛8", "牛9", "五条", "炸弹", "葫芦", "顺子", "三条", "两对", "单对", "散号"};
		//11x5 猜中位
		String [] czw11x5 = new String[]{"03,09","04,08","05,07","06"};
		//K3和值
		String [] k3hezhi = new String[]{"3,18","4,17","5,16","6,15","7,14","8,13","9,12","10,11"};
		//KL8
		String [] kl8hezhidx = new String[]{"大","和","小"};
		String [] kl8jopan = new String[]{"奇","和","偶"};
		String [] kl8sxpan = new String[]{"上","中","下"};
		String [] kl8wx = new String[]{"金","木","水","火","土"};
	}

}
