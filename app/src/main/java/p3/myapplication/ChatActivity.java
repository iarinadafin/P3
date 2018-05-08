package p3.myapplication;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import p3.myapplication.ArrayAdapters.ChatMessageArrayAdapter;
import p3.myapplication.Model.Message;

public class ChatActivity extends AppCompatActivity {

	DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

	ListView chatMessages;
	Button viewMeeting;
	FloatingActionButton sendMessage;
	TextView title;

	String meetingID;
	List<String[]> messageDetails = new ArrayList<>();

	@SuppressWarnings("ConstantConditions")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		meetingID = getIntent().getExtras().getString("p3.myapplication:meeting_id");

		sendMessage = findViewById(R.id.sendMessageButton);
		title = findViewById(R.id.chatTitleLabel);
		chatMessages = findViewById(R.id.chatMessages);
		viewMeeting = findViewById(R.id.viewMeetingChat);

		title.setText(getIntent().getExtras().getString("p3.myapplication:meeting_name"));

		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				// clears the adapter that contains all messages at database data change
				messageDetails.clear();

				for (DataSnapshot message : dataSnapshot.child("chats/" + meetingID).getChildren())
					// adds the message details into the array adapter item
					messageDetails.add(new String[] {
										dataSnapshot.child("users/" + message.child("userID").getValue(String.class) + "/firstName").getValue(String.class), // first name of the message sender [0]
										message.child("content").getValue(String.class), // message content [1]
										message.child("timestamp").getValue(String.class).substring(0, 16), // message timestamp [2]
										String.valueOf(message.child("userID").getValue(String.class).equals("system")), // if the sender is the system [3]
										message.child("userID").getValue(String.class)}); // sender ID [4]
				showMessages();
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				throw databaseError.toException();
			}
		});

		// sets the listener for sending the message
		sendMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				EditText input = findViewById(R.id.input);

				// checks that the message is not empty
				if (!input.getText().toString().equals("")) {
					// gets the current date
					Calendar calendar = Calendar.getInstance(Locale.UK);
					calendar.add(Calendar.HOUR_OF_DAY, 1);

					// gets the chat reference and pushes the message to the database location
					reference.child("chats/" + meetingID).push()
							.setValue(new Message(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK).format(calendar.getTime()),
												  FirebaseAuth.getInstance().getCurrentUser().getUid(),
												  input.getText().toString()));

					// clear the input
					input.setText("");
				}
			}
		});

		// sets the click listener for the view meeting button
		viewMeeting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				reference.child("meetings/" + meetingID).addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						goToViewMeeting(dataSnapshot);
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}
				});

			}
		});
	}

	@Override
	protected void onStart () {
		super.onStart();
	}

	@Override
	protected void onStop () {
		super.onStop();
	}

	/**
	 * Adds the adapter to the ListView and displays the chats.
	 */
	void showMessages () {
		ChatMessageArrayAdapter adapter = new ChatMessageArrayAdapter(0, this, messageDetails);
		chatMessages.setAdapter(adapter);
	}

	/**
	 * Triggered at the "View meeting" button press, prepares the meeting data and starts the view meeting activity
	 * @param dataSnapshot the DataSnapshot of the meeting database location
	 */
	@SuppressWarnings("ConstantConditions")
	void goToViewMeeting (DataSnapshot dataSnapshot) {
		// separate start and end times from start and end dates
		String[] startTokens = dataSnapshot.child("startDate").getValue(String.class).split("[-|\\s]");
		String[] endTokens = dataSnapshot.child("endDate").getValue(String.class).split("[-|\\s]");

		// format string into date
		Date startDate = new Date();
		try {
			startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).parse(dataSnapshot.child("startDate").getValue(String.class));
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		// set hours string as startHour:startMinute - endHour:endMinute
		final String hoursString = startTokens[3] + " - " + endTokens[3];
		// sets the date string resource in the [dayOfTheWeek, month day, year] format
		final String dateString = getResources().getString(R.string.date_field, // date resource
				new SimpleDateFormat("EEE", Locale.UK).format(startDate), // friendly short day of week
				new SimpleDateFormat("MMM", Locale.UK).format(startDate), // friendly short month
				new SimpleDateFormat("dd", Locale.UK).format(startDate), // day of month
				new SimpleDateFormat("yyyy", Locale.UK).format(startDate)); // year
		new Helper(this).goToViewMeeting(meetingID, dataSnapshot.child("module").getValue(String.class), dateString, hoursString, dataSnapshot.child("startDate").getValue(String.class).split(" ")[0]);
	}
}