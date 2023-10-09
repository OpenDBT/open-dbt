package com.highgo.opendbt.experiment.terminal.utils.doc2html.config;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Config {
	private final static String CONFIG_PROPERTY_FILENAME = "application.properties";

	private static Properties properties;

	public static String DOC_BASE_DIR;
	public static String DOC_IMAGE_DIR;

	static {
		try {
			loadConfig();

			DOC_BASE_DIR = get("baseDir");
			DOC_IMAGE_DIR = DOC_BASE_DIR + "/image";

		} catch (IOException e) {
			throw new IllegalStateException("load message resource " + CONFIG_PROPERTY_FILENAME + " error", e);
		}
	}

	private static void loadConfig() throws IOException {
		if (properties != null) {
			return;
		}
		properties = new Properties();
		properties.load(getInputStream());
	}

	/**
	 * ������Դ�ļ�
	 *
	 * @return
	 */
	private static InputStream getInputStream() {

		// �ӵ�ǰ��������м�����Դ
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_PROPERTY_FILENAME);
		if (is != null) {
			return is;
		}

		// ��ϵͳ��������м�����Դ
		is = ClassLoader.getSystemResourceAsStream(CONFIG_PROPERTY_FILENAME);
		if (is != null) {
			return is;
		}
		throw new IllegalStateException("cannot find " + CONFIG_PROPERTY_FILENAME + " anywhere");
	}

	public static String get(String name) {
		return properties.getProperty(name);
	}

	public static String getDir(String folder) {
		StringBuilder dir = new StringBuilder();
		dir.append(Config.DOC_BASE_DIR).append("/").append(FormartUtil.getDateFolder()).append("/").append(folder);
		return dir.toString();
	}

	static class FormartUtil {
		private static final SimpleDateFormat dfm = new SimpleDateFormat("yyyy/M/d");

		protected static String getDateFolder() {
			return dfm.format(new Date());
		}
	}
}
