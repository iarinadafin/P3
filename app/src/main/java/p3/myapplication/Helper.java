package p3.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Helper {

	private Context c;

	public Helper(Context c) {
		this.c = c;
	}

	void goToSignIn (boolean ifFromSignUp) {
		FirebaseAuth.getInstance().signOut();

		Intent i = new Intent(c, SignInActivity.class);
		i.putExtra("p3.myapplication:ifVerificationNeeded", String.valueOf(ifFromSignUp));
		c.startActivity(i);
	}

	void goToSignUp () {
		Intent i = new Intent(c, SignUpActivity.class);
		c.startActivity(i);
	}

	void goHome () {
		Intent i = new Intent(c, HomeActivity.class);
		c.startActivity(i);
	}

	void goToMessages () {
		Intent i = new Intent(c, ChatsListActivity.class);
		c.startActivity(i);
	}

	public void goToProfile (boolean isOwnProfile, String userID) {
		Intent i = new Intent(c, UserProfileActivity.class);

		// if the user tries to access their own profile, send "true"; send "false" otherwise
		if (isOwnProfile)
			i.putExtra("p3.myapplication:isOwnProfile", "true");
		else {
			i.putExtra("p3.myapplication:isOwnProfile", "false");
			i.putExtra("p3.myapplication:userID", userID);
		}

		c.startActivity(i);
	}

	public void goToViewMeeting (String meetingID, String module, String dateFormatted, String hoursString, String dateString) {
		Intent i = new Intent(c, ViewMeetingActivity.class);
		i.putExtra("p3.myapplication:meeting_id", meetingID); // sends id of meeting
		i.putExtra("p3.myapplication:module_id", module); // sends module name
		i.putExtra("p3.myapplication:date", dateFormatted); // sends date
		i.putExtra("p3.myapplication:hours", hoursString); // sends hours
		i.putExtra("p3.myapplication:dateString", dateString); // sends hours
		c.startActivity(i);
	}

	public void goToChat (String meetingID, String meetingName) {
		Intent i = new Intent(c, ChatActivity.class);
		i.putExtra("p3.myapplication:meeting_id", meetingID);
		i.putExtra("p3.myapplication:meeting_name", meetingName);
		c.startActivity(i);
	}

	public void goToRating (String meetingID, String meetingName) {
		Intent i = new Intent(c, RatingActivity.class);
		i.putExtra("p3.myapplication:meeting_id", meetingID);
		i.putExtra("p3.myapplication:meeting_name", meetingName);
		c.startActivity(i);
	}

	void deleteMeeting (String userID, DatabaseReference reference, String meetingID) {
		reference.child("meetings/" + meetingID).removeValue();
		reference.child("users/" + userID + "/meetings/" + meetingID).removeValue();
		reference.child("chats/" + meetingID).removeValue();
	}

	public void addPoints (DataSnapshot dataSnapshot, DatabaseReference reference, String userUid, int points) {
		int score = Integer.parseInt(dataSnapshot.child("users/" + userUid + "/score").getValue(String.class));
		reference.child("users/" + userUid + "/score").setValue(String.valueOf(score + points));
	}

	float getRating (DataSnapshot dataSnapshot) {
		return Float.valueOf(dataSnapshot.child("totalScore").getValue(String.class)) / Float.valueOf(dataSnapshot.child("numberOfRatings").getValue(String.class));
	}

	/**
	 * Helps set the date label to the date selected with the date picker
	 * @param year the year value
	 * @param month the month value
	 * @param dayOfMonth the day of month value
	 */
	void setDateLabel (TextView dateLabel, int year, int month, int dayOfMonth) {
		String dateString = String.format(c.getResources().getString(R.string.date_format_create),
				new DateFormatSymbols().getMonths()[month], dayOfMonth, year);
		dateLabel.setText(dateString);
	}

	/**
	 * Helps set the time labels to the times selected with the time pickers
	 * @param field the time label that will be set; either start time or end time
	 * @param hour the hour value
	 * @param minute the minute value
	 */
	void setTimeLabel (TextView field, int hour, int minute) {
		String hourString = checkExtraZero(hour);
		String minuteString = checkExtraZero(minute);

		String timeString = String.format(c.getResources().getString(R.string.time_format_create), hourString, minuteString);
		field.setText(timeString);
	}

	/**
	 * Checks the date or time values, so that any single digit will be suffixed to a zero. Helps set a universal format for the timestamp.
	 * @param number the number that will be checked
	 * @return a string representing the value, prefixed or not
	 */
	String checkExtraZero (int number) {
		if (number < 10)
			return "0" + Integer.toString(number);
		return Integer.toString(number);
	}
	/**
	 * Verifies the validity of the create meeting fields
	 * @param startDate the start date of the meeting
	 * @param endDate the end date of the meeting
	 * @param startTimeLabel the start time of the meeting
	 * @param endTimeLabel the end time of the meeting
	 * @return either true if the info is correct, false otherwise
	 */
	boolean verifyData (String startDate, String endDate, TextView startTimeLabel, TextView endTimeLabel, EditText meetingName) {
		boolean check = true; // initially assumes all info is correct
		try {
			Date startTimeDate = new SimpleDateFormat("HH:mm", Locale.UK).parse(startTimeLabel.getText().toString());
			Date endTimeDate = new SimpleDateFormat("HH:mm", Locale.UK).parse(endTimeLabel.getText().toString());

			// checks if start time is in the past
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			if (new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).parse(startDate).before(calendar.getTime())) {
				startTimeLabel.setError("");
				check = false;
			}
			// checks if start time is after end time or if end time is in the past; if so, throws UI error
			if (startTimeDate.after(endTimeDate) || new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).parse(endDate).before(calendar.getTime())) {
				endTimeLabel.setError("");
				check = false;
			}
			// checks if meeting name is empty; if so, throws UI error
			if (meetingName.getText().toString().isEmpty()) {
				meetingName.setError("Required!");
				check = false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return check;
	}

	/**
	 * Gets a date string and transforms it into a friendly format
	 * @param dateString the string that should be transformed; has format: yyyy-MM-dd
	 * @return a string with the friendly date format EEE, MMM dd, yyyy
	 */
	String getFriendlyDate (String dateString) {
		Date dateObject = new Date();
		try {
			dateObject = new SimpleDateFormat("yyyy-MM-dd", Locale.UK).parse(dateString);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return c.getResources().getString(R.string.date_field, // date resource
				new SimpleDateFormat("EEE", Locale.UK).format(dateObject), // friendly short day of week
				new SimpleDateFormat("MMM", Locale.UK).format(dateObject), // friendly short month
				new SimpleDateFormat("dd", Locale.UK).format(dateObject), // day of month
				new SimpleDateFormat("yyyy", Locale.UK).format(dateObject)); // year
	}

	String prepareDate (int year, int month, int day, String time) {
		return year + "-" + checkExtraZero(month + 1) + "-" + checkExtraZero(day) + " " + time;
	}
}