package lottery.domains.content.biz.impl;

import javautils.ip.IpUtil;
import lottery.domains.content.biz.UserLoginLogService;
import lottery.domains.content.dao.UserLoginLogDao;
import lottery.domains.content.entity.UserLoginLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

@Service
public class UserLoginLogServiceImpl implements UserLoginLogService {
	
	@Autowired
	private UserLoginLogDao uLoginLogDao;

	String ip2Address(String ip) {
		String address = "[未知地址]";
		try {
			String[] infos = IpUtil.find(ip);
			address = Arrays.toString(infos);
		} catch (Exception e) {}
		return address;
	}

	@Override
	public boolean add(int userId, String ip, String userAgent, String time,String loginLine) {
		String address = ip2Address(ip);
		try {
			if(System.getProperty("os.name").toLowerCase().startsWith("win")){
				address = new String(address.getBytes("GBK"),"UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String maskLoginLine = loginLine;

		if (StringUtils.isNotEmpty(loginLine)) {
			// 登录线路需要加密处理
			if (loginLine.indexOf("encrypt") > -1) {
				maskLoginLine = "加密线路";
			}
		}

		UserLoginLog entity = new UserLoginLog(userId, ip, address, userAgent, time, maskLoginLine);
		boolean result = uLoginLogDao.add(entity);
		return result;
	}
}