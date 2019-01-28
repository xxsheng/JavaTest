package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.PaymentChannelBankService;
import lottery.domains.content.dao.PaymentChannelBankDao;
import lottery.domains.content.entity.PaymentChannelBank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Nick on 2017-06-02.
 */
@Service
public class PaymentChannelBankServiceImpl implements PaymentChannelBankService{
    @Autowired
    private PaymentChannelBankDao paymentChannelBankDao;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentChannelBank> listAll(int status) {
        return paymentChannelBankDao.listAll(status);
    }
}
