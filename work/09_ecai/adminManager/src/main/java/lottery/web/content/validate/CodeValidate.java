package lottery.web.content.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import admin.web.WebJSONObject;
import javautils.StringUtil;
import lottery.domains.content.entity.Lottery;
import lottery.domains.pool.LotteryDataFactory;
import lottery.domains.utils.lottery.open.LotteryOpenUtil;
import lottery.domains.utils.lottery.open.OpenTime;

@Component
public class CodeValidate {
	
	@Autowired
	private LotteryOpenUtil lotteryOpenUtil;
	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	public boolean validateExpect(WebJSONObject json, String lottery, String expect) {
		boolean isTrueExpect = false;
		Lottery lotteryBean = lotteryDataFactory.getLottery(lottery);
		if(lotteryBean != null) {
			OpenTime bean = lotteryOpenUtil.getCurrOpenTime(lotteryBean.getId());
			if(bean.getExpect().compareTo(expect) > 0) isTrueExpect = true;
		}
		if(!isTrueExpect) {
			json.set(2, "2-2101");
		}
		return isTrueExpect;
	}
	
	public boolean validateCode(WebJSONObject json, String lottery, String code) {
		boolean isTrueCode = false;
		Lottery lotteryBean = lotteryDataFactory.getLottery(lottery);
		if(lotteryBean != null) {
			switch (lotteryBean.getType()) {
			case 1:
				if(isSsc(code)) isTrueCode = true; 
				break;
			case 2:
				if(is11x5(code)) isTrueCode = true; 
				break;
			case 3:
				if(isK3(code)) isTrueCode = true; 
				break;
			case 4:
				if(is3d(code)) isTrueCode = true; 
				break;
			case 5:
				if(isBjkl8(code)) isTrueCode = true; 
				break;
			case 6:
				if(isBjpk10(code)) isTrueCode = true; 
				break;
			case 7: // 龙虎
				if(isSsc(code)) isTrueCode = true;
				break;
			default:
				break;
			}
		}
		if(!isTrueCode) {
			json.set(2, "2-2100");
		}
		return isTrueCode;
	}
	
	public boolean isSsc(String s) {
		// 是否为空
		if(!StringUtil.isNotNull(s)) return false;
		String[] codes = s.split(",");
		// 数字长度
		if(codes.length != 5) return false;
		for (String tmpS : codes) {
			// 是否是数字
			if(!StringUtil.isInteger(tmpS)) return false;
			// 数字位数对不对
			if(tmpS.length() != 1) return false;
			int tmpC = Integer.parseInt(tmpS);
			// 数字的有效范围
			if(!(tmpC >= 0 && tmpC <= 9)) return false;
		}
		return true;
	}
	
	public boolean is11x5(String s) {
		// 是否为空
		if(!StringUtil.isNotNull(s)) return false;
		String[] codes = s.split(",");
		// 数字长度
		if(codes.length != 5) return false;
		for (String tmpS : codes) {
			// 是否是数字
			if(!StringUtil.isInteger(tmpS)) return false;
			// 数字位数对不对
			if(tmpS.length() != 2) return false;
			int tmpC = Integer.parseInt(tmpS);
			// 数字的有效范围
			if(!(tmpC >= 1 && tmpC <= 11)) return false;
		}
		return true;
	}
	
	public boolean isK3(String s) {
		// 是否为空
		if(!StringUtil.isNotNull(s)) return false;
		String[] codes = s.split(",");
		// 数字长度
		if(codes.length != 3) return false;
		for (String tmpS : codes) {
			// 是否是数字
			if(!StringUtil.isInteger(tmpS)) return false;
			// 数字位数对不对
			if(tmpS.length() != 1) return false;
			int tmpC = Integer.parseInt(tmpS);
			// 数字的有效范围
			if(!(tmpC >= 1 && tmpC <= 6)) return false;
		}
		return true;
	}
	
	public boolean is3d(String s) {
		// 是否为空
		if(!StringUtil.isNotNull(s)) return false;
		String[] codes = s.split(",");
		// 数字长度
		if(codes.length != 3) return false;
		for (String tmpS : codes) {
			// 是否是数字
			if(!StringUtil.isInteger(tmpS)) return false;
			// 数字位数对不对
			if(tmpS.length() != 1) return false;
			int tmpC = Integer.parseInt(tmpS);
			// 数字的有效范围
			if(!(tmpC >= 0 && tmpC <= 9)) return false;
		}
		return true;
	}
	
	public boolean isBjkl8(String s) {
		// 是否为空
		if(!StringUtil.isNotNull(s)) return false;
		String[] codes = s.split(",");
		// 数字长度
		if(codes.length != 20) return false;
		for (String tmpS : codes) {
			// 是否是数字
			if(!StringUtil.isInteger(tmpS)) return false;
			// 数字位数对不对
			if(tmpS.length() != 2) return false;
			int tmpC = Integer.parseInt(tmpS);
			// 数字的有效范围
			if(!(tmpC >= 1 && tmpC <= 80)) return false;
		}
		return true;
	}
	
	public boolean isBjpk10(String s) {
		// 是否为空
		if(!StringUtil.isNotNull(s)) return false;
		String[] codes = s.split(",");
		// 数字长度
		if(codes.length != 10) return false;
		for (String tmpS : codes) {
			// 是否是数字
			if(!StringUtil.isInteger(tmpS)) return false;
			// 数字位数对不对
			if(tmpS.length() != 2) return false;
			int tmpC = Integer.parseInt(tmpS);
			// 数字的有效范围
			if(!(tmpC >= 1 && tmpC <= 10)) return false;
		}
		return true;
	}

}