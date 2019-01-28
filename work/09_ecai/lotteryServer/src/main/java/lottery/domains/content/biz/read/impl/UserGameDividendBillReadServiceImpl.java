package lottery.domains.content.biz.read.impl;

import javautils.jdbc.PageList;
import lottery.domains.content.biz.read.UserGameDividendBillReadService;
import lottery.domains.content.dao.read.UserGameDividendBillReadDao;
import lottery.domains.content.entity.UserGameDividendBill;
import lottery.domains.content.vo.user.UserGameDividendBillVO;
import lottery.domains.pool.DataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 老虎机真人账单Service
 * Created by Nick on 2016/10/31.
 */
@Service
@Transactional(readOnly = true)
public class UserGameDividendBillReadServiceImpl implements UserGameDividendBillReadService {
    @Autowired
    private UserGameDividendBillReadDao uGameDividendBillReadDao;

    @Autowired
    private DataFactory dataFactory;


    @Override
    @Transactional(readOnly = true)
    public PageList searchByZhuGuans(List<Integer> userIds, String sTime, String eTime, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList = uGameDividendBillReadDao.searchByZhuGuans(userIds, sTime, eTime, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    @Override
    @Transactional(readOnly = true)
    public PageList searchByLowers(int upUserId, String sTime, String eTime, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList = uGameDividendBillReadDao.searchByLowers(upUserId, sTime, eTime, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    private PageList convertPageList(PageList pList) {
        List<UserGameDividendBillVO> convertList = new ArrayList<>();
        if(pList != null && pList.getList() != null){
            for (Object tmpBean : pList.getList()) {
                convertList.add(new UserGameDividendBillVO((UserGameDividendBill) tmpBean, dataFactory));
            }
        }
        pList.setList(convertList);
        return pList;
    }
}
