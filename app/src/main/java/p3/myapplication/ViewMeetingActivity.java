package p3.myapplication;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class ViewMeetingActivity extends AppCompatActivity {

	DatabaseReference reference;

	TextView titleLabel;
	TextView moduleLabel;
	Button messageOrJoinButton;
	ListView participantsList;
	TextView date;
	TextView time;

	String participantName;
	String referenceString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_meeting);

		reference = FirebaseDatabase.getInstance().getReference();

		referenceString = "meetings/"
				+ getIntent().getExtras().getString("p3.myapplication:module_id") + "/"
				+ getIntent().getExtras().getString("p3.myapplication:meeting_id");

		titleLabel = findViewById(R.id.meetingTitleView);
		moduleLabel = findViewById(R.id.meetingModuleView);
		messageOrJoinButton = findViewById(R.id.messageJoinButtonView);
		participantsList = findViewById(R.id.participantsListView);
		date = findViewById(R.id.dateView);
		time = findViewById(R.id.timeView);

		BottomNavigationView navigation = findViewById(R.id.navigationViewMeeting);
		navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				Intentions intentions = new Intentions(ViewMeetingActivity.this);
				switch (item.getItemId()) {
					case R.id.action_messages: {
						intentions.goToMessages();
						return true;
					}
					case R.id.action_profile: {
						intentions.goToProfile();
						return true;
					}
				}
				return false;
			}
		});

		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				showData(dataSnapshot);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.d("mytag", "realtimeDatabase:error");
			}
		});
	}

	public void showData (DataSnapshot dataSnapshot) {
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

		ArrayAdapter<String> participantListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
		participantsList.setAdapter(participantListAdapter);
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
