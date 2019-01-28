package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.redis.JedisTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.LotteryOpenStatusService;
import lottery.domains.content.dao.LotteryOpenCodeDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.lottery.LotteryOpenStatusVO;
import lottery.domains.pool.LotteryDataFactory;
import lottery.domains.utils.lottery.open.LotteryOpenUtil;
import lottery.domains.utils.lottery.open.OpenTime;

@Service
public class LotteryOpenStatusServiceImpl implements LotteryOpenStatusService {
	private static final String ADMIN_OPEN_CODE_KEY = "ADMIN_OPEN_CODE:%s";
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Autowired
	private LotteryOpenCodeDao lotteryOpenCodeDao;
	
	@Autowired
	private LotteryOpenUtil lotteryOpenUtil;
	
	 @Autowired
	private JedisTemplate jedisTemplate;
	
	@Override
	public List<LotteryOpenStatusVO> search(String lotteryId, String date) {
		List<LotteryOpenStatusVO> list = new ArrayList<>();
		if(StringUtil.isDateString(date) && StringUtil.isInteger(lotteryId)) {
			Lottery lottery = lotteryDataFactory.getLottery(Integer.parseInt(lotteryId));
			if(lottery != null) {
				// 获取开奖时间
				List<OpenTime> openList = lotteryOpenUtil.getOpenDateList(lottery.getId(), date);
				if(openList != null) {
					// 遍历获取开奖期号
					String[] expects = new String[openList.size()];
					for (int i = 0, j = openList.size(); i < j; i++) {
						expects[i] = openList.get(i).getExpect();
					}
					// 转换成Map<期号, 开奖状态>，方便下面循环取出
					Map<String, LotteryOpenCode> openCodeMap = new HashMap<>();
					if(expects != null && expects.length > 0) {
						// 查询期号的开奖状态
						List<LotteryOpenCode> lotteryOpenCodeList = lotteryOpenCodeDao.list(lottery.getShortName(), expects);
						for (LotteryOpenCode tmpCodeBean : lotteryOpenCodeList) {
							openCodeMap.put(tmpCodeBean.getExpect(), tmpCodeBean);
						}
					}
					// 遍历开奖时间，取出对应开奖状态数据
					for (OpenTime openTime : openList) {
						LotteryOpenStatusVO tmpBean = new LotteryOpenStatusVO();
						tmpBean.setLottery(lottery);
						tmpBean.setOpenTime(openTime);
						
						if(openCodeMap.containsKey(openTime.getExpect())) {
							//填充数据库已开奖数据
							tmpBean.setOpenCode(openCodeMap.get(openTime.getExpect()));
						}else{
							//填充未开奖，指定开奖号码数据
							String key = String.format(ADMIN_OPEN_CODE_KEY, lottery.getShortName());
							boolean iskey = jedisTemplate.hexists(key, openTime.getExpect());
							if(iskey){
								String code = jedisTemplate.hget(key, openTime.getExpect());
								LotteryOpenCode bean = new LotteryOpenCode();
						        bean.setCode(code);
						        bean.setOpenStatus(Global.LOTTERY_OPEN_CODE_STATUS_NOT_OPEN);
						        bean.setLottery(lottery.getShortName());
						        bean.setTime(new Moment().toSimpleTime());
						        bean.setInterfaceTime(new Moment().toSimpleTime());
								tmpBean.setOpenCode(bean);
							}
						}
						list.add(tmpBean);
					}
				}
			}
		}
		return list;
	}
	
	@Override
	public boolean doManualControl(String lottery, String expect) {
		LotteryOpenCode entity = lotteryOpenCodeDao.get(lottery, expect);
		if(entity != null) {
			entity.setOpenStatus(0);
			entity.setOpenTime(null);
			return lotteryOpenCodeDao.update(entity);
		}
		return false;
	}
}