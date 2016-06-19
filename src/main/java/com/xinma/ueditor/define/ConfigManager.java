package com.xinma.ueditor.define;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 配置管理器
 * 
 * @author Hoctor
 *
 */
public final class ConfigManager {

	private final String rootPath;
	private final String configFilePath;

	private JsonNode jsonConfig = null;
	// 涂鸦上传filename定义
	private final static String SCRAWL_FILE_NAME = "scrawl";
	// 远程图片抓取filename定义
	private final static String REMOTE_FILE_NAME = "remote";

	private ConfigManager(String rootPath, String configFilePath) throws IOException {

		rootPath = rootPath.replace("\\", "/");

		this.rootPath = rootPath;

		this.configFilePath = configFilePath;

		this.initEnv();

	}

	/**
	 * 配置管理器构造工厂
	 * 
	 * @param rootPath
	 *            服务器根路径
	 * @param configFilePath
	 *            服务器所在项目路径
	 * 
	 * @return 配置管理器实例或者null
	 * @throws IOException
	 *             IO异常
	 */
	public static ConfigManager getInstance(String rootPath, String configFilePath) throws IOException {

		return new ConfigManager(rootPath, configFilePath);
	}

	/**
	 * 验证配置文件加载是否正确
	 * 
	 * @return 参数配置不为null返回true；否则返回false
	 */
	public boolean valid() {
		return this.jsonConfig != null;
	}

	public JsonNode getAllConfig() {

		return this.jsonConfig;

	}

	public Map<String, Object> getConfig(int type) throws IOException {

		Map<String, Object> conf = new HashMap<String, Object>();
		String savePath = null;

		switch (type) {

		case ActionMap.UPLOAD_FILE:
			conf.put("isBase64", "false");
			conf.put("maxSize", this.jsonConfig.get("fileMaxSize").asLong());
			conf.put("allowFiles", this.getArray("fileAllowFiles"));
			conf.put("fieldName", this.jsonConfig.get("fileFieldName").asText());
			savePath = this.jsonConfig.get("filePathFormat").asText();
			break;

		case ActionMap.UPLOAD_IMAGE:
			conf.put("isBase64", "false");
			conf.put("maxSize", this.jsonConfig.get("imageMaxSize").asLong());
			conf.put("allowFiles", this.getArray("imageAllowFiles"));
			conf.put("fieldName", this.jsonConfig.get("imageFieldName").asText());
			savePath = this.jsonConfig.get("imagePathFormat").asText();
			break;

		case ActionMap.UPLOAD_VIDEO:
			conf.put("maxSize", this.jsonConfig.get("videoMaxSize").asLong());
			conf.put("allowFiles", this.getArray("videoAllowFiles"));
			conf.put("fieldName", this.jsonConfig.get("videoFieldName").asText());
			savePath = this.jsonConfig.get("videoPathFormat").asText();
			break;

		case ActionMap.UPLOAD_SCRAWL:
			conf.put("filename", ConfigManager.SCRAWL_FILE_NAME);
			conf.put("maxSize", this.jsonConfig.get("scrawlMaxSize").asLong());
			conf.put("fieldName", this.jsonConfig.get("scrawlFieldName").asText());
			conf.put("isBase64", "true");
			savePath = this.jsonConfig.get("scrawlPathFormat").asText();
			break;

		case ActionMap.CATCH_IMAGE:
			conf.put("filename", ConfigManager.REMOTE_FILE_NAME);
			conf.put("filter", this.getArray("catcherLocalDomain"));
			conf.put("maxSize", this.jsonConfig.get("catcherMaxSize").asLong());
			conf.put("allowFiles", this.getArray("catcherAllowFiles"));
			conf.put("fieldName", this.jsonConfig.get("catcherFieldName").asText() + "[]");
			savePath = this.jsonConfig.get("catcherPathFormat").asText();
			break;

		case ActionMap.LIST_IMAGE:
			conf.put("allowFiles", this.getArray("imageManagerAllowFiles"));
			conf.put("dir", this.jsonConfig.get("imageManagerListPath").asText());
			conf.put("count", this.jsonConfig.get("imageManagerListSize").asInt());
			break;

		case ActionMap.LIST_FILE:
			conf.put("allowFiles", this.getArray("fileManagerAllowFiles"));
			conf.put("dir", this.jsonConfig.get("fileManagerListPath").asText());
			conf.put("count", this.jsonConfig.get("fileManagerListSize").asInt());
			break;

		}

		conf.put("savePath", savePath);
		conf.put("rootPath", this.rootPath);

		return conf;

	}

	private void initEnv() throws IOException {

		File file = new File(this.configFilePath);

		if (!file.isAbsolute()) {
			file = new File(file.getAbsolutePath());
		}

		String configContent = this.readFile(this.getConfigPath());
		jsonConfig = new ObjectMapper().readTree(configContent);

	}

	private String getConfigPath() {
		return this.configFilePath;
	}

	private String[] getArray(String key) throws IOException {
		return new ObjectMapper().readValue(jsonConfig.get(key).toString(), String[].class);
	}

	private String readFile(String path) throws IOException {

		StringBuilder builder = new StringBuilder();

		InputStreamReader reader = new InputStreamReader(new FileInputStream(path), "UTF-8");
		BufferedReader bfReader = new BufferedReader(reader);

		String tmpContent = null;

		while ((tmpContent = bfReader.readLine()) != null) {
			builder.append(tmpContent);
		}

		bfReader.close();

		return this.filter(builder.toString());
	}

	private String filter(String input) {

		return input.replaceAll("/\\*[\\s\\S]*?\\*/", "");

	}

}
