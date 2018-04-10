package p3.myapplication;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class HomeActivity extends AppCompatActivity {

	FirebaseAuth mAuth;
	DatabaseReference reference;

	TextView welcomeMessage;
	Button createMeeting;
	ListView userMeetings;
	TextView message;

	MeetingDetailsArrayAdapter adapter;
	List<String[]> userMeetingsList = new ArrayList<>();
	String currentUid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mAuth = FirebaseAuth.getInstance();
		currentUid = mAuth.getCurrentUser().getUid();
		reference = FirebaseDatabase.getInstance().getReference();

		BottomNavigationView navigation = findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				Intentions intentions = new Intentions(HomeActivity.this);
				switch (item.getItemId()) {
					case R.id.action_messages: {
						intentions.goToMessages();
						return false;
					}
					case R.id.action_profile: {
						mAuth.signOut();
						intentions.goToSignIn();
						return false;
					}
				}
				return false;
			}
		});

		welcomeMessage = findViewById(R.id.welcomeMessage);
		createMeeting = findViewById(R.id.createMeeting);
		userMeetings = findViewById(R.id.userMeetingsList);
		message = findViewById(R.id.noMeetingsLabelHome);

		showTitle();

		createMeeting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				goToCreateMeeting();
			}
		});

		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				try {
					showUserMeetings(dataSnapshot);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public void showTitle () {

		reference.child("users").child(currentUid).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				showData(dataSnapshot);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.d("mytag", "realtimeDatabase:no_connection");
			}
		});
	}

	@SuppressWarnings("ConstantConditions")
	public void showData (DataSnapshot dataSnapshot) {
		String message = String.format(getResources().getString(R.string.welcome_message), dataSnapshot.child("firstName").getValue(String.class));
		welcomeMessage.setText(message);
	}

	public void showUserMeetings (DataSnapshot dataSnapshot) throws ParseException {
		userMeetingsList.clear();

		// if user joined any meetings
		if (dataSnapshot.child("users/" + currentUid + "/meetings").getChildrenCount() > 0) {
			for (DataSnapshot userMeeting : dataSnapshot.child("users/" + currentUid + "/meetings").getChildren()) {
				// check if meeting still exists
				if (dataSnapshot.child("meetings/" + userMeeting.getKey()).exists()) {
					// gets meeting end date
					Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK)
							.parse(dataSnapshot.child("meetings/" + userMeeting.getKey() + "/endDate").getValue(String.class));
					// gets current timestamp
					Calendar calendar = Calendar.getInstance(Locale.UK);
					calendar.add(Calendar.HOUR_OF_DAY, 1);

					// check if the meeting has only the current user as sole participant and if the endTime has passed
					if (dataSnapshot.child("meetings/" + userMeeting.getKey() + "/members").getChildrenCount() == 1 && endDate.before(calendar.getTime())) {
						// remove meeting from database
						if (mAuth.getCurrentUser() != null)
							new Intentions(this).deleteMeeting(mAuth.getCurrentUser().getUid(), reference, dataSnapshot, userMeeting.getKey());
					} else {
						// sends all meeting details to the listview
						String[] meetingDetails = {dataSnapshot.child("meetings/" + userMeeting.getKey() + "/name").getValue(String.class), // name of meeting [0]
								dataSnapshot.child("meetings/" + userMeeting.getKey() + "/startDate").getValue(String.class), // start timestamp of meeting [1]
								dataSnapshot.child("meetings/" + userMeeting.getKey() + "/endDate").getValue(String.class), // end timestamp of meeting [2]
								String.valueOf(dataSnapshot.child("meetings/" + userMeeting.getKey() + "/members").getChildrenCount()), // number of members in the meeting [3]
								userMeeting.getKey(), // key of meeting database reference [4]
								dataSnapshot.child("meetings/" + userMeeting.getKey() + "/module").getValue(String.class), // name of the meeting's module [5]
								String.valueOf(true), // if current user is a member [6]
								currentUid, // current user id [7]
								dataSnapshot.child("users/" + currentUid + "/firstName").getValue(String.class)}; // first name of current user [8]
						userMeetingsList.add(meetingDetails);
					}
				}
			}
			// adds all meetings to the adapter and then into the listview
			adapter = new MeetingDetailsArrayAdapter(0, this, userMeetingsList);
			userMeetings.setAdapter(adapter);
			// if listview is empty, show appropriate message
			if (userMeetingsList.size() == 0)
				message.setVisibility(View.VISIBLE);
			else
				message.setVisibility(View.GONE);
		}
	}

	public void goToCreateMeeting () {
		Intent i = new Intent(this, ModuleListActivity.class);
		startActivity(i);
	}
}