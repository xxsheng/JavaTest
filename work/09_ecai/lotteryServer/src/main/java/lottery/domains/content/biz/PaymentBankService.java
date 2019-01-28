package lottery.domains.content.biz;

import lottery.domains.content.entity.PaymentBank;

import java.util.List;

/**
 * Created by Nick on 2017-06-02.
 */
public interface PaymentBankService {
    List<PaymentBank> listAll();
}
