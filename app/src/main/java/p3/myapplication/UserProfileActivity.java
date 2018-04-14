package p3.myapplication;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@SuppressWarnings("ConstantConditions")
public class UserProfileActivity extends AppCompatActivity {

	DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

	Button logout;
	TextView name;
	TextView course;
	TextView year;
	TextView email;
	RatingBar ratingBar;
	TextView ratingNumber;
	TextView level;
	SeekBar scoreBar;
	TextView minBoundary;
	TextView maxBoundary;
	TextView pointsLeft;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);

		logout = findViewById(R.id.logoutButtonProfile);
		name = findViewById(R.id.nameLabelProfile);
		course = findViewById(R.id.courseLabelProfile);
		year = findViewById(R.id.yearLabelProfile);
		email = findViewById(R.id.emailLabelProfile);
		ratingBar = findViewById(R.id.ratingProfile);
		ratingNumber = findViewById(R.id.ratingNumbersProfile);
		level = findViewById(R.id.levelLabelProfile);
		scoreBar = findViewById(R.id.scoreProgress);
		minBoundary = findViewById(R.id.minScoreBoundary);
		maxBoundary = findViewById(R.id.maxScoreBoundary);
		pointsLeft = findViewById(R.id.pointsLeft);

		// set navbar behaviour
		BottomNavigationView navigation = findViewById(R.id.navigationChatsList);
		navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				Helper helper = new Helper(UserProfileActivity.this);
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
		navigation.getMenu().getItem(2).setCheckable(true);

		// disables seekbar touch
		scoreBar.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		reference.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				showData(dataSnapshot);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});

		logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FirebaseAuth.getInstance().signOut();
				goToSignIn();
			}
		});
	}

	public void showData (DataSnapshot dataSnapshot) {
		// sets user details
		String fullName = String.format(getResources().getString(R.string.full_name),
										dataSnapshot.child("firstName").getValue(String.class),
										dataSnapshot.child("lastName").getValue(String.class));
		name.setText(fullName);
		course.setText(dataSnapshot.child("course").getValue(String.class));
		year.setText(dataSnapshot.child("year").getValue(String.class));
		email.setText(dataSnapshot.child("email").getValue(String.class));

		// sets user rating
		float meanRating = new Helper(this).getRating(dataSnapshot);
		ratingBar.setRating(meanRating);
		ratingNumber.setText(String.format(java.util.Locale.US,"%.1f", meanRating));

		// sets user score and level
		int score = Integer.parseInt(dataSnapshot.child("score").getValue(String.class));
		Pair<Integer, Pair<Integer, Integer>> scoreInformation = getLevel(score);
		String levelLabel = String.format(getResources().getString(R.string.level_label), scoreInformation.first, score);
		level.setText(levelLabel);
		scoreBar.setMax(scoreInformation.second.second - scoreInformation.second.first);
		scoreBar.setProgress(score - scoreInformation.second.first);
		minBoundary.setText(String.valueOf(scoreInformation.second.first));
		maxBoundary.setText(String.valueOf(scoreInformation.second.second));

		// sets the remaining points
		String points = String.format(getResources().getString(R.string.points_left_label), scoreInformation.second.second - score);
		pointsLeft.setText(points);
	}

	public void goToSignIn () {
		new Helper(this).goToSignIn();
	}

	/**
	 * Gets the score of the user and returns the right level according to it.
	 * @param score the number of experience points that the user obtained
	 * @return a pair object with: the user's level and another pair containing the minimum and maximum level points boundaries
	 */
	public Pair<Integer, Pair<Integer, Integer>> getLevel (int score) {
		if (score < 10)
			return new Pair<>(1, new Pair<>(0, 10));
		else if (score >= 10 && score < 20)
			return new Pair<>(2, new Pair<>(10, 20));
		else if (score >= 20 && score < 40)
			return new Pair<>(3, new Pair<>(20, 40));
		else if (score >= 40 && score < 70)
			return new Pair<>(4, new Pair<>(40, 70));
		else if (score >= 70 && score < 100)
			return new Pair<>(5, new Pair<>(70, 100));
		else return new Pair<>(6, new Pair<>(100, 150));
	}
}