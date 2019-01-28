package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.PaymentBankService;
import lottery.domains.content.dao.PaymentBankDao;
import lottery.domains.content.entity.PaymentBank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Nick on 2017-06-02.
 */
@Service
public class PaymentBankServiceImpl implements PaymentBankService {
    @Autowired
    private PaymentBankDao paymentBankDao;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentBank> listAll() {
        return paymentBankDao.listAll();
    }
}
