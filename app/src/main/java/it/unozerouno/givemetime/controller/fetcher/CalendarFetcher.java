package it.unozerouno.givemetime.controller.fetcher;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.text.format.Time;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import it.unozerouno.givemetime.model.CalendarModel;
import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.utils.AsyncTaskWithListener;
import it.unozerouno.givemetime.utils.GiveMeLogger;
import it.unozerouno.givemetime.utils.Results;
import it.unozerouno.givemetime.utils.TaskListener;
/**
 * Fetcher for device stored calendars.
 * It supports both "external queries" [takes as input an Action and a projection and return each row in a specific TaskListener] and "internal" one [for fetching and Model creation, projections are managed
 * internally].
 * External Queries:  
 * Please specify a task with setAction() before calling .execute(), you can chose them from CalendarFetcher.Actions
 * You also have to specify the query projection when calling execute(), you can pick one from CalendarFetcher.Projections or specify your own.
 * Results are returned to attached TaskListener (use setListener)
 * Internal Queries: 
 * Just specify a compatible action (i.e. "CALENDARS_TO_MODEL") and call the relative getter (i.e. getCalendarList()) on the TaskListener onResult.
 * Note that in this case the result returned to the TaskListener is a string from the "Results" class.
 * 
 * In both cases it is possible to manage a ProgressBar on the calling activity (if provided).
 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
 * @see TaskListener
 */
public class CalendarFetcher extends AsyncTaskWithListener<String, Void, String[]> {
	//NOTICE: For the developers - When adding new functions, please comply with the present structure.
	//I.E: Adding an actions: provide "Actions" integer with relative projections (if needed)
	//Add "case" in doInBackground()
	//Compute result in a separate function. See "getCalendarlist" for example.
	//Don't forget to call "setResult" whenever a single result (row) is ready!
	
	
	//Results
	private static List<CalendarModel> calendarList;
	private static List<EventInstanceModel> eventList;
	
	
	
	/**
	 * Overview of the possible actions performable from CalendarFetcher
	 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
	 *
	 */
	public static class Actions{
		public static final int NO_ACTION = -1;
		public static final int CALENDARS_TO_MODEL = 1;
		public static final int LIST_OF_EVENTS = 2;
		public static final int ADD_NEW_CALENDAR = 3;
		public static final int LIST_EVENTS_ID_RRULE = 4;
		public static final int UPDATE_EVENT = 5;
		public static final int ADD_NEW_EVENT = 6;
		//... here other actions
	}
	/**
	 * Overview of recurrent projections to be used in CalendarFetcher
	 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
	 *
	 */
	public static class Projections {
		//Calendar related
		public static String[] CALENDAR_ID_NAME_PROJ = {Calendars._ID, Calendars.NAME};
		public static String[] CALENDAR_ID_OWNER_NAME_COLOUR = {Calendars._ID, Calendars.OWNER_ACCOUNT, Calendars.NAME, Calendars.CALENDAR_COLOR};
		//Event related
		public static String[] EVENT_ID_RRULE_RDATE = {Events._ID,Events.RRULE,Events.RDATE, Events.DURATION, Events.EVENT_LOCATION};
		public static String[] EVENT_ID_TITLE = {Events._ID, Events.TITLE};
		public static String[] EVENT_INFOS = {Events._ID, Events.TITLE, Events.DTSTART, Events.DTEND, Events.EVENT_COLOR, Events.RRULE, Events.RDATE, Events.ALL_DAY}; //When changing this remember to update both fetching and updating
		public static String[] INSTANCES_INFOS = {Instances.EVENT_ID, Instances.BEGIN, Instances.END};
		//...
		
		public static int getIndex(String[] projection, String coloumn) {
			int counter = 0;
			for (String currentColoumn : projection) {
				if (currentColoumn == coloumn)
					return counter;
				else {
					counter++;
				}
			}
			return -1;
		}
	
	}

	
	
	private int task = Actions.NO_ACTION;
	Context caller;
	ProgressBar progressBar;
	
	public CalendarFetcher(Context caller) {
		this.caller = caller;
	}
	
	
	public CalendarFetcher(Activity caller, ProgressBar progressBar){
		this(caller);
		this.progressBar =progressBar;
	}
	
