package lottery.domains.content.api.im;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Nick on 2017-05-24.
 */
@Component
public class IMAPI {
    private static final Logger log = LoggerFactory.getLogger(IMAPI.class);

    @Value("${im.api_account}")
    private String apiAccount;
    @Value("${im.key}")
    private String key;
    @Value("${im.url}")
    private String url;



    public String transSports(int id) {
        switch (id) {
            case 0 : return "Soccer";
            case 1 : return "Basketball";
            case 2 : return "Tennis";
            case 3 : return "Motor Racing";
            case 4 : return "Golf";
            case 5 : return "Soccer (HT)";
            case 6 : return "Football";
            case 7 : return "Hockey";
            case 8 : return "Baseball";
            case 9 : return "Volleyball";
            case 10: return " Badminton";
            case 11: return " Snooker";
            case 12: return " Boxing";
            case 13: return " Rugby";
            case 14: return " Cricket";
            case 16: return " Handball";
            case 17: return " FinancialBets";
            case 18: return " Futsal";
            case 19: return " Asian9Ball";
            case 20: return " Billiard";
            case 21: return " Darts";
            case 22: return " WaterPolo";
            case 23: return " Olympic";
            case 24: return " Cycling";
            case 25: return " Beach Volleyball";
            case 26: return " Field Hockey";
            case 27: return " Table Tennis18";
            case 28: return " Athletics";
            case 29: return " Archery";
            case 30: return " Weight Lifting";
            case 31: return " Canoeing";
            case 32: return " Gymnastics";
            case 33: return " Equestrian";
            case 34: return " Triathlon";
            case 35: return " Swimming";
            case 36: return " Fencing";
            case 37: return " Judo";
            case 38: return " M. Pentathlon";
            case 39: return " Rowing";
            case 40: return " Sailing";
            case 41: return " Shooting";
            case 42: return " Taekwondo";
            case 43: return " Virtual Soccer";
            default: return "未知";
        }
    }
}
