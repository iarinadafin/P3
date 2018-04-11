package p3.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
		pointsLeft = findViewById(R.id.pointsLeft);

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
		String fullName = String.format(getResources().getString(R.string.full_name),
										dataSnapshot.child("firstName").getValue(String.class),
										dataSnapshot.child("lastName").getValue(String.class));
		name.setText(fullName);
		course.setText(dataSnapshot.child("course").getValue(String.class));
		year.setText(dataSnapshot.child("year").getValue(String.class));
		email.setText(dataSnapshot.child("email").getValue(String.class));

		float meanRating = Float.valueOf(dataSnapshot.child("totalScore").getValue(String.class)) / Float.valueOf(dataSnapshot.child("numberOfRatings").getValue(String.class));
		ratingBar.setRating(meanRating);
		ratingNumber.setText(String.format(java.util.Locale.US,"%.1f", meanRating));

	}

	public void goToSignIn () {
		new Intentions(this).goToSignIn();
	}
}