	/**
	 * Set the action to perform in order to get expected result
	 * @param action
	 * @see CalendarFetcher
	 */
	public void setAction(int action){
		task = (int) action;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//If progressbar is present, than show it
		if (progressBar != null){
			progressBar.setVisibility(View.VISIBLE);
		}
	}
	@Override
	protected void onPostExecute(String[] result) {
		super.onPostExecute(result);
		//If progressbar is present, than hide it
		if (progressBar != null){
			progressBar.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	protected synchronized String[] doInBackground(String... projection) {
		switch (task) {
		case Actions.NO_ACTION:
			break;
		case Actions.CALENDARS_TO_MODEL:
			calendarList = getCalendarModel();
			setResult(Results.RESULT_OK);
			break;
		case Actions.LIST_OF_EVENTS:
			getEvents();
			getInstances();
			setResult(Results.RESULT_OK);
			break;
		case Actions.ADD_NEW_CALENDAR:
			createCalendar();
			setResult(Results.RESULT_OK);
			break;
		case Actions.LIST_EVENTS_ID_RRULE:
			fetchEventList();
			break;
		case Actions.UPDATE_EVENT:
			updateEvent();
			break;
		case Actions.ADD_NEW_EVENT:
			addNewEvent();
			break;
		//Add here new actions
		default:
			break;
		}
		
		return null;
	}
	
	/**
	 * Build the uri as sync adapter in order to gain more write access
	 * @param uri
	 * @param account
	 * @param accountType
	 * @return
	 */
	static Uri asSyncAdapter(Uri uri, String account, String accountType) {
	    return uri.buildUpon()
	        .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
	        .appendQueryParameter(Calendars.ACCOUNT_NAME, account)
	        .appendQueryParameter(Calendars.ACCOUNT_TYPE, accountType).build();
	 }
	
	Long queryStartTime;
	Long queryEndTime;
	public void setEventInstanceTimeQuery(Long start, Long end){
		queryStartTime = start;
		queryEndTime = end;
	}
		
	private int eventId = -1;
	public void setEventId(int eventId){
	this.eventId = eventId;	
	}
	/**
	 * Fetch event list and returns each result to the Task Listener attached to CalendarFetcher. If the setEventId() has not been called, or the given id is -1, all events are returned
	 */
	private synchronized void getEvents(){
		
		ContentResolver cr = caller.getContentResolver();
		Uri eventURI = Events.CONTENT_URI;
		
		String[] eventInfoProjection = Projections.EVENT_INFOS;
		String whereCalendar = Events.CALENDAR_ID + " = " + UserKeyRing.getCalendarId(caller);
		String whereEventId = "";
		if(eventId != -1 && eventId >= 0){
			whereEventId = " AND " + Events._ID + " = " + eventId;
		}
		
		
		
		//For Identifying as SyncAdapter, User must already be logged)
		eventURI = asSyncAdapter(eventURI, UserKeyRing.getUserEmail(caller), "com.google");
		
		
		// execute the query, get a Cursor back
		Cursor eventCursor = cr.query(eventURI, eventInfoProjection, whereCalendar + whereEventId , null, Events._ID);
		
		// step through the records
		while(eventCursor.moveToNext()){
			String[] result = new String[eventInfoProjection.length];
			for (int i = 0; i < result.length; i++) {
				result[i] = eventCursor.getString(i);
			}
			// provide result to TaskListener
			setResult(Results.RESULT_TYPE_EVENT,result);
		}
		eventCursor.close();
		
	}
	
	


	private void getInstances(){
		//Fetching events Instances
				ContentResolver cr = caller.getContentResolver();
				String[] eventInstancesProjection = Projections.INSTANCES_INFOS;
				String whereEventId = "";
				if(eventId != -1 && eventId >= 0){
					whereEventId = " AND " + Instances.EVENT_ID + " = " + eventId;
				}
				String whereCalendar = Instances.CALENDAR_ID + " = " + UserKeyRing.getCalendarId(caller);
				
						// Construct the query with the desired date range.
						Uri.Builder instancesUriBuilder = Instances.CONTENT_URI.buildUpon();
						ContentUris.appendId(instancesUriBuilder, queryStartTime);
						ContentUris.appendId(instancesUriBuilder, queryEndTime);
						
						
						Cursor instanceCursor = cr.query(instancesUriBuilder.build(), eventInstancesProjection, whereCalendar + whereEventId, null, Instances.EVENT_ID);
						while (instanceCursor.moveToNext()){
							String[] eventInstance = new String[eventInstancesProjection.length];
							for (int i = 0; i < eventInstance.length; i++) {
								eventInstance[i] = instanceCursor.getString(i);
							}
							setResult(Results.RESULT_TYPE_INSTANCE,eventInstance);
						}
						instanceCursor.close();
	}
	
	private void fetchEventList(){
		Cursor cur = null;
		ContentResolver cr = caller.getContentResolver();
		Uri uri = Events.CONTENT_URI;
		
		String WHERE_CLAUSE = Events.CALENDAR_ID +" = " + UserKeyRing.getCalendarId(caller);
		// execute the query, get a Cursor back
		cur = cr.query(uri, Projections.EVENT_ID_RRULE_RDATE, WHERE_CLAUSE, null, null);
		
		// step through the records
		while(cur.moveToNext()){
			String[] result = new String[Projections.EVENT_ID_RRULE_RDATE.length];
			for (int i = 0; i < result.length; i++) {
				result[i] = cur.getString(i);
			}
			// provide result to TaskListener
			setResult(result);
		}
		cur.close();
	}
	
	private void getSingleEvent(){
		if (eventId==-1) {
			GiveMeLogger.log("Event Id not set! Aborting..");
			return;
		}
		
		
	}
	
	private EventInstanceModel eventInstanceToUpdate;
	
	public void setEventToUpdate(EventInstanceModel eventToUpdate){
		eventInstanceToUpdate = eventToUpdate;
	}
	
	//TODO: Fix this in the case of recurring events! They will need to edit RRULE instead of starting Time!
	private void updateEvent(){
		if(eventInstanceToUpdate == null){
			System.err.println("Event to update has not been set.");
			return;
		}
		
		ContentResolver cr = caller.getContentResolver();
		Uri uri = Events.CONTENT_URI;
				
		ContentValues values = new ContentValues();
		
		values.put(Events._ID, eventInstanceToUpdate.getEvent().getID());
		values.put(Events.TITLE, eventInstanceToUpdate.getEvent().getName());
		if (!eventInstanceToUpdate.getEvent().isRecursive()){
			values.put(Events.DTSTART, eventInstanceToUpdate.getStartingTime().toMillis(false));
			values.put(Events.DTEND, eventInstanceToUpdate.getEndingTime().toMillis(false));
		} else {
			values.put(Events.DTSTART, eventInstanceToUpdate.getStartingTime().toMillis(false));
			// dtend è sicuramente null se ricorsivo 
			// setStartingTime viene settato all'update di un evento
			values.put(Events.DURATION, eventInstanceToUpdate.getEvent().getDuration());
		}
		values.put(Events.EVENT_COLOR, eventInstanceToUpdate.getEvent().getColor());
		//values.put(Events.EVENT_LOCATION, eventUpdate.getLocation());
		
		
		
		//For Identifying as SyncAdapter, User must already be logged)
		uri = asSyncAdapter(uri, UserKeyRing.getUserEmail(caller), "com.google");
		
		// execute the query, get a Cursor back
		int nUpdates = cr.update(uri, values, Events.CALENDAR_ID + " = " + UserKeyRing.getCalendarId(caller) + " AND " + Events._ID + " = " + eventInstanceToUpdate.getEvent().getID(), null);
		if( nUpdates > 0) {
			System.out.println("Updated " + nUpdates + " Rows");
			setResult(Results.RESULT_OK);
		}else{
			setResult(Results.RESULT_ERROR);
		}

	}
	
	/**
	 * Adds a new event to the Calendar Provider. Please set this event with setEventToUpdate() prior to task execution
	 */
	private void addNewEvent(){
		if(eventInstanceToUpdate == null){
			System.err.println("Event to add has not been set.");
			return;
		}
		ContentResolver cr = caller.getContentResolver();
		
		// extract the data from newEvent to be inserted into EVENT_MODEL
		String calId = UserKeyRing.getCalendarId(caller);
		String title = eventInstanceToUpdate.getEvent().getName();
		long startTime = eventInstanceToUpdate.getStartingTime().toMillis(false);
		long endingTime = eventInstanceToUpdate.getEndingTime().toMillis(false);
		String RRULE = eventInstanceToUpdate.getEvent().getRRULE();
		String RDATE = eventInstanceToUpdate.getEvent().getRDATE();
		String duration = eventInstanceToUpdate.getEvent().getDuration();
		boolean isRecursive = eventInstanceToUpdate.getEvent().isRecursive();
		int allDay = eventInstanceToUpdate.getEvent().isAllDayEvent();
		
		ContentValues values = new ContentValues();
		
		// This values has to be inserted in order for the provider to accept the insert on the Events table
		values.put(CalendarContract.Events.TITLE, title);
		values.put(CalendarContract.Events.DTSTART, startTime);
		if (isRecursive){
			// only if the event is recurring
			values.put(CalendarContract.Events.DURATION, duration); 
			// only if the event is recurring. If not null, original_id and original_sync_id must be null
			values.put(CalendarContract.Events.RRULE, RRULE);
			values.put(CalendarContract.Events.RDATE, RDATE);
			//values.put(CalendarContract.Events.ORIGINAL_ID, "null");
			//values.put(CalendarContract.Events.ORIGINAL_SYNC_ID, "null");
		} else {
			// only if the event is non-recurring
			values.put(CalendarContract.Events.DTEND, endingTime); 
		}
		if (allDay == 1){
			// if allday == 1, it must be TIMEZONE_UTC
			values.put(CalendarContract.Events.EVENT_TIMEZONE, Time.TIMEZONE_UTC); 
		} else {
			values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
		}
		// do not modify
		values.put(CalendarContract.Events.CALENDAR_ID, calId); 
		
		// execute the query and return the ID of the new event
		Uri result = cr.insert(CalendarContract.Events.CONTENT_URI, values);
		// this is the ID of the newly inserted event
		String eventID = result.getLastPathSegment();
		String[] insertResult = new String[1];
		insertResult[0] = eventID;
		setResult(insertResult);
	}
	
	/**
	 * This function fetches the calendars managing needed projections in order to provide a ready-to-use CalendarModel list.
	 */
	private List<CalendarModel> getCalendarModel(){
		List<CalendarModel> calendarList = new ArrayList<CalendarModel>();
		// Run query
		Cursor cur = null;
		ContentResolver cr = caller.getContentResolver();
		Uri uri = Calendars.CONTENT_URI;
		String[] projection = Projections.CALENDAR_ID_OWNER_NAME_COLOUR;
		
		//For Identifying as SyncAdapter, User must already be logged)
		uri = asSyncAdapter(uri, UserKeyRing.getUserEmail(caller), "com.google");
		
		String WHERE_CLAUSE = CalendarContract.Calendars.OWNER_ACCOUNT + " = '" + UserKeyRing.getUserEmail(caller) + 
				"' OR " + CalendarContract.Calendars.OWNER_ACCOUNT + " LIKE '%group.calendar.google.com%'";
		
		// Submit the query and get a Cursor object back. 
		cur = cr.query(uri, projection, WHERE_CLAUSE ,null, null);
		
		// Use the cursor to step through the returned records
		while (cur.moveToNext()) {
		   CalendarModel newCalendar = new CalendarModel(cur.getString(0), cur.getString(1), cur.getString(2), Integer.parseInt(cur.getString(3)));
		   calendarList.add(newCalendar);
		}
		cur.close();
		return calendarList;
		}
	

	
	/**
	 * Insert a new calendar into CalendarProvider
	 * @return id of the new calendar
	 */
	public void createCalendar(){
		//Setting calendar data
		ContentValues values = new ContentValues();
		values.put(Calendars.ACCOUNT_NAME,UserKeyRing.getUserEmail(caller));
		values.put(Calendars.ACCOUNT_TYPE, "com.google");
		values.put(Calendars.NAME, "GiveMeTime Calendar");
		values.put(Calendars.CALENDAR_DISPLAY_NAME, "GiveMeTime Calendar");
		values.put(Calendars.CALENDAR_COLOR, Color.GREEN);
		values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
		values.put(Calendars.OWNER_ACCOUNT, UserKeyRing.getUserEmail(caller));
		values.put(Calendars.SYNC_EVENTS, 1);
		
		//TODO: Set here the timezone
		//values.put(Calendars.CALENDAR_TIME_ZONE, Locale.getDefault().t);
		
		ContentResolver cr = caller.getContentResolver();
		Uri uri = Calendars.CONTENT_URI;
		
		//For Identifying as SyncAdapter, User must already be logged)
				uri = asSyncAdapter(uri, UserKeyRing.getUserEmail(caller), "com.google");
				cr.insert(uri, values);
	}
	
	Context getCaller(){return caller;}
	
	public static List<CalendarModel> getCalendarList(){
		return calendarList;
	}
	
	public static List<EventInstanceModel> getEventList(){
		return eventList;
	}
	}
