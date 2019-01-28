package lottery.domains.content.biz;

import activity.domains.content.vo.activity.ActivityWheelVO;
import lottery.web.WebJSON;

/**
 * Created by Nick on 2017/11/27.
 */
public interface ActivityRebateWheelService {
    ActivityWheelVO getTodayData(int userId);

    Double draw(WebJSON json, int userId, String ip);
}
