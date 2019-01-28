package lottery.domains.content.biz;

import java.util.Map;

/**
 * Created by Nick on 2017/2/26.
 */
public interface UserBetsNoticeService {
    /**
     * 获取所有投注通知，key:用户ID；value:域：用户通知信息
     */
    Map<Integer, Map<String, String>> getAllBetsNotices();

    /**
     * 删除一个投注通知
     */
    void delBetsNotice(String field);
}
