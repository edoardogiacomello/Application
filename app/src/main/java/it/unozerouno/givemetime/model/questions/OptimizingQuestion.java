package it.unozerouno.givemetime.model.questions;

import android.content.Context;
import android.text.format.Time;

import it.unozerouno.givemetime.model.events.EventInstanceModel;

/**
 * This type of question is made when there are events with missing information
 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
 *
 */
public class OptimizingQuestion extends QuestionModel{
	public static final String TYPE = "OptimizingQuestion";
	private EventInstanceModel event;
	private boolean missingPlace; //If place is unknown
	private boolean missingCategory; //If category has not been set
	private boolean missingConstraints; //If event is marked as movable but no constraints are defined
	public OptimizingQuestion(Context context, EventInstanceModel event,
			boolean missingPlace, boolean missingCategory,
			boolean missingConstraints, Time generationTime) {
		super(context, generationTime);
		this.event = event;
		this.missingPlace = missingPlace;
		this.missingCategory = missingCategory;
		this.missingConstraints = missingConstraints;
	}
	public EventInstanceModel getEventInstance() {
		return event;
	}
	public void setEventInstance(EventInstanceModel event) {
		this.event = event;
	}
	public boolean isMissingPlace() {
		return missingPlace;
	}
	public void setMissingPlace(boolean missingPlace) {
		this.missingPlace = missingPlace;
	}
	public boolean isMissingCategory() {
		return missingCategory;
	}
	public void setMissingCategory(boolean missingCategory) {
		this.missingCategory = missingCategory;
	}
	public boolean isMissingConstraints() {
		return missingConstraints;
	}
	public void setMissingConstraints(boolean missingConstraints) {
		this.missingConstraints = missingConstraints;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof OptimizingQuestion)) return false;
		OptimizingQuestion other = (OptimizingQuestion) o;
		if (this.getEventInstance().equals(other.getEventInstance()) && 
				this.isMissingCategory() == other.isMissingCategory() &&
				this.isMissingConstraints() == other.isMissingConstraints() &&
				this.isMissingPlace() == other.isMissingPlace()
				) return true;
		return false;
	}
	
	
	
	
	

}
