package it.unozerouno.givemetime.controller.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Time;

import java.util.ArrayList;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.controller.fetcher.places.LocationFetcher;
import it.unozerouno.givemetime.controller.fetcher.places.LocationFetcher.OnLocationReadyListener;
import it.unozerouno.givemetime.controller.optimizer.FeasibleEventGiver;
import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.model.questions.FreeTimeQuestion;
import it.unozerouno.givemetime.model.questions.LocationMismatchQuestion;
import it.unozerouno.givemetime.model.questions.QuestionModel;
import it.unozerouno.givemetime.utils.GiveMeLogger;
import it.unozerouno.givemetime.view.questions.QuestionActivity;
import it.unozerouno.givemetime.view.utilities.OnDatabaseUpdatedListener;

public class GiveMeTimeService extends IntentService{
	private DatabaseManager database;
	private static ArrayList<Integer> activeNotificationsId;
	
	
	public GiveMeTimeService() {
		super("GiveMeTimeWorkerThread");
	}

	
	@Override
	public void onCreate() {
		super.onCreate();
		//Initializing components
		database = DatabaseManager.getInstance(getApplication());
		if(activeNotificationsId==null){
			activeNotificationsId = new ArrayList<Integer>();
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Calling back the IntentService onStartCommand for managing the life cycle
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//Free any resources or save the collected data
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		//This methods are meant for debug purpose only
		GiveMeLogger.log("Starting the service Flow");
				serviceFlow();
	}
	
	private void showNotification(String message){
		Notification noti = new Notification.Builder(getApplicationContext())
        .setContentTitle("GiveMeTime")
        .setContentText(message)
        .setSmallIcon(R.drawable.ic_drawer)
        .getNotification();
		NotificationManager mNotificationManager =   (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		int id= activeNotificationsId.size();
		mNotificationManager.notify(id, noti);
		activeNotificationsId.add(activeNotificationsId.size());
		GiveMeLogger.log("Notification sent: "+ message );
	}
	/**
	 * This method contains the whole flow of the service.
	 */
	private void serviceFlow(){
		//Getting current active events
		OnDatabaseUpdatedListener<ArrayList<EventInstanceModel>> listener = new OnDatabaseUpdatedListener<ArrayList<EventInstanceModel>>() {
			@Override
			protected void onUpdateFinished(
					
					final ArrayList<EventInstanceModel> activeEvents) {
				//That's the list of current active events
				if (activeEvents.isEmpty()){
					//2a - No events are active, then we suppose it is the free-time (please make checks about Work TimeTable, etc)	
					GiveMeLogger.log("No active events");
					flowIfNoActiveEvents();
						
				} else {
					//2b- If there are active events, proceed with the flow
					GiveMeLogger.log(activeEvents.size() + " events are active");
					flowIfActiveEvents(activeEvents);
				}
			}
		};
		//1 - Get the active event 
		DatabaseManager.getActiveEvents(listener, getApplicationContext());
	}
	
	private void flowIfActiveEvents(final ArrayList<EventInstanceModel> activeEvents){
		//Getting current Location
		LocationFetcher.getInstance(getApplication());
		LocationFetcher.getLastLocation(new OnLocationReadyListener() {
			@Override
			public void onConnectionFailed() {
				GiveMeLogger.log("No connectivity for getting location");
			}
			@Override
			public void locationReady(Location location) {
				GiveMeLogger.log("Got location! " + location.getLatitude() + " " + location.getLongitude());
				//Now that we have a location, we can check if the user is really where it should be
				for (EventInstanceModel currentActiveEvent : activeEvents) {
					if (currentActiveEvent.getEvent().getLocation() == null){
						//Event location is not set
						if(currentActiveEvent.getEvent().getDoNotDisturb()){
							Time now = new Time();
							now.setToNow();
							LocationMismatchQuestion question = new LocationMismatchQuestion(getApplication(), currentActiveEvent, location, now);
							showNotification("Storing a new LocationMismatchQuestion");
							String message = getString(R.string.question_list_description_locationmismatch) + currentActiveEvent.getEvent().getName();
							long questionId = DatabaseManager.addQuestion(question,message);
							GiveMeLogger.log("Stored a locationMismatch question with id:" + questionId);
							
						}else{
							Time now = new Time();
							now.setToNow();
							LocationMismatchQuestion question = new LocationMismatchQuestion(getApplication(), currentActiveEvent, location, now);
							String message = getString(R.string.question_list_description_locationmismatch) + currentActiveEvent.getEvent().getName();
							long questionId = DatabaseManager.addQuestion(question, message);
							GiveMeLogger.log("Showing locationMismatch question with id:" + questionId);
							showQuestion(questionId, question, message);
						
						}
					} else
					{
						//Current event has a location, we can check if the user is far from it
						if (location.distanceTo(currentActiveEvent.getEvent().getLocation())> UserKeyRing.getLocationMaxDistTolerance(getApplicationContext())){
							//The user is far from the current scheduled event
							if(currentActiveEvent.getEvent().getDoNotDisturb()){
								Time now = new Time();
								now.setToNow();
								LocationMismatchQuestion question = new LocationMismatchQuestion(getApplication(), currentActiveEvent, location, now);
								String message = getString(R.string.question_list_description_locationmismatch) + currentActiveEvent.getEvent().getName();
								long questionId = DatabaseManager.addQuestion(question, message);
								GiveMeLogger.log("Stored a locationMismatch question with id:" + questionId);
							}else{
								Time now = new Time();
								now.setToNow();
								LocationMismatchQuestion question = new LocationMismatchQuestion(getApplication(), currentActiveEvent, location, now);
								showNotification("Are you doing " + currentActiveEvent.getEvent().getName() + " at " + location.getLatitude()+","+location.getLongitude()+"?");
								String message = getString(R.string.question_list_description_locationmismatch) + currentActiveEvent.getEvent().getName();
								long questionId = DatabaseManager.addQuestion(question, message);
								GiveMeLogger.log("Showing locationMismatch question with id:" + questionId);
								showQuestion(questionId, question, message);
							}
						} else {
							GiveMeLogger.log("User is at the scheduled event");
						}
					}
				}
			}
			
			
		}, UserKeyRing.getLocationUpdateFrequency(getApplication()));
	}
	
	private void showQuestion(long questionId, QuestionModel question, String message) {
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
				.setSmallIcon(it.unozerouno.givemetime.R.drawable.ic_launcher)
				.setAutoCancel(true)
		        .setContentTitle("GiveMeTime")
		        .setContentText(message);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, QuestionActivity.class);
		resultIntent.putExtra(QuestionModel.QUESTION_ID, questionId);
		
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(QuestionActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());
		
	} 
	private void flowIfNoActiveEvents(){
		//Getting current Location
				LocationFetcher.getInstance(getApplication());
				LocationFetcher.getLastLocation(new OnLocationReadyListener() {
					@Override
					public void onConnectionFailed() {
						GiveMeLogger.log("No connectivity for getting location");
					}
					@Override
					public void locationReady(Location location) {
						GiveMeLogger.log("Got location! " + location.getLatitude() + " " + location.getLongitude());
						//Now we know that user has freeTime and we know his location
						//Getting a new feasible event to propose to the user
						Time now = new Time();
						now.setToNow();
						FeasibleEventGiver.getClosestFisibleEvent(getApplicationContext(), location, now, new OnDatabaseUpdatedListener<EventInstanceModel>() {
							
							@Override
							protected void onUpdateFinished(EventInstanceModel closestEvent) {
								//TimeConstraint sleepTime = DatabaseManager.getUserSleepTime(UserKeyRing.getUserEmail(getApplicationContext()) );
								if (closestEvent != null/* && sleepTime != null && !sleepTime.isActive()*/){
									showNotification("Maybe you could do:" + closestEvent.getEvent().getName());
									Time now = new Time();
									now.setToNow();
									FreeTimeQuestion question = new FreeTimeQuestion(getApplicationContext(), now, closestEvent);
									String message = getString(R.string.question_list_description_freetime) + closestEvent.getEvent().getName();
									long questionId = DatabaseManager.addQuestion(question, message);
									GiveMeLogger.log("Showing locationMismatch question with id:" + questionId);
									showQuestion(questionId, question, message);
									
								}else{
									//TODO: There are any feasible events to be done now
									showNotification("Enjoy your Free time!");
								}
								
							}
						});
						
					} 
				}, UserKeyRing.getLocationUpdateFrequency(getApplication()));
	}
	
}