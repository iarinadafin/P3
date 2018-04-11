package p3.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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

@SuppressWarnings("ConstantConditions")
public class ViewMeetingActivity extends AppCompatActivity {

	DatabaseReference reference;

	TextView titleLabel;
	TextView moduleLabel;
	Button messageButton;
	ListView participantsList;
	TextView date;
	TextView time;
	Button leaveMeeting;
	Button joinMeeting;
	Button endMeeting;
	LinearLayout manageMeetingPanel;

	String participantName;
	String referenceString;
	String meetingID;
	Intentions helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_meeting);

		reference = FirebaseDatabase.getInstance().getReference();

		meetingID = getIntent().getExtras().getString("p3.myapplication:meeting_id");
		referenceString = "meetings/" + meetingID;
		helper = new Intentions(ViewMeetingActivity.this);

		titleLabel = findViewById(R.id.meetingTitleView);
		moduleLabel = findViewById(R.id.meetingModuleView);
		messageButton = findViewById(R.id.messageButtonView);
		participantsList = findViewById(R.id.participantsListView);
		date = findViewById(R.id.dateView);
		time = findViewById(R.id.timeView);
		leaveMeeting = findViewById(R.id.leaveMeetingButtonView);
		joinMeeting = findViewById(R.id.joinMeetingButtonView);
		endMeeting = findViewById(R.id.endMeetingButtonView);
		manageMeetingPanel = findViewById(R.id.manageMeetingPanel);

		// by default, the end meeting option is not visible
		endMeeting.setVisibility(View.GONE);

		BottomNavigationView navigation = findViewById(R.id.navigationViewMeeting);
		navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch (item.getItemId()) {
					case R.id.action_home: {
						helper.goHome();
						return false;
					}
					case R.id.action_messages: {
						helper.goToMessages();
						return false;
					}
					case R.id.action_profile: {
						helper.goToProfile();
						return false;
					}
				}
				return false;
			}
		});

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

	public void showData (final DataSnapshot dataSnapshot) throws ParseException {
		titleLabel.setText(dataSnapshot.child(referenceString + "/name").getValue(String.class));
		moduleLabel.setText(getIntent().getExtras().getString("p3.myapplication:module_id"));
		date.setText(getIntent().getExtras().getString("p3.myapplication:date"));
		time.setText(getIntent().getExtras().getString("p3.myapplication:hours"));

		// gets all the participants in /[meeting]/members and looks for the matching key information in /users
		List<String> values = new ArrayList<>();
		for (DataSnapshot data : dataSnapshot.child(referenceString + "/members").getChildren()) {
			DataSnapshot user = dataSnapshot.child("users/" + data.getKey());
			participantName = user.child("firstName").getValue(String.class) + " " + user.child("lastName").getValue(String.class);
			values.add(participantName);
		}

		// if meeting still exists
		if (dataSnapshot.child("meetings/" + meetingID).exists()) {
			// customises if user is participant
			if (dataSnapshot.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/meetings/" + meetingID).exists()) {

				// calculates the current date and time
				Calendar calendar = Calendar.getInstance(Locale.UK);
				calendar.add(Calendar.HOUR_OF_DAY, 1);
				// gets the end time for the meeting
				Date meetingEndDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).parse(dataSnapshot.child("meetings/" + meetingID + "/endDate").getValue(String.class));

				// checks if the meeting has more than one participant and if the endTime for the meeting has passed
				// in which case, it will block the meeting management and leave meeting options in the favour of the end meeting button
					// this way, the user will be encouraged to give feedback
				if (dataSnapshot.child("meetings/" + meetingID + "/members").getChildrenCount() > 1 && meetingEndDate.before(calendar.getTime())) {
					manageMeetingPanel.setVisibility(View.GONE);
					leaveMeeting.setVisibility(View.GONE);
					joinMeeting.setVisibility(View.GONE);
					endMeeting.setVisibility(View.VISIBLE);

					endMeeting.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							helper.goToRating(meetingID, dataSnapshot.child("meetings/" + meetingID + "/name").getValue(String.class));
						}
					});
				}
				// if meeting has one participant, and the endTime has passed
				// the meeting will be cancelled automatically
				else if (dataSnapshot.child("meetings/" + meetingID + "/members").getChildrenCount() == 1 && meetingEndDate.before(calendar.getTime())) {
					helper.deleteMeeting(meetingID, reference, dataSnapshot, meetingID);
					// redirects the user to the home page
					helper.goHome();
				}
				// otherwise
				else {
					manageMeetingPanel.setVisibility(View.VISIBLE);
					leaveMeeting.setVisibility(View.VISIBLE);
					joinMeeting.setVisibility(View.GONE);

					leaveMeeting.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							goToModuleMeetings();

							reference.addListenerForSingleValueEvent(new ValueEventListener() {
								@Override
								public void onDataChange(DataSnapshot dataSnapshot) {
									// todo: POTENTIAL BUG ?????
									// if current user is the only participant
									if (dataSnapshot.child("meetings/" + meetingID + "/members").getChildrenCount() <= 1) {
										helper.deleteMeeting(FirebaseAuth.getInstance().getCurrentUser().getUid(), reference, dataSnapshot, meetingID);
										// subtract 10 points if meeting is left
										reference.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/score")
												.setValue(Integer.parseInt(dataSnapshot.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/score").getValue(String.class)) - 10);
										// redirects the user to the home page
										helper.goHome();
									}
									else
										removeUserFromMeeting(meetingID, dataSnapshot);
								}

								@Override
								public void onCancelled(DatabaseError databaseError) {

								}
							});
						}
					});

					messageButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							helper.goToChat(meetingID, titleLabel.getText().toString());
						}
					});
				}
			}
			// customises if user is not participant
			else {
				manageMeetingPanel.setVisibility(View.GONE);
				leaveMeeting.setVisibility(View.GONE);
				joinMeeting.setVisibility(View.VISIBLE);

				joinMeeting.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						reference.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/meetings/" + meetingID).setValue("true");
						reference.child("meetings/" + meetingID + "/members/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("true");

						Calendar calendar = Calendar.getInstance(Locale.UK);
						calendar.add(Calendar.HOUR_OF_DAY, 1);
						reference.child("chats/" + meetingID).push().setValue(new Message(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK).format(calendar.getTime()),
								"system",
								dataSnapshot.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/firstName").getValue(String.class) + " joined"));

						manageMeetingPanel.setVisibility(View.VISIBLE);
						leaveMeeting.setVisibility(View.VISIBLE);
						joinMeeting.setVisibility(View.GONE);
					}
				});
			}
		}


		ArrayAdapter<String> participantListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
		participantsList.setAdapter(participantListAdapter);
	}

	// todo: duplicate intention
	public void goToModuleMeetings () {
		Intent i = new Intent(this, ModuleMeetingsActivity.class);
		i.putExtra("p3.myapplication:module", moduleLabel.getText().toString());
		startActivity(i);
	}

	// removes user from meeting
	public void removeUserFromMeeting (final String meetingID, DataSnapshot dataSnapshot) {
		// removes meeting reference from [user]
		reference.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/meetings/" + meetingID).removeValue();

		reference.child("meetings/" + meetingID + "/members/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
		// adds system message that the user left the meeting
		Calendar calendar = Calendar.getInstance(Locale.UK);
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		reference.child("chats/" + meetingID).push().setValue(new Message(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK).format(calendar.getTime()),
																		"system",
																		dataSnapshot.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/firstName").getValue(String.class) + " left"));
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
}
