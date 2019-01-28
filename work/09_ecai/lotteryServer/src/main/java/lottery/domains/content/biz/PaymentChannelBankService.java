package lottery.domains.content.biz;

import lottery.domains.content.entity.PaymentChannelBank;

import java.util.List;

/**
 * Created by Nick on 2017-06-02.
 */
public interface PaymentChannelBankService {
    List<PaymentChannelBank> listAll(int status);
}
