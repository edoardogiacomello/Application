package it.unozerouno.givemetime.model.questions;

import android.content.Context;
import android.text.format.Time;

public abstract class QuestionModel {
	//Descriptor strings for letting database interact with UI
	public static final String QUESTION_ID = "question_id";
	public static final String QUESTION_TEXT = "question_text";
	public static final String QUESTION_TIME = "question_time";
	public static final String QUESTION_TYPE = "question_type";
	
	private Context context;
	private Time generationTime;
	private int id;
	private int eventId;
	
	
	

	public QuestionModel(Context context, Time generationTime) {
		super();
		this.id = -1;
		this.context = context;
		this.generationTime = generationTime;
	}
	
	

	public int getEventId() {
		return eventId;
	}



	public void setEventId(int eventId) {
		this.eventId = eventId;
	}



	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Time getGenerationTime() {
		return generationTime;
	}

	public void setGenerationTime(Time generationTime) {
		this.generationTime = generationTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public interface OnQuestionGenerated{
		public void onQuestionGenerated();
	}
}
