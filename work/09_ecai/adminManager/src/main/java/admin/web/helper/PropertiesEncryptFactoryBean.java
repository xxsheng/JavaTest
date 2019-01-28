package admin.web.helper;

import java.util.Properties;

import org.springframework.beans.factory.FactoryBean;

import javautils.encrypt.DESUtil;

public class PropertiesEncryptFactoryBean implements FactoryBean<Properties> {

	private Properties properties;

	@Override
	public Properties getObject() throws Exception {
		return getProperties();
	}

	@Override
	public Class<?> getObjectType() {
		return Properties.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
		try {
			String username = properties.getProperty("user");
			String password = properties.getProperty("password");
			String key = properties.getProperty("key");
			String decryptUsername = DESUtil.getInstance().decryptStr(username, key);
			String decryptPassword = DESUtil.getInstance().decryptStr(password, key);
			properties.put("user", decryptUsername);
			properties.put("password", decryptPassword);
		} catch (Exception e) {}
	}
	
}