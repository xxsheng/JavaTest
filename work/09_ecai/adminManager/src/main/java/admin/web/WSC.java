package admin.web;

public final class WSC {

	private WSC() {}

	// 项目部署的路径
	public static String PROJECT_PATH;

	// 静态文件对应地址
	public static final String WEB_STATICMEDIA_DIR = "staticmedia";

	// 用户的Session名称
	public static final String SESSION_UNLOCK_WITHDARWPWD = "SESSION_UNLOCK_WITHDARWPWD";
	public static final String SESSION_EXPIRE_TIME = "SESSION_EXPIRE_TIME";
	public static final String SESSION_USER_PROFILE = "SESSION_USER_PROFILE_SES";
	public static final String SESSION_GOOGLE_USER = "SESSION_GOOGLE_USER";
	public static final String COOKIE_USER_PTOKEN = "PTOKEN";
	public static final String COOKIE_USER_STOKEN = "STOKEN";
	public static final String COOKIE_USER_SAVEUSER = "SAVEUSER";
	public static final String DISPOSABLE_TOKEN = "DISPOSABLE_TOKEN";
	public static final int COOKIE_USER_TIMEOUT = 30*24*60*60;

}
