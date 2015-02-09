package it.unozerouno.givemetime.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


/**
 * This class manages the Shared Preferences and cached Auth Tokens for GiveMeTime Application.
 * Provides access to preferences, user data and auth token via getters/setters. 
 * @author Edoardo Giacomello
 *
 */
public final class UserKeyRing {
	//General
	private static SharedPreferences prefs;
	private static SharedPreferences.Editor editor;
	private static final String SHARED_PREF_GENERAL = "givemetime_preferences";
	
	//Login and tokens
	private static final String firstTimeLoginPref = "first_time_login";		
	private static final String userEmailPref = "user_email";
	private static final String userTokenPref = "token_user";
	private static final String userNamePref = "user_name";
	private static final String userSurnamePref = "user_surname";
	private static final String apiKeyPref ="api_key";
	
	//Calendar Selection preferences
	private static final String selectedCalendarId = "calendar_selected_id";
	private static final String selectedCalendarName = "calendar_selected_name";
	
	//Service Related
	private static final String serviceLocationUpdateFrequency = "service_location_update_frequency";
	private static final String serviceLocationMaxAge = "service_location_max_age";
	private static final String serviceLocationMaxDist = "service_location_max_dist";
	
	
	@SuppressLint("CommitPrefEdits") 
	private static void setSharedPreferences(Context context){
		prefs = context.getSharedPreferences(SHARED_PREF_GENERAL, Context.MODE_PRIVATE);
		editor = prefs.edit();
	}
	
	/**
	 * This method will wipe all shared preferences for the application
	 * @param context
	 */
	public static void resetSharedPreferences(Context context){
		setSharedPreferences(context);
		editor.clear();
		editor.commit();
	}
	
	
	public static void setUserEmail(Context context, String userEmail) {
		setSharedPreferences(context);
		editor.putString(userEmailPref, userEmail);
		editor.commit();
	}
	public static void setUserName(Context context, String userName) {
		setSharedPreferences(context);
		editor.putString(userNamePref, userName);
		editor.commit();
	}
	public static void setUserSurname(Context context, String userSurname) {
		setSharedPreferences(context);
		editor.putString(userSurnamePref, userSurname);
		editor.commit();
	}
	
	public static void setToken(Context context, String token) {
		setSharedPreferences(context);
		editor.putString(userTokenPref, token);
		editor.commit();
	}
	
	public static void setFirstLogin(Context context, Boolean value) {
		setSharedPreferences(context);
		editor.putBoolean(firstTimeLoginPref, value);
		editor.commit();
	}
	
	public static void setCalendarId(Context context, String calendarID){
		setSharedPreferences(context);
		editor.putString(selectedCalendarId, calendarID);
		editor.commit();
	}
	public static void setCalendarName(Context context, String calendarName){
		setSharedPreferences(context);
		editor.putString(selectedCalendarName, calendarName);
		editor.commit();
	}
	
	public static void setApiKey(Context context, String apiKey){
		setSharedPreferences(context);
		editor.putString(apiKeyPref,apiKey);
		editor.commit();
	}
	
	public static void setLocationUpdateFrequency(Context context, int updateFrequency) {
		setSharedPreferences(context);
		editor.putInt(serviceLocationUpdateFrequency, updateFrequency);
		editor.commit();
	}
	
	public static void setLocationMaxAge(Context context, int maximumLocationAge) {
		setSharedPreferences(context);
		editor.putInt(serviceLocationMaxAge, maximumLocationAge);
		editor.commit();
	}
	public static void setLocationMaxDistTolerance(Context context, float maximumLocationDist) {
		setSharedPreferences(context);
		editor.putFloat(serviceLocationMaxDist, maximumLocationDist);
		editor.commit();
	}
	
	
	
	
	
	
	
	
	public static String getUserEmail(Context context) {
		setSharedPreferences(context);
		return prefs.getString(userEmailPref, null);
		}
	public static String getUserName(Context context) {
		setSharedPreferences(context);
		return prefs.getString(userNamePref, "");
	}
	public static String getUserSurname(Context context) {
		setSharedPreferences(context);
		return prefs.getString(userSurnamePref, "");
	}
	public static String getToken(Context context) {
		setSharedPreferences(context);
		return prefs.getString(userTokenPref, null);
	}
	
	public static boolean isFirstTimeLogin(Context context){
		setSharedPreferences(context);
		return prefs.getBoolean(firstTimeLoginPref, true);
	}
	
	public static String getCalendarId(Context context) {
		setSharedPreferences(context);
		return prefs.getString(selectedCalendarId, null);
	}
	public static String getCalendarName(Context context) {
		setSharedPreferences(context);
		return prefs.getString(selectedCalendarName, "");
	}
	
	public static String getApiKey(Context context){
		setSharedPreferences(context);
		return prefs.getString(apiKeyPref, "");
	}
	
	public static int getLocationUpdateFrequency(Context context) {
		setSharedPreferences(context);
		return prefs.getInt(serviceLocationUpdateFrequency, 2);
	}
	
	public static int getLocationMaxAge(Context context) {
		setSharedPreferences(context);
		return prefs.getInt(serviceLocationMaxAge, 20);
	}
	public static float getLocationMaxDistTolerance(Context context) {
		setSharedPreferences(context);
		return prefs.getFloat(serviceLocationMaxDist, 1000f);
	}
	
	/**
	 * Checks whether all crucial user variables are set properly.  
	 * @return False if at least one is missing
	 */
	public static boolean checkVariables(Context context){
		if (getUserEmail(context) == null){
			return false;
		}
		if (getCalendarId(context) == null){
			return false;
		}
		
		return true;
	}

	

	
}
