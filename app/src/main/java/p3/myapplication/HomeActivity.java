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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

	FirebaseAuth mAuth;
	FirebaseUser currentUser;
	DatabaseReference reference;

	TextView welcomeMessage;
	Button createMeeting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mAuth = FirebaseAuth.getInstance();
		currentUser = mAuth.getCurrentUser();
		reference = FirebaseDatabase.getInstance().getReference();

		BottomNavigationView navigation = findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				Intentions intentions = new Intentions(HomeActivity.this);
				switch (item.getItemId()) {
					case R.id.action_messages: {
						intentions.goToMessages();
						return true;
					}
					case R.id.action_profile: {
						mAuth.signOut();
						intentions.goToSignIn();
						return true;
					}
				}
				return false;
			}
		});

		welcomeMessage = findViewById(R.id.welcomeMessage);
		createMeeting = findViewById(R.id.createMeeting);

		showTitle();

		createMeeting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				goToCreateMeeting();
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

		reference.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

	public void goToCreateMeeting () {
		Intent i = new Intent(this, ModuleListActivity.class);
		startActivity(i);
	}
}