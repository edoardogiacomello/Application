package it.unozerouno.givemetime.model.questions;

import android.content.Context;
import android.text.format.Time;

import it.unozerouno.givemetime.model.events.EventInstanceModel;

/**
 * This type of service-generated question is made when the user is apparently not engaged in any event
 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
 *
 */
public class FreeTimeQuestion extends QuestionModel{
	public static final String TYPE = "FreeTimeQuestion";
	EventInstanceModel closestEvent;
	
	public FreeTimeQuestion(Context context, Time generationTime, EventInstanceModel closestEvent) {
		super(context, generationTime);
		this.closestEvent = closestEvent;
	}

	public EventInstanceModel getClosestEvent() {
		return closestEvent;
	}

	public void setClosestEvent(EventInstanceModel closestEvent) {
		this.closestEvent = closestEvent;
	}

}
