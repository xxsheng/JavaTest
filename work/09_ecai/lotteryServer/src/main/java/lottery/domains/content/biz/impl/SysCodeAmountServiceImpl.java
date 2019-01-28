package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.SysCodeAmountService;
import lottery.domains.content.dao.SysCodeAmountDao;
import lottery.domains.content.entity.SysCodeAmount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Nick on 2017-06-01.
 */
@Service
public class SysCodeAmountServiceImpl implements SysCodeAmountService {
    @Autowired
    private SysCodeAmountDao sysCodeAmountDao;

    @Override
    @Transactional(readOnly = true)
    public List<SysCodeAmount> listAll() {
        return sysCodeAmountDao.listAll();
    }
}
