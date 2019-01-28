package lottery.domains.content.api.sb;

import com.alibaba.fastjson.JSON;
import javautils.http.HttpClientUtil;
import javautils.http.ToUrlParamUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Nick on 2017-05-27.
 */
@Component
public class Win88SBAPI implements InitializingBean{
    private static final Logger log = LoggerFactory.getLogger(Win88SBAPI.class);

    @Value("${sb.opcode}")
    private String opCode;
    @Value("${sb.md5key}")
    private String md5key;
    @Value("${sb.apiurl}")
    private String apiUrl;

    private static final String SPORTBETLOG_URL = "api/GetSportBetLog";
    private static final String HELLO_URL = "api/Hello";

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isNotEmpty(apiUrl)) {
            if (!apiUrl.endsWith("/")) {
                apiUrl = apiUrl + "/";
            }
        }
    }

    public static String transSportType(String sportType) {
        if (StringUtils.isEmpty(sportType)) {
            // 有时候沙巴的这个字段并不会返回，所以这里返回默认
            return "足球";
        }
        switch (sportType) {
            case Win88SBSportType.SOCCER: return "足球";
            case Win88SBSportType.BASKET_BALL: return "篮球";
            case Win88SBSportType.FOOTBOOL: return "足球";
            case Win88SBSportType.ICE_HOCKEY_BALL: return "冰上曲棍球";
            case Win88SBSportType.TENNIS: return "网球";
            case Win88SBSportType.VOLLEY_BALL: return "排球";
            case Win88SBSportType.BILLIARDS: return "台球";
            case Win88SBSportType.BASE_BALL: return "棒球";
            case Win88SBSportType.BADMINTON: return "羽毛球";
            case Win88SBSportType.GOLF: return "高尔夫";
            case Win88SBSportType.RACING: return "赛车";
            case Win88SBSportType.SWIMMING: return "游泳";
            case Win88SBSportType.POLITICS: return "政治";
            case Win88SBSportType.WATER_POLO: return "水球";
            case Win88SBSportType.DIVING: return "跳水";
            case Win88SBSportType.BOXING: return "拳击";
            case Win88SBSportType.ARCHERY: return "射箭";
            case Win88SBSportType.TABLE_TENNIS: return "乒乓球";
            case Win88SBSportType.WEIGHTLIFTING: return "举重";
            case Win88SBSportType.CANOEING: return "皮划艇";
            case Win88SBSportType.GYMNASTICS: return "体操";
            case Win88SBSportType.ATHLETICS: return "田径";
            case Win88SBSportType.EQUESTRIANISM: return "马术";
            case Win88SBSportType.HAND_BALL: return "手球";
            case Win88SBSportType.DART: return "飞镖";
            case Win88SBSportType.RUGBY: return "橄榄球";
            case Win88SBSportType.CRICKET: return "板球";
            case Win88SBSportType.HOCKEY_BALL: return "曲棍球";
            case Win88SBSportType.WINTER_SPORTS: return "冬季运动";
            case Win88SBSportType.RACKET_BALL: return "壁球";
            case Win88SBSportType.ENTERTAINMENT: return "娱乐";
            case Win88SBSportType.NEAR_NET_BALL: return "网前球";
            case Win88SBSportType.CYCLE_RACING: return "骑自行车";
            case Win88SBSportType.TRIATHLON: return "铁人三项";
            case Win88SBSportType.WRESTLING: return "摔跤";
            case Win88SBSportType.E_SPORTS: return "电子竞技";
            case Win88SBSportType.MUAY_THAI: return "泰拳";
            case Win88SBSportType.CRICKET_GAME: return "板球游戏";
            case Win88SBSportType.OTHER: return "其他运动";
            case Win88SBSportType.MIX_PARLAY: return "混合足球";
            case Win88SBSportType.HORSE_RACING: return "赛马";
            case Win88SBSportType.DOG_RACING: return "灰狗";
            case Win88SBSportType.HARNESS: return "马具";
            case Win88SBSportType.HORSE_RACING_FIXED: return "赛马固定赔率";
            case Win88SBSportType.NUMBER_GAME: return "数字游戏";
            case Win88SBSportType.VIRTUAL_FOOTBALL: return "虚拟足球";
            case Win88SBSportType.VIRTUAL_HORSE_RACING: return "虚拟赛马";
            case Win88SBSportType.VIRTUAL_LION_RACING: return "虚拟灵狮";
            case Win88SBSportType.VIRTUAL_RACING_TRACK: return "虚拟赛道";
            case Win88SBSportType.VIRTUAL_F1: return "虚拟F1";
            case Win88SBSportType.VIRTUAL_CYCLE_RACING: return "虚拟自行车";
            case Win88SBSportType.VIRTUAL_TENNIS: return "虚拟网球";
            case Win88SBSportType.VIRTUAL_KENO: return "基诺";
            case Win88SBSportType.CASINO: return "赌场";
            case Win88SBSportType.RNG: return "RNG游戏";
            case Win88SBSportType.MINI_GAME: return "迷你游戏";
            case Win88SBSportType.MOBILE: return "移动";
            default:
                return "未知";
        }
    }

    public static int transTicketStatus(String ticketStatus) {
        if (Win88SBTicketStatus.WAITING.equalsIgnoreCase(ticketStatus)) return 2;
        if (Win88SBTicketStatus.RUNNING.equalsIgnoreCase(ticketStatus)) return 3;
        if (Win88SBTicketStatus.WON.equalsIgnoreCase(ticketStatus)) return 4;
        if (Win88SBTicketStatus.LOSE.equalsIgnoreCase(ticketStatus)) return 5;
        if (Win88SBTicketStatus.DRAW.equalsIgnoreCase(ticketStatus)) return 6;
        if (Win88SBTicketStatus.REJECT.equalsIgnoreCase(ticketStatus)) return 7;
        if (Win88SBTicketStatus.REFUND.equalsIgnoreCase(ticketStatus)) return 8;
        if (Win88SBTicketStatus.VOID.equalsIgnoreCase(ticketStatus)) return 9;
        if (Win88SBTicketStatus.HALF_WON.equalsIgnoreCase(ticketStatus)) return 10;
        if (Win88SBTicketStatus.HALF_LOSE.equalsIgnoreCase(ticketStatus)) return 11;

        return -1;
    }

    public boolean hello() {
        try {
            String url = new StringBuffer(apiUrl).append(HELLO_URL).append("?OpCode=").append(opCode).toString();

            String json = HttpClientUtil.get(url, null, 5000);

            if (StringUtils.isEmpty(json)) {
                log.error("访问SB接口时返回空，访问地址：" + url);
                return false;
            }

            Win88SBCommonResult result = JSON.parseObject(json, Win88SBCommonResult.class);
            if (result == null) {
                log.error("连接SB发生解析错误，请求地址："+url+",返回：" + json);
                return false;
            }
            if (!"0".equals(result.getErrorCode())) {
                log.error("连接SB发生错误" + ",返回：" + json);
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("连接SB发生错误", e);
            return false;
        }
    }

    public Win88SBSportBetLogResult sportBetLog(String lastVersionKey) throws Exception {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("OpCode", opCode);
        params.put("LastVersionKey", lastVersionKey);
        params.put("lang", "cs");

        Object postResult = post(SPORTBETLOG_URL, params, Win88SBSportBetLogResult.class);

        Win88SBSportBetLogResult result = (Win88SBSportBetLogResult) postResult;
        if (!"0".equals(result.getErrorCode())) {
            // 23000表示空
            if ("23000".equals(result.getErrorCode())) {
                log.error("获取沙巴投注记录时返回错误码表示现在没有记录,访问参数：" + JSON.toJSONString(params) + ",返回：" + JSON.toJSONString(result));
                return null;
            }

            String errorCode = result.getErrorCode();
            if (StringUtils.isEmpty(errorCode)) {
                log.error("获取沙巴投注记录时返回错误码未知,访问参数：" + JSON.toJSONString(params) + ",返回：" + JSON.toJSONString(result));
                throw new RuntimeException("获取沙巴投注记录时返回错误码未知,访问参数：" + JSON.toJSONString(params));
            }
            else {
                log.error("获取沙巴投注记录时返回错误码" + errorCode + "," + result.getMessage() + ",访问参数：" + JSON.toJSONString(params) + ",返回：" + JSON.toJSONString(result));
                throw new RuntimeException("获取沙巴投注记录时返回错误码" + errorCode + "," + result.getMessage() + ",访问参数：" + JSON.toJSONString(params) + ",返回：" + JSON.toJSONString(result));
            }
        }

        return result;
    }

    /**
     * post只是将返回的结果转换并返回，如果发生网络错误，返回空
     */
    private Object post(String subUrl, Map<String, String> params, Class<? extends Win88SBResult> resultClass) throws Exception {
        String paramsStr = ToUrlParamUtils.toUrlParam(params);

        String securityToken = new StringBuffer(md5key).append("/").append(subUrl).append("?").append(paramsStr).toString();
        securityToken = DigestUtils.md5Hex(securityToken).toUpperCase();

        String paramsEncode =  new StringBuffer("SecurityToken=").append(securityToken).append("&").append(paramsStr).toString();

        String url = new StringBuffer(apiUrl).append(subUrl).append("?").append(paramsEncode).toString();

        log.debug("开始请求沙巴：{}", url);

        String json = HttpClientUtil.get(url, null, 30000); // 30秒超时

        if (StringUtils.isEmpty(json)) {
            log.error("连接沙巴返回记录时返回空，访问地址：" + url);
            throw new RuntimeException("连接沙巴返回记录时返回空，访问地址：" + url);
        }

        Win88SBResult result;
        try {
            result = JSON.parseObject(json, resultClass);
        } catch (Exception e) {
            log.error("解析沙巴返回信息错误，请求地址："+url+",请求参数：" + JSON.toJSONString(params) + ",返回：" + json);
            throw e;
        }
        if (result == null) {
            log.error("解析沙巴返回信息错误，请求地址："+url+",请求参数：" + JSON.toJSONString(params) + ",返回：" + json);
            throw new RuntimeException("解析沙巴返回信息错误，请求地址："+url+",请求参数：" + JSON.toJSONString(params) + ",返回：" + json);
        }

        return result;
    }
}
