package lottery.web.content.validate;

import javautils.StringUtil;
import lottery.web.WebJSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class UserSecurityValidate {

	public boolean required(WebJSON json, String question, String answer) {
		if(!StringUtil.isNotNull(question)) {
			json.set(2, "2-1029");
			return false;
		}
		if(!StringUtil.isNotNull(answer)) {
			json.set(2, "2-1030");
			return false;
		}
		return true;
	}
	
	public boolean required(WebJSON json, String question1, String answer1, String question2, String answer2, String question3, String answer3) {
		if(!StringUtil.isNotNull(question1) || !StringUtil.isNotNull(question2) || !StringUtil.isNotNull(question3)) {
			json.set(2, "2-1029");
			return false;
		}
		if(!StringUtil.isNotNull(answer1) || !StringUtil.isNotNull(answer2) || !StringUtil.isNotNull(answer3)) {
			json.set(2, "2-1030");
			return false;
		}
		return true;
	}

	public boolean isSame(WebJSON json, String answer1, String answer2, String answer3) {
		if (StringUtils.equals(answer2, answer1)) {
			json.set(2, "2-1081", 2, 1);
			return true;
		}
		if (StringUtils.equals(answer3, answer2)) {
			json.set(2, "2-1081", 3, 2);
			return true;
		}
		if (StringUtils.equals(answer3, answer1)) {
			json.set(2, "2-1081", 3, 1);
			return true;
		}

		return false;
	}
	
}