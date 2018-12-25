package mybatis_generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;



public class Mybatis_Generator {

	private static Logger logger = Logger.getLogger(Mybatis_Generator.class);
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		List<String> warnings = new ArrayList<String>();
		boolean overwrite = true;
		
		File configFile = new File(Mybatis_Generator.class.getResource("generatorConfig.xml").toURI());
		logger.debug(configFile.getAbsolutePath());
		
		ConfigurationParser cp = new ConfigurationParser(warnings);
		org.mybatis.generator.config.Configuration config = cp.parseConfiguration(configFile);
		DefaultShellCallback callback = new DefaultShellCallback(overwrite);
	    MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
	            callback, warnings);
	    myBatisGenerator.generate(null);
	}

}
