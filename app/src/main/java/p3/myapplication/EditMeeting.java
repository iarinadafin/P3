package p3.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import p3.myapplication.Model.Message;

public class EditMeeting extends AppCompatActivity {
	DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
	FirebaseAuth mAuth = FirebaseAuth.getInstance();

	EditText meetingName;
	TextView dateLabel;
	TextView startTimeLabel;
	TextView endTimeLabel;
	DatePickerDialog.OnDateSetListener dateSetListener;
	TimePickerDialog.OnTimeSetListener startTimeSetListener;
	TimePickerDialog.OnTimeSetListener endTimeSetListener;
	Button chooseDate;
	Button chooseStartTime;
	Button chooseEndTime;
	Button submit;

	Calendar calendar = Calendar.getInstance();
	int selectedDay;
	int selectedMonth;
	int selectedYear;
	int selectedStartHour;
	int selectedStartMinute;
	int selectedEndHour;
	int selectedEndMinute;
	String meetingReference;
	String module;
	String userName;
	Helper helper = new Helper(this);

	@SuppressWarnings("ConstantConditions")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_meeting);

		meetingReference = getIntent().getExtras().getString("p3.myapplication:meetingReference");
		module = getIntent().getExtras().getString("p3.myapplication:module");
		selectedYear = getIntent().getExtras().getInt("p3.myapplication:year");
		selectedMonth = getIntent().getExtras().getInt("p3.myapplication:month");
		selectedDay = getIntent().getExtras().getInt("p3.myapplication:day");
		selectedStartHour = getIntent().getExtras().getInt("p3.myapplication:startHour");
		selectedStartMinute = getIntent().getExtras().getInt("p3.myapplication:startMinute");
		selectedEndHour = getIntent().getExtras().getInt("p3.myapplication:endHour");
		selectedEndMinute = getIntent().getExtras().getInt("p3.myapplication:endMinute");
		userName = getIntent().getExtras().getString("p3.myapplication:name");

		meetingName = findViewById(R.id.meetingNameEdit);
		dateLabel = findViewById(R.id.dateLabelEdit);
		startTimeLabel = findViewById(R.id.startTimeLabelEdit);
		endTimeLabel = findViewById(R.id.endTimeLabelEdit);
		chooseDate = findViewById(R.id.chooseDateButtonEdit);
		chooseStartTime = findViewById(R.id.chooseStartTimeButtonEdit);
		chooseEndTime = findViewById(R.id.chooseEndTimeButtonEdit);
		submit = findViewById(R.id.confirmEdit);

		meetingName.setText(getIntent().getExtras().getString("p3.myapplication:meetingName"));

		// sets date picker to now
		helper.setDateLabel(dateLabel, selectedYear, selectedMonth, selectedDay);
		helper.setTimeLabel(startTimeLabel, selectedStartHour, 0);
		helper.setTimeLabel(endTimeLabel, selectedEndHour, 0);

		// date picker dialog button
		chooseDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DatePickerDialog dialog = new DatePickerDialog(EditMeeting.this,
						dateSetListener, selectedYear, selectedMonth, selectedDay);

				long now = Calendar.getInstance().getTimeInMillis() + 3600000;
				dialog.getDatePicker().setMinDate(now);

				dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
				dialog.show();
			}
		});

		// start time picker dialog button
		chooseStartTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimePickerDialog dialog = new TimePickerDialog(EditMeeting.this,
						startTimeSetListener, selectedStartHour, selectedStartMinute, true);
				dialog.show();
			}
		});

		// end time picker dialog button
		chooseEndTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimePickerDialog dialog = new TimePickerDialog(EditMeeting.this,
						endTimeSetListener, selectedEndHour, selectedEndMinute, true);
				dialog.show();
			}
		});

		// sets listener to date picker
		dateSetListener = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
				selectedYear = year;
				selectedMonth = month;
				selectedDay = dayOfMonth;

				helper.setDateLabel(dateLabel, year, month, dayOfMonth);
			}
		};

		// sets listener to start time picker
		startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				selectedStartHour = hourOfDay;
				selectedStartMinute = minute;

				helper.setTimeLabel(startTimeLabel, hourOfDay, minute);
			}
		};

		// sets listener to end time picker
		endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				selectedEndHour = hourOfDay;
				selectedEndMinute = minute;

				helper.setTimeLabel(endTimeLabel, hourOfDay, minute);
			}
		};

		// confirms and moves to the module meeting list
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// prepares the start and end dates from the date and time pickers, in the right format for the database
				String startDate = helper.prepareDate(selectedYear, selectedMonth, selectedDay, startTimeLabel.getText().toString());
				String endDate = helper.prepareDate(selectedYear, selectedMonth, selectedDay, endTimeLabel.getText().toString());

				// if verification passes
				if (helper.verifyData(startDate, endDate, startTimeLabel, endTimeLabel, meetingName)) {
					// sets all meeting information
					reference.child("meetings/" + meetingReference + "/startDate").setValue(startDate);
					reference.child("meetings/" + meetingReference + "/endDate").setValue(endDate);

					// adds an edit notification to meeting chat
					String notification = reference.child("chats/" + meetingReference).push().getKey();
					reference.child("chats/" + meetingReference + "/" + notification).setValue(
							new Message(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK).format(calendar.getTime()),
										"system",
										userName + " edited the meeting details"));

					// goes to view meeting after creation
					helper.goToViewMeeting(meetingReference,
							module,
							helper.getFriendlyDate(selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay),
							startTimeLabel.getText().toString() + " - " + endTimeLabel.getText().toString(),
							selectedYear + "-" + selectedMonth + "-" + selectedDay);
				}
				else {
					Toast.makeText(EditMeeting.this, "Please review your details.", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		finish();
	}

	@Override
	public void onBackPressed() {
		helper.goToViewMeeting(meetingReference,
							   module,
							   helper.getFriendlyDate(selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay),
							   startTimeLabel.getText().toString() + " - " + endTimeLabel.getText().toString(),
							   selectedYear + "-" + selectedMonth + "-" + selectedDay);
	}
}
