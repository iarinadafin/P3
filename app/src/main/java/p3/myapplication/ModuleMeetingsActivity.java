package p3.myapplication;

import android.content.Intent;
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

@SuppressWarnings("ConstantConditions")
public class ModuleMeetingsActivity extends AppCompatActivity {

	FirebaseAuth mAuth;
	FirebaseUser currentUser;
	DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

	String module;

	TextView moduleMeetingsTitle;
	ListView moduleMeetingsList;
	Button createNewMeeting;
	TextView message;

	MeetingDetailsArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_module_meetings);

		mAuth = FirebaseAuth.getInstance();
		currentUser = mAuth.getCurrentUser();

		BottomNavigationView navigation = findViewById(R.id.navigationModuleMeetings);
		navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				return new Intentions(ModuleMeetingsActivity.this).chooseMenuItem(item);
			}
		});
		navigation.getMenu().findItem(navigation.getSelectedItemId()).setCheckable(false);

		moduleMeetingsTitle = findViewById(R.id.moduleMeetingsTitle);
		moduleMeetingsList = findViewById(R.id.moduleMeetingsList);
		message = findViewById(R.id.noMeetingsLabelModuleMeetings);

		module = getIntent().getExtras().getString("p3.myapplication:module");
		moduleMeetingsTitle.setText(String.format(getResources().getString(R.string.module_meetings_label), module));

		showModuleMeetings();

		createNewMeeting = findViewById(R.id.addMeetingCreate);
		createNewMeeting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				goToCreateNewMeeting();
			}
		});
	}

	public void showModuleMeetings () {

		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				try {
					showData(dataSnapshot);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.d("mytag", "realtimeDatabase:error");
			}
		});
	}

	public void showData (DataSnapshot dataSnapshot) throws ParseException {
		List<String[]> list = new ArrayList<>();

		// iterates through all meetings
		for (DataSnapshot data : dataSnapshot.child("meetings").getChildren()) {
			// filters through all meetings and only selects the chosen module's meetings
			if (data.child("module").getValue(String.class).equals(module)) {

				// if meeting still exists
				if (data.exists()) {
					// checks if current user is a member of this meeting
					Boolean isMember = false;
					if (data.child("members/" + mAuth.getCurrentUser().getUid()).exists())
						isMember = true;
					// gets the number of participants in the meeting
					String noOfParticipants = String.valueOf(data.child("members").getChildrenCount());
					// gets meeting end date
					Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).parse(data.child("endDate").getValue(String.class));
					// gets current timestamp
					Calendar calendar = Calendar.getInstance(Locale.UK);
					calendar.add(Calendar.HOUR_OF_DAY, 1);

					// check if the meeting has only the current user as sole participant and if the endTime has passed
					if (Integer.parseInt(noOfParticipants) == 1 && endDate.before(calendar.getTime())) {
						// remove meeting from database
						if (!mAuth.getCurrentUser().getUid().isEmpty())
							new Intentions(this).deleteMeeting(mAuth.getCurrentUser().getUid(), reference, dataSnapshot, data.getKey());
					} else {
						// sends all meeting details
						String[] meetingDetails = {data.child("name").getValue(String.class), // name of meeting [0]
								data.child("startDate").getValue(String.class), // start timestamp of meeting [1]
								data.child("endDate").getValue(String.class), // end timestamp of meeting [2]
								noOfParticipants, // number of members in the meeting [3]
								data.getKey(), // key of meeting database reference [4]
								module, // name of the meeting's module [5]
								isMember.toString(), // if current user is a member [6]
								mAuth.getCurrentUser().getUid(), // current user id [7]
								dataSnapshot.child("users/" + mAuth.getCurrentUser().getUid() + "/firstName").getValue(String.class)}; // first name of current user [8]
						list.add(meetingDetails);
					}
				}
			}

		}

		adapter = new MeetingDetailsArrayAdapter(0, this, list);
		moduleMeetingsList.setAdapter(adapter);

		// if listview is empty, show appropriate message
		if (list.size() == 0)
			message.setVisibility(View.VISIBLE);
		else
			message.setVisibility(View.GONE);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();

		// if adapter has been changed since creating the activity and if it has been emptied
		// used to show a message if no meetings became available ever since the user started the application
		if (adapter != null && adapter.isEmpty())
			message.setVisibility(View.VISIBLE);
	}

	void goToCreateNewMeeting () {
		Intent i = new Intent(this, CreateMeetingActivity.class);
		i.putExtra("p3.myapplication:module_name_list", module);
		startActivity(i);
	}
}