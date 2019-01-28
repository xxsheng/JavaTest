package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import javautils.image.ImageUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserRegistLinkService;
import lottery.domains.content.dao.UserRegistLinkDao;
import lottery.domains.content.entity.UserRegistLink;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import lottery.web.WebJSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserRegistLinkServiceImpl implements UserRegistLinkService {

	@Autowired
	private UserRegistLinkDao uRegistLinkDao;

	@Autowired
	private DataFactory dataFactory;

	@Override
	public String add(WebJSON json, int userId, int type, String code, double point, String expTime, int amount, Integer deviceType) {
		// 最多不能超过20个链接
		List<UserRegistLink> registerLinks = uRegistLinkDao.getByUserId(userId);
		if (CollectionUtils.isNotEmpty(registerLinks) && registerLinks.size() >= 20) {
			json.set(2, "2-1093", 20);
			return null;
		}

		int _deviceType; // 设备类型；1：网页；2：手机；3：微信
		if (deviceType == null) {
			_deviceType = Global.REGISTER_DEVICE_TYPE_WEB;
		}
		else if (deviceType != 1 && deviceType != 2 && deviceType != 3) {
			_deviceType = Global.REGISTER_DEVICE_TYPE_WEB;
		}
		else {
			_deviceType = deviceType;
		}

		String qrCode = null;
		if (_deviceType == Global.REGISTER_DEVICE_TYPE_MOBILE) {
			// 请注意微信在扫码二维码的时候是有缓存的，前端可以动态加载JS并加时间戳
//			String mobileLink = dataFactory.getDoMainConfig().getMobileRegisterUrl() + "?code=" + code;
			String mobileLink = dataFactory.getDoMainConfig().getMobileRegisterUrl() + code;
			qrCode = ImageUtil.encodeQR(mobileLink, 200, 200);
			String time = new Moment().toSimpleTime();
			int status = 0;
			UserRegistLink entity = new UserRegistLink(userId, type, code, point, expTime, amount, time, status, deviceType, qrCode);
			uRegistLinkDao.add(entity);
			return qrCode;
		}
		else {
			String time = new Moment().toSimpleTime();
			int status = 0;
			UserRegistLink entity = new UserRegistLink(userId, type, code, point, expTime, amount, time, status, deviceType, qrCode);
			uRegistLinkDao.add(entity);

			List<String> urls = dataFactory.getDoMainConfig().getUrls();
			if (CollectionUtils.isNotEmpty(urls)) {
				int randomIndex = RandomUtils.nextInt(urls.size());
				String domain = urls.get(randomIndex);
				return domain;
			}

			return code;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PageList search(int userId, Integer deviceType, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;
		List<Criterion> criterions = new ArrayList<>();
		criterions.add(Restrictions.eq("userId", userId));
		if (deviceType != null) {
			criterions.add(Restrictions.eq("deviceType", deviceType));
		}
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("id"));
		return uRegistLinkDao.search(criterions, orders, start, limit);
	}

	@Override
	public boolean delete(int id, int userId) {
		return uRegistLinkDao.delete(id, userId);
	}

	@Override
	@Transactional(readOnly = true)
	public UserRegistLink get(String code) {
		return uRegistLinkDao.get(code);
	}
}