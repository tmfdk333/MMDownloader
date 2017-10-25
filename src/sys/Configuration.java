package sys;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Properties;

import common.ErrorHandling;

/**
 * 환경설정을 담당하는 클래스
 * @author occidere
 *
 */
public class Configuration {
	private Configuration() {}
	
	//환경설정 파일 위치: Marumaru/MMDownloader.properties로 고정
	private static String CONF_PATH = SystemInfo.DEFAULT_PATH + SystemInfo.fileSeparator + "MMDownloader.properties";
	
	private static File profile = new File(CONF_PATH);
	private static FileInputStream fis = null;
	private static FileOutputStream fos = null;
	private static Properties prop = new Properties();
	
	/** 
	 * 설정파일 initialize 메서드
	 */
	public static void init() {
		/* 시작과 동시에 설정파일(MMDownloader.properties) 읽어들여 적용 */
		try { loadProperty(); }
		catch(Exception e) { 
			ErrorHandling.saveErrLog("설정파일 읽기 실패", "", e);
		}
		
		/***** 이 사이엔 알맞은 시스템 변수들을 설정하는 내용이 들어가야 됨 *****/
		
		if(prop.containsKey("PATH")==false) prop.setProperty("PATH", SystemInfo.DEFAULT_PATH);
		if(prop.containsKey("MERGE")==false) prop.setProperty("MERGE", "false");
		if(prop.containsKey("DEBUG")==false) prop.setProperty("DEBUG", "false");
		
		/************************************************************************/
		
		/* property 새로고침(store -> load -> apply) */
		try { refresh(); }
		catch(Exception e) {
			ErrorHandling.saveErrLog("설정파일 새로고침 실패", "", e);
		}
	}
	
	/**
	 * Property를 load하는 메서드
	 * @throws Exception
	 */
	public static void loadProperty() throws Exception {
		if(profile.exists() == false) {
			profile = new File(CONF_PATH);
			profile.createNewFile();
		}
		if(prop == null) prop = new Properties();
		
		fis = new FileInputStream(profile);
		prop.load(new BufferedInputStream(fis));
		fis.close();
	}

	/**
	 * Property를 저장하는 메서드
	 * @throws Exception
	 */
	public static void storeProperty() throws Exception {
		if(profile.exists() == false) {
			profile = new File(CONF_PATH);
			profile.createNewFile();
		}
		if(prop == null) prop = new Properties();
		
		fos = new FileOutputStream(profile);
		prop.store(new BufferedOutputStream(fos), "");
		fos.close();
	}
	
	/**
	 * 불러온 Property를 적용하는 메서드(reflect 이용)
	 */
	public static void applyProperty() {
		Enumeration<?> propNames = prop.propertyNames();
		String propName = null, fieldName = null;
		
		Field field[] = SystemInfo.class.getDeclaredFields();
		while(propNames.hasMoreElements()) {
			propName = (String)propNames.nextElement();
			for(Field each : field) {
				fieldName = each.getName();
				if(fieldName.equals(propName)) {
					/* 변수명에 맞는 이름을 찾아서 설정 적용 */
					try { each.set(fieldName, prop.getProperty(propName)); }
					catch(Exception e) {
						ErrorHandling.saveErrLog("설정값 적용 실패: "+propName, "", e);
					}
				}
			}
		}
	}
	
	/**
	 * Store-> Load -> Apply를 순차적으로 실행
	 * @throws Exception
	 */
	public static void refresh() throws Exception {
		storeProperty();
		loadProperty();
		applyProperty();
	}
	
	public static void setProperty(String key, String value) {
		prop.setProperty(key, value);
	}
	
	public static short getShort(String name, short def) {
		String tmp = prop.getProperty(name);
		return tmp == null ? def : Short.parseShort(tmp);
	}
	
	public static short getByte(String name, byte def) {
		String tmp = prop.getProperty(name);
		return tmp == null ? def : Byte.parseByte(tmp);
	}
	
	public static int getInt(String name, int def) {
		String tmp = prop.getProperty(name);
		return tmp == null ? def : Integer.parseInt(tmp);
	}

	public static float getFloat(String name, float def) {
		String tmp = prop.getProperty(name);
		return tmp == null ? def : Float.parseFloat(tmp);
	}
	
	public static double getDouble(String name, double def) {
		String tmp = prop.getProperty(name);
		return tmp == null ? def : Double.parseDouble(tmp);
	}
	
	public static String getString(String name, String def) {
		String tmp = prop.getProperty(name);
		return tmp == null ? def : tmp;
	}
	
	public static boolean getBoolean(String name, boolean def) {
		String tmp = prop.getProperty(name);
		return tmp == null ? def : Boolean.parseBoolean(tmp);
	}
	
	public static boolean exist() {
		return profile.exists();
	}
}
