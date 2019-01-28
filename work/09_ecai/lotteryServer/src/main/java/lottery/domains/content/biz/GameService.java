package lottery.domains.content.biz;

import lottery.domains.content.entity.Game;
import lottery.domains.content.entity.UserGameAccount;
import lottery.web.WebJSON;

import java.util.List;

/**
 * Created by Nick on 2017-05-03.
 */
public interface GameService {
    String getPTRedirectUrl(int userId);

    UserGameAccount decryptPTParam(WebJSON json, String ip, String base64EncryptParam);

    List<Game> listAll();

    Game getById(int id);
}
