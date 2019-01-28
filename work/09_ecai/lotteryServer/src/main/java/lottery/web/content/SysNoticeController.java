package lottery.web.content;

import lottery.domains.content.vo.SysNoticeVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.helper.AbstractActionController;
import lottery.web.helper.session.SessionUser;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class SysNoticeController extends AbstractActionController {
	/**
	 * Service
	 */

	/**
	 * Validate
	 */

	/**
	 * Util
	 */

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;


	@RequestMapping(value = WUC.SYS_NOTICE_LAST_SIMPLE, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> SYS_NOTICE_LAST_SIMPLE(HttpSession session,HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		List<SysNoticeVO> list = dataFactory.listSysNoticeSimples();
		json.data("data",  CollectionUtils.isEmpty(list) ? null : list.get(0));
		json.set(0, "0-1");
		return json.toJson();
	}


	@RequestMapping(value = WUC.SYS_NOTICE_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> SYS_NOTICE_LIST(HttpSession session,HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		List<SysNoticeVO> list = dataFactory.listSysNotices();
		json.data("data",  list);
		json.set(0, "0-1");
		return json.toJson();
	}


	@RequestMapping(value = WUC.SYS_NOTICE_LIST_SIMPLE, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> SYS_NOTICE_LIST_SIMPLE(HttpSession session,HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		List<SysNoticeVO> list = dataFactory.listSysNoticeSimples();
		json.data("data",  list);
		json.set(0, "0-1");
		return json.toJson();
	}

}