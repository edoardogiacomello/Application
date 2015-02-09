package it.unozerouno.givemetime.model.constraints;

import android.text.format.Time;

/**
 * Represent a Date interval
 * @author Edoardo Giacomello
 *
 */
public class DateConstraint extends Constraint{
	private Time startingDate;
	private Time endingDate;
	public static final String TYPE = "DateConstraint";
	
	/**
	 * Create a new Date Interval starting from startingDate (included) and finishing at endingDate (excluded)
	 * @param startingDate
	 * @param endingDate
	 */
	public DateConstraint(Time startingDate, Time endingDate) {
		this.startingDate = startingDate;
		startingDate.allDay=true;
		this.endingDate = endingDate;
		endingDate.allDay=true;
	}
	
	public DateConstraint(int sDay, int sMonth, int sYear, int eDay, int eMonth, int eYear) {
		Time startD=new Time();
		startD.set(sDay, sMonth-1, sYear);
		Time endD  = new Time();
		endD.set(eDay, eMonth-1, eYear);
		
		startingDate=startD;
		endingDate=endD;
		
	}
	
	@Override
	public Boolean isActive() {
		Time now = new Time();
		now.setToNow();
		if (startingDate.before(now)&&endingDate.after(now)){return true;}
		else {
			return false;
		}
	}
	
	public Boolean isActive(Time when) {
		Time mWhen = new Time(when);
		mWhen.set(mWhen.monthDay, mWhen.month, mWhen.year);
		return (startingDate.before(mWhen)&&endingDate.after(mWhen));
	}

	public Time getStartingDate() {
		return startingDate;
	}

	public Time getEndingDate() {
		return endingDate;
	}
	
	@Override
	public String toString() {
		return "from" + startingDate.monthDay + "/" + (startingDate.month+1) + "/" + startingDate.year +
				"to" + endingDate.monthDay + "/" + (endingDate.month+1) + "/" + endingDate.year;
	}
	@Override
	public DateConstraint clone() {
		Time newStart = new Time(startingDate);
		Time newEnd = new Time(endingDate);
		DateConstraint copy = new DateConstraint(newStart, newEnd);
		return copy;
	}
	
}
