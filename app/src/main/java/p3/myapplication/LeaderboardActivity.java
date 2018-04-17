package p3.myapplication;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import p3.myapplication.ArrayAdapters.LeaderboardItem;

public class LeaderboardActivity extends AppCompatActivity {

	DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
	FirebaseAuth mAuth = FirebaseAuth.getInstance();

	ListView leaderboard;

	ArrayList<String[]> leaderboardContent = new ArrayList<>();
	LeaderboardItem adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaderboard);

		BottomNavigationView navigation = findViewById(R.id.navigationLeaderboard);
		navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				Helper helper = new Helper(LeaderboardActivity.this);
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
						helper.goToProfile(true, mAuth.getCurrentUser().getUid());
						return false;
					}
				}
				return false;
			}
		});
		navigation.getMenu().findItem(navigation.getSelectedItemId()).setCheckable(false);

		leaderboard = findViewById(R.id.leaderboard);

		Query query = reference.child("users").orderByChild("score");
		query.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				showData(dataSnapshot);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});

	}

	public void showData (DataSnapshot dataSnapshot) {
		for (DataSnapshot user : dataSnapshot.getChildren()) {
			leaderboardContent.add(new String[]{user.child("score").getValue(String.class), // user's score [0]
												user.child("firstName").getValue(String.class), // user's first name [1]
												user.child("lastName").getValue(String.class), // user's last name [2]
												user.getKey()}); // user's key [3]
		}

		// used to populate the listview in desc order
		Collections.reverse(leaderboardContent);

		adapter = new LeaderboardItem(0, this, leaderboardContent);
		leaderboard.setAdapter(adapter);

		// waits for leaderboard to populate
		leaderboard.post(new Runnable() {
			@Override
			public void run() {
				int score;
				int prev = 0;
				int place = 1;

				for (int i=0; i<adapter.getCount(); i++) {
					View listItem = leaderboard.getChildAt(i);

					TextView textView = listItem.findViewById(R.id.leaderboardScore);
					TextView placeView = listItem.findViewById(R.id.place);

					if (i == 0)
						prev = Integer.parseInt(textView.getText().toString().replaceAll("\\D+",""));

					score = Integer.parseInt(textView.getText().toString().replaceAll("\\D+",""));

					if (score == prev)
						placeView.setText(String.valueOf(place));
					else {
						place++;
						placeView.setText(String.valueOf(place));
						prev = score;
					}

					if (place == 1)
						placeView.setTextColor(getResources().getColor(R.color.colorAccent));
				}
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
}
