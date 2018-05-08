package p3.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import p3.myapplication.ArrayAdapters.LeaderboardItem;

@SuppressWarnings("ConstantConditions")
public class UserProfileActivity extends AppCompatActivity {

	DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

	TextView profileTitle;
	Button logout;
	TextView name;
	TextView course;
	TextView year;
	TextView email;
	RatingBar ratingBar;
	TextView ratingNumber;
	TextView ratingDescription;
	TextView level;
	SeekBar scoreBar;
	TextView minBoundary;
	TextView maxBoundary;
	TextView pointsLeft;
	LinearLayout scorePanel;
	Button leaderboard;

	Helper helper = new Helper(this);
	String userUid;
	Boolean isOwnProfile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);

		profileTitle = findViewById(R.id.profileTitle);
		logout = findViewById(R.id.logoutButtonProfile);
		name = findViewById(R.id.nameLabelProfile);
		course = findViewById(R.id.courseLabelProfile);
		year = findViewById(R.id.yearLabelProfile);
		email = findViewById(R.id.emailLabelProfile);
		ratingBar = findViewById(R.id.ratingProfile);
		ratingNumber = findViewById(R.id.ratingNumbersProfile);
		ratingDescription = findViewById(R.id.ratingDescription);
		level = findViewById(R.id.levelLabelProfile);
		scoreBar = findViewById(R.id.scoreProgress);
		minBoundary = findViewById(R.id.minScoreBoundary);
		maxBoundary = findViewById(R.id.maxScoreBoundary);
		pointsLeft = findViewById(R.id.pointsLeft);
		scorePanel = findViewById(R.id.scorePanel);
		leaderboard = findViewById(R.id.leaderboardButton);

		userUid = getIntent().getExtras().getString("p3.myapplication:userID");
		isOwnProfile = Boolean.parseBoolean(getIntent().getExtras().getString("p3.myapplication:isOwnProfile"));

		// set navbar behaviour
		BottomNavigationView navigation = findViewById(R.id.navigationProfile);
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
				}
				return false;
			}
		});

		String referenceString;
		if (isOwnProfile) {
			// selects the 'profile' nav button if own profile
			navigation.getMenu().getItem(2).setChecked(true);

			// if the user is trying to access their own profile
			referenceString = "users/" + FirebaseAuth.getInstance().getCurrentUser().getUid();

			// logout is only available to the profile of the current user
			logout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					FirebaseAuth.getInstance().signOut();
					helper.goToSignIn(false);
				}
			});

			// goes to leaderboard activity
			leaderboard.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					goToLeaderboard();
				}
			});
		}
		else {
			navigation.getMenu().findItem(navigation.getSelectedItemId()).setCheckable(false);

			referenceString = "users/" + userUid;

			// makes the logout button, rating description and score panel inaccessible
			logout.setVisibility(View.GONE);
			ratingDescription.setVisibility(View.GONE);
			scorePanel.setVisibility(View.GONE);
		}

		// disables score seekbar touch
		scoreBar.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		reference.child(referenceString).addValueEventListener(new ValueEventListener() {
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
		if (!isOwnProfile) {
			// sets custom title
			String title = String.format(getResources().getString(R.string.user_profile_title), dataSnapshot.child("firstName").getValue(String.class));
			profileTitle.setText(title);
		}
		// sets user details
		String fullName = String.format(getResources().getString(R.string.full_name),
										dataSnapshot.child("firstName").getValue(String.class),
										dataSnapshot.child("lastName").getValue(String.class));
		name.setText(fullName);
		course.setText(dataSnapshot.child("course").getValue(String.class));
		year.setText(dataSnapshot.child("year").getValue(String.class));
		email.setText(dataSnapshot.child("email").getValue(String.class));

		// sets user rating
		float meanRating = helper.getRating(dataSnapshot);
		ratingBar.setRating(meanRating);
		ratingNumber.setText(String.format(java.util.Locale.US,"%.1f", meanRating));

		// sets user score and level
		int score = Integer.parseInt(dataSnapshot.child("score").getValue(String.class));
		Pair<Integer, Pair<Integer, Integer>> scoreInformation = getLevel(score);
		String levelLabel = String.format(getResources().getString(R.string.level_label), scoreInformation.first, score);
		level.setText(levelLabel);

		if (isOwnProfile) {

			scoreBar.setMax(scoreInformation.second.second - scoreInformation.second.first);
			scoreBar.setProgress(score - scoreInformation.second.first);
			minBoundary.setText(String.valueOf(scoreInformation.second.first));
			maxBoundary.setText(String.valueOf(scoreInformation.second.second));

			// sets the remaining points
			String points = String.format(getResources().getString(R.string.points_left_label), scoreInformation.second.second - score);
			pointsLeft.setText(points);
		}
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

	/**
	 * Starts the leaderboard activity
	 */
	void goToLeaderboard () {
		Intent i = new Intent(this, LeaderboardActivity.class);
		startActivity(i);
	}
}