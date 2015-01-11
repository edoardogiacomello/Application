package it.unozerouno.givemetime.controller.fetcher;

import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.utils.GiveMeLogger;
import it.unozerouno.givemetime.utils.TaskListener;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract.Contacts.Data;

/**
 * This is the entry Point for the model. It fetches all stored app data from DB and generates Model.
 * It also keeps the internal GiveMeTime db and the CalendarProvider synchronized by fetching updates from Google calendar and updating the internal db.
 * @author Edoardo Giacomello
 * @author Paolo Bassi
 *
 */
public final class DatabaseManager {
	
	private static SQLiteDatabase database = null;
	private static DatabaseCreator dbCreator;
	private static DatabaseManager dbManagerInstance;
	


	private DatabaseManager(Context context) {
		if (database == null || dbCreator == null){
			dbCreator=DatabaseCreator.createHelper(context);
			database = dbCreator.getReadableDatabase();
		}
	}
	
	public static synchronized DatabaseManager getInstance(Context context) {
		if(dbManagerInstance == null){
			dbManagerInstance = new DatabaseManager(context);
		}
		return dbManagerInstance;
	}
	
		
	/**
	 * close all instances of DB and DBHelper
	 */
	
	public static void closeDB(){
		if(database != null){
			database.close();
		}
		if(dbCreator != null){
			dbCreator.close();
		}
	}
	
	public static class Results{
		public static String[] RESULT_OK = {"OK"};
		public static String[] RESULT_ERROR = {"ERROR"};
	}
	
	
	
		
	
	/**
	 * Pulls all new events from Google Calendar
	 */
	public static synchronized boolean synchronize(Activity caller){
		
		//Fetching Events ID from CalendarProvider
		final CalendarFetcher calendarFetcher = new CalendarFetcher(caller);
		calendarFetcher.setAction(CalendarFetcher.Actions.LIST_EVENTS_ID_ONLY);
		calendarFetcher.setListener(new TaskListener<String[]>(caller) {
			@Override
			public void onTaskResult(String[]... results) {
				for (String[] strings : results) {
					for (String eventId : strings) {
						System.out.println("Created event with id: " + eventId);
						DatabaseManager.getInstance(calendarFetcher.getCaller()).createEventRow( calendarFetcher.getCaller(), eventId);
					}
				}
				
			}
		});
		calendarFetcher.execute();
		
		
		//TODO: create events in db using eventId		
		
		
		return true;
		//TODO: synchronization
	}
	
	
	public void createEventRow(Context context, String eventId){
		String calId = UserKeyRing.getCalendarId(context);
		String CREATE_NEW_EMPTY_EVENT = "IF NOT EXISTS (SELECT * FROM "+ DatabaseCreator.EVENT_MODEL +" WHERE "+ DatabaseCreator.ID_CALENDAR + " = '"+ calId + "' AND "+ DatabaseCreator.ID_EVENT_PROVIDER +" = '"+ eventId +"') " +"INSERT INTO "+ DatabaseCreator.EVENT_MODEL + " (" + DatabaseCreator.ID_CALENDAR + ", " + DatabaseCreator.ID_EVENT_PROVIDER + ") VALUES ('"+ calId +"','"+ eventId +"'); ";
		database.execSQL(CREATE_NEW_EMPTY_EVENT);
		
	}
	
	/**
	 * This helper class creates the GiveMeTime database
	 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
	 * 		   Paolo Bassi
	 *
	 */
	private static class DatabaseCreator extends SQLiteOpenHelper{
		
		// Database Name
		private static final String DATABASE_NAME = "givemetime.db";
		// Database Version
		private static final int DATABASE_VERSION = 1;
		// Database Tables
		 static final String EVENT_MODEL = "event_model";
		 static final String PLACE_MODEL = "place_model";
		 static final String QUESTION_MODEL = "question_model";
		 static final String OPENING_TIMES = "opening_times";
		 static final String OPENING_DAYS = "opening_days";
		 static final String EVENT_CATEGORY = "event_category";
		 static final String EVENT_CONSTRAINTS = "event_constraints";
		 static final String CONSTRAINTS = "constraints";
		 static final String USER_PREFERENCE = "user_preference";
		 static final String VACATION_DAYS = "vacation_days";
		 static final String WORK_TIMETABLE = "work_timetable";
		
		// Database Column Names
		// EVENT_MODEL
		private static final String ID_CALENDAR = "id_calendar";
		private static final String ID_EVENT_PROVIDER = "id_event_provider";
		private static final String ID_EVENT_CATEGORY = "id_event_category";
		private static final String ID_PLACE = "id_place";
		private static final String FLAG_DO_NOT_DISTURB = "do_not_disturb_flag";
		private static final String FLAG_DEADLINE = "flag_deadline";
		private static final String FLAG_MOVABLE = "flag_movable";
		// PLACE_MODEL
		private static final String ID_LOCATION = "id_location";
		private static final String PM_NAME = "name";
		// QUESTION_MODEL
		private static final String ID_QUESTION = "id_question";
		private static final String DATE_TIME = "date_time";
		private static final String TYPE_QUESTION = "type_question";
		private static final String EVENT_ID = "event_id";
		private static final String USER_LOCATION = "user_location";
		// OPENING TIMES
		private static final String OT_ID_LOCATION = "ot_id_location";
		private static final String OT_ID_CONSTRAINT = "ot_id_constraint";
		// OPENING DAYS
		private static final String OD_ID_LOCATION = "od_id_location";
		private static final String OD_ID_CONSTRAINT = "od_id_constraint";
		// EVENT CATEGORY
		private static final String ECA_NAME = "eca_name";
		private static final String DEFAULT_DONOTDISTURB = "default_donotdisturb";
		// EVENT CONSTRAINT
		private static final String ECO_ID_CONSTRAINT = "eco_id_constraint";
		private static final String ECO_ID_EVENT = "eco_id_event";
		// CONSTRAINTS
		private static final String C_ID_CONSTRAINT = "c_id_constraint";
		private static final String CONSTRAINT_TYPE = "constraint_type";
		private static final String C_START = "c_start";
		private static final String C_END = "c_end";
		// USER_PREFERENCE
		private static final String ACCOUNT = "account";
		private static final String HOME_LOCATION = "home_location";
		private static final String ID_SLEEP_TIME = "id_sleep_time";
		// VACATION DAYS
		private static final String VD_ACCOUNT = "vd_account";
		private static final String VD_ID_CONSTRAINT = "vd_id_constraint";
		// WORK TIMETABLE
		private static final String WT_ACCOUNT = "wt_account";
		private static final String WT_ID_CONSTRAINT = "wt_id_constraint";
		
		// Table Create Statements
		// EVENT_MODEL
		private static final String CREATE_TABLE_EVENT_MODEL = "CREATE TABLE "
				+ EVENT_MODEL + "(" + ID_CALENDAR + " VARCHAR(5), "
				+ ID_EVENT_PROVIDER + " VARCHAR(5), "
				+ ID_EVENT_CATEGORY + " VARCHAR(30), "
				+ ID_PLACE + " VARCHAR(50), "
				+ FLAG_DO_NOT_DISTURB + " BOOLEAN, "
				+ FLAG_DEADLINE + " BOOLEAN, "
				+ FLAG_MOVABLE + " BOOLEAN, "
				+ " PRIMARY KEY (" + ID_CALENDAR + ", " + ID_EVENT_PROVIDER + "),"
				+ " FOREIGN KEY (" + ID_EVENT_CATEGORY + ") REFERENCES " + EVENT_CATEGORY + " (" + ECA_NAME + ")" 
				+ " FOREIGN KEY (" + ID_PLACE + ") REFERENCES " + PLACE_MODEL + " (" + ID_LOCATION + ")" + ");";
		// PLACE_MODEL
		private static final String CREATE_TABLE_PLACE_MODEL = "CREATE TABLE "
				+ PLACE_MODEL + "(" + ID_LOCATION + " VARCHAR(5) PRIMARY KEY, "
				+ PM_NAME + " VARCHAR(50), "
				+ " FOREIGN KEY (" + ID_LOCATION + ") REFERENCES " + OPENING_TIMES + " (" + OT_ID_LOCATION + ")" + ");";
		// QUESTION_MODEL
		private static final String CREATE_TABLE_QUESTION_MODEL = "CREATE TABLE "
				+ QUESTION_MODEL + "(" + ID_QUESTION + " VARCHAR(5) PRIMARY KEY, "
				+ DATE_TIME + " DATE, "
				+ TYPE_QUESTION + " VARCHAR(30), "
				+ EVENT_ID + " VARCHAR(5), "
				+ USER_LOCATION + " VARCHAR(5), "
				+ " FOREIGN KEY (" + EVENT_ID + ") REFERENCES " + EVENT_MODEL + " (" + ID_EVENT_PROVIDER + ")"
				+ " FOREIGN KEY (" + USER_LOCATION + ") REFERENCES " + PLACE_MODEL + " (" + ID_LOCATION	 + ")" + ");";
		// OPENING_TIMES 
		private static final String CREATE_TABLE_OPENING_TIMES = "CREATE TABLE "
				+ OPENING_TIMES + "(" + OT_ID_LOCATION + " VARCHAR(5), "
				+ OT_ID_CONSTRAINT + " VARCHAR(5), "
				+ " PRIMARY KEY (" + OT_ID_LOCATION + ", " + OT_ID_CONSTRAINT + "),"
				+ " FOREIGN KEY (" + OT_ID_LOCATION + ") REFERENCES " + PLACE_MODEL + " (" + ID_LOCATION + ")"
				+ " FOREIGN KEY (" + OT_ID_CONSTRAINT + ") REFERENCES " + CONSTRAINTS + " (" + C_ID_CONSTRAINT + ")" + ");";
		// OPENING_DAYS 
		private static final String CREATE_TABLE_OPENING_DAYS = "CREATE TABLE "
				+ OPENING_DAYS + "(" + OD_ID_LOCATION + " VARCHAR(5), "
				+ OD_ID_CONSTRAINT + " VARCHAR(5), "
				+ " PRIMARY KEY (" + OD_ID_LOCATION + ", " + OD_ID_CONSTRAINT + "),"
				+ " FOREIGN KEY (" + OD_ID_LOCATION + ") REFERENCES " + PLACE_MODEL + " (" + ID_LOCATION + ")"
				+ " FOREIGN KEY (" + OD_ID_CONSTRAINT + ") REFERENCES " + CONSTRAINTS + " (" + C_ID_CONSTRAINT + ")" + ");";
		// EVENT_CATEGORY 
		private static final String CREATE_TABLE_EVENT_CATEGORY = "CREATE TABLE "
				+ EVENT_CATEGORY + "(" + ECA_NAME + " VARCHAR(30) PRIMARY KEY, "
				+ DEFAULT_DONOTDISTURB + " BOOLEAN"
				+  ");";
		// EVENT_CONSTRAINTS 
		private static final String CREATE_TABLE_EVENT_CONSTRAINTS = "CREATE TABLE "
				+ EVENT_CONSTRAINTS + "(" + ECO_ID_CONSTRAINT + " VARCHAR(5), "
				+ ECO_ID_EVENT + " VARCHAR(5), "
				+ " PRIMARY KEY (" + ECO_ID_CONSTRAINT + ", " + ECO_ID_EVENT + "),"
				+ " FOREIGN KEY (" + ECO_ID_CONSTRAINT + ") REFERENCES " + CONSTRAINTS + " (" + C_ID_CONSTRAINT + ")"
				+ " FOREIGN KEY (" + ECO_ID_EVENT + ") REFERENCES " + EVENT_MODEL + " (" + ID_EVENT_PROVIDER + ")" + ");";
		// CONSTRAINTS 
		private static final String CREATE_TABLE_CONSTRAINTS = "CREATE TABLE "
				+ CONSTRAINTS + "(" + C_ID_CONSTRAINT + " VARCHAR(5) PRIMARY KEY, "
				+ CONSTRAINT_TYPE + " VARCHAR(30), "
				+ C_START + " VARCHAR(30), "
				+ C_END + " VARCHAR(30)" + ");";
		// USER_PREFERENCE
		private static final String CREATE_TABLE_USER_PREFERENCE = "CREATE TABLE "
				+ USER_PREFERENCE + "(" + ACCOUNT + " VARCHAR(30) PRIMARY KEY, "
				+ HOME_LOCATION + " VARCHAR(50), "
				+ ID_SLEEP_TIME + " VARCHAR(5), "
				+ " FOREIGN KEY (" + ACCOUNT + ") REFERENCES " + VACATION_DAYS + " (" + VD_ACCOUNT + ")"
				+ " FOREIGN KEY (" + HOME_LOCATION + ") REFERENCES " + PLACE_MODEL + " (" + ID_LOCATION + ")"
				+ " FOREIGN KEY (" + ID_SLEEP_TIME + ") REFERENCES " + CONSTRAINTS + " (" + C_ID_CONSTRAINT + ")" + ");";
		// VACATION_DAYS 
		private static final String CREATE_TABLE_VACATION_DAYS = "CREATE TABLE "
				+ VACATION_DAYS + "(" + VD_ACCOUNT + " VARCHAR(30), "
				+ VD_ID_CONSTRAINT + " VARCHAR(5), "
				+ " PRIMARY KEY (" + VD_ACCOUNT + ", " + VD_ID_CONSTRAINT + "),"
				+ " FOREIGN KEY (" + VD_ACCOUNT + ") REFERENCES " + USER_PREFERENCE + " (" + ACCOUNT + ")"
				+ " FOREIGN KEY (" + VD_ID_CONSTRAINT + ") REFERENCES " + CONSTRAINTS + " (" + C_ID_CONSTRAINT + ")" + ");";
		// WORK_TIMETABLE
		private static final String CREATE_TABLE_WORK_TIMETABLE = "CREATE TABLE "
				+ WORK_TIMETABLE + "(" + WT_ACCOUNT + " VARCHAR(30), "
				+ WT_ID_CONSTRAINT + " VARCHAR(5), "
				+ " PRIMARY KEY (" + WT_ACCOUNT + ", " + WT_ID_CONSTRAINT + "),"
				+ " FOREIGN KEY (" + WT_ACCOUNT + ") REFERENCES " + USER_PREFERENCE + " (" + ACCOUNT + ")"
				+ " FOREIGN KEY (" + WT_ID_CONSTRAINT + ") REFERENCES " + CONSTRAINTS + " (" + C_ID_CONSTRAINT + ")" + ");";
		
		public static DatabaseCreator createHelper(Context context){
			return new DatabaseCreator(context);
		}
		
		public DatabaseCreator(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			
			// generate all the tables of the db
			db.execSQL(CREATE_TABLE_EVENT_MODEL);
			db.execSQL(CREATE_TABLE_PLACE_MODEL);
			db.execSQL(CREATE_TABLE_QUESTION_MODEL);
			db.execSQL(CREATE_TABLE_OPENING_TIMES);
			db.execSQL(CREATE_TABLE_OPENING_DAYS);
			db.execSQL(CREATE_TABLE_EVENT_CATEGORY);
			db.execSQL(CREATE_TABLE_EVENT_CONSTRAINTS);
			db.execSQL(CREATE_TABLE_CONSTRAINTS);
			db.execSQL(CREATE_TABLE_USER_PREFERENCE);
			db.execSQL(CREATE_TABLE_VACATION_DAYS);
			db.execSQL(CREATE_TABLE_WORK_TIMETABLE);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
			GiveMeLogger.log("Upgrading database from version " + oldVersion + " to "
					+ newVersion + " which will destroy all old data");
			// on upgrade drop older tables
			db.execSQL("DROP TABLE IF EXISTS " + EVENT_MODEL);
			db.execSQL("DROP TABLE IF EXISTS " + PLACE_MODEL);
			db.execSQL("DROP TABLE IF EXISTS " + QUESTION_MODEL);
			db.execSQL("DROP TABLE IF EXISTS " + OPENING_TIMES);
			db.execSQL("DROP TABLE IF EXISTS " + OPENING_DAYS);
			db.execSQL("DROP TABLE IF EXISTS " + EVENT_CATEGORY);
			db.execSQL("DROP TABLE IF EXISTS " + EVENT_CONSTRAINTS);
			db.execSQL("DROP TABLE IF EXISTS " + CONSTRAINTS);
			db.execSQL("DROP TABLE IF EXISTS " + USER_PREFERENCE);
			db.execSQL("DROP TABLE IF EXISTS " + VACATION_DAYS);
			db.execSQL("DROP TABLE IF EXISTS " + WORK_TIMETABLE);
			
			// create new tables
			onCreate(db);
			
		}
		
		
		
		
	}
}
