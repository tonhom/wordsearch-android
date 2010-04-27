package com.dahl.brendan.wordsearch;

public class Constants {
	public static final String API_URL_BASE = "http://wordsearchapp.brendandahl.com/app/";
	public static final String API_URL_CRASH = API_URL_BASE + "crash";
	private static final String API_URL_SCORE = API_URL_BASE + "score";
	public static final String API_URL_SCORE_RANK = API_URL_SCORE + "/rank";
	public static final String API_URL_SCORE_SUBMIT = API_URL_SCORE + "/submit";
	public static final String API_URL_SCORE_TOP10 = API_URL_SCORE + "/top10";
	
	public static final String KEY_GLOBAL_RANK = "KEY_GLOBAL_RANK";
	public static final String KEY_GLOBAL_HIGH_SCORE = "KEY_GLOBAL_HIGH_SCORE";
	public static final String KEY_HIGH_SCORE = "KEY_HIGH_SCORE";
	public static final String KEY_RANK = "KEY_RANK";
	public static final String KEY_WORD_COUNT = "KEY_WORDLIST_COUNT";
	public static final String KEY_HIGH_SCORE_TIME = "KEY_TIME";
	public static final String KEY_HIGH_SCORE_SIZE = "KEY_SIZE";
	public static final String KEY_HIGH_SCORE_THEME = "KEY_THEME";
	public static final String KEY_HIGH_SCORE_NAME = "KEY_NAME";
	public static final String KEY_PAYLOAD = "payload";
	public static final String KEY_DEVICE_ID = "KEY_DEVICE_ID";
	public static final String KEY_INTRO_VER = "intro_app_ver";

	// intent params
	public final static String APPLICATION_VERSION = "APPLICATION_VERSION";
	public final static String APPLICATION_PACKAGE = "APPLICATION_PACKAGE";
	public final static String APPLICATION_STACKTRACE = "APPLICATION_STACKTRACE";
	public final static String PHONE_MODEL = "PHONE_MODEL";
	public final static String ANDROID_VERSION = "ANDROID_VERSION";
	public final static String SECURITY_TOKEN = "SECURITY_TOKEN";
	public final static String ADDITIONAL_DATA = "ADDITIONAL_DATA";

	public static final String VALUE_SECRET = "wordsearchfreepw";
	
	public static final int GRID_SIZE_DEFAULT = 10;
	public static final int MAX_NAME_LENGTH = 30;
	public static final int MAX_TOP_SCORES = 10;
}
