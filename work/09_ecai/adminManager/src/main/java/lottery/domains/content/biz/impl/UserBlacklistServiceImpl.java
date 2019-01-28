package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.PaymentBankService;
import lottery.domains.content.biz.UserBlacklistService;
import lottery.domains.content.dao.UserBlacklistDao;
import lottery.domains.content.entity.PaymentBank;
import lottery.domains.content.entity.UserBlacklist;
import lottery.domains.content.vo.user.UserBlacklistVO;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserBlacklistServiceImpl implements UserBlacklistService {
	
	@Autowired
	private UserBlacklistDao uBlacklistDao;
	
	@Autowired
	private PaymentBankService  paymentBankService;
	
	@Override
	public PageList search(String keyword, int start, int limit) {
		if(start < 0) start = 0;
		if(limit < 0) limit = 10;
		if(limit > 100) limit = 100;
		List<Criterion> criterions = new ArrayList<Criterion>();
		List<Order> orders = new ArrayList<Order>();
		if(StringUtil.isNotNull(keyword)) {
			criterions.add(Restrictions.or(
					Restrictions.eq("username", keyword), 
					Restrictions.like("cardName", keyword, MatchMode.ANYWHERE), 
					Restrictions.like("cardId", keyword, MatchMode.ANYWHERE), 
					Restrictions.like("ip", keyword, MatchMode.ANYWHERE)
				)
			);
		}
		PageList plist = uBlacklistDao.find(criterions, orders, start, limit);
		List<UserBlacklistVO> list = new ArrayList<>();
		for (Object tmpBean : plist.getList()) {
			UserBlacklist bbean = (UserBlacklist)tmpBean;
			if(null != bbean && null!= bbean.getBankId()){
				PaymentBank pk = paymentBankService.getById(bbean.getBankId());
				UserBlacklistVO vo = new UserBlacklistVO(bbean,pk.getName());
				list.add(vo);
			}else{
				UserBlacklistVO vo = new UserBlacklistVO(bbean,"");
				list.add(vo);
			}
		}
		plist.setList(list);
		return plist;
	}
	
	@Override
	public boolean add(String username, String cardName, String cardId,
			Integer bankId, String ip, String operatorUser,
			String operatorTime, String remarks) {
		if(StringUtil.isNotNull(operatorUser) && StringUtil.isNotNull(operatorTime)) {
			UserBlacklist entity = new UserBlacklist(cardName, operatorUser, operatorTime);
			entity.setUsername(username);
			entity.setCardId(cardId);
			entity.setBankId(bankId);
			entity.setIp(ip);
			entity.setRemarks(remarks);
			return uBlacklistDao.add(entity);
		}
		return false;
	}
	
	@Override
	public boolean delete(int id) {
		return uBlacklistDao.delete(id);
	}
	
}