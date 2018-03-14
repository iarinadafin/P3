package p3.myapplication;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ModuleMeetingsActivity extends AppCompatActivity {

	FirebaseAuth mAuth;
	FirebaseUser currentUser;
	DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("meetings");

	String module;

	TextView moduleMeetingsTitle;
	ListView moduleMeetingsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_module_meetings);

		mAuth = FirebaseAuth.getInstance();
		currentUser = mAuth.getCurrentUser();

		BottomNavigationView navigation = findViewById(R.id.navigationModuleList);
		navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				return new Intentions(ModuleMeetingsActivity.this).chooseMenuItem(item);
			}
		});
		navigation.getMenu().findItem(navigation.getSelectedItemId()).setCheckable(false);

		moduleMeetingsTitle = findViewById(R.id.moduleMeetingsTitle);
		moduleMeetingsList = findViewById(R.id.moduleMeetingsList);

		module = getIntent().getExtras().getString("p3.myapplication:module");
		moduleMeetingsTitle.setText(String.format(getResources().getString(R.string.module_meetings_label), module));

		showModuleMeetings();
	}

	public void showModuleMeetings () {

		reference.child(module).addValueEventListener(new ValueEventListener() {
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
		List<String[]> list = new ArrayList<>();

		for (DataSnapshot data : dataSnapshot.getChildren()) {
			TextView message = findViewById(R.id.noMeetingsLabel);
			message.setVisibility(View.GONE);

			// sends all meeting details
			String [] meetingDetails = {data.child("name").getValue(String.class), data.child("start").getValue(String.class), data.child("end").getValue(String.class), String.valueOf(data.child("members").getChildrenCount()), data.getKey()};
			list.add(meetingDetails);
		}

		CustomArrayAdapter adapter = new CustomArrayAdapter(0, this, list);
		moduleMeetingsList.setAdapter(adapter);
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