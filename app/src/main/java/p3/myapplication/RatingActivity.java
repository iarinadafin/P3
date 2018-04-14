package p3.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import p3.myapplication.ArrayAdapters.RatingArrayAdapter;

@SuppressWarnings("ConstantConditions")
public class RatingActivity extends AppCompatActivity {

	DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
	FirebaseAuth mAuth = FirebaseAuth.getInstance();

	TextView meetingTitle;
	ListView userRatings;
	Button submitRatings;

	RatingArrayAdapter adapter;
	List<String[]> userInformationList = new ArrayList<>();
	String meetingID;
	Helper helper = new Helper(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rating);

		meetingTitle = findViewById(R.id.meetingTitleRating);
		userRatings = findViewById(R.id.listOfRatings);
		submitRatings = findViewById(R.id.submitRatings);

		meetingTitle.setText(getIntent().getExtras().getString("p3.myapplication:meeting_name"));

		meetingID = getIntent().getExtras().getString("p3.myapplication:meeting_id");

		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				showData(dataSnapshot);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}

	public void showData (final DataSnapshot dataSnapshot) {
		for (DataSnapshot data : dataSnapshot.child("meetings/" + meetingID + "/members").getChildren())
			// if the participant is not the current user
			if (!data.getKey().equals(mAuth.getCurrentUser().getUid())) {
				// used to construct the participant's full name
				String firstName = dataSnapshot.child("users/" + data.getKey() + "/firstName").getValue(String.class);
				String lastName = dataSnapshot.child("users/" + data.getKey() + "/lastName").getValue(String.class);
				userInformationList.add(new String[]{firstName + " " + lastName, data.getKey()});
			}

		adapter = new RatingArrayAdapter(0, this, userInformationList);
		userRatings.setAdapter(adapter);

		submitRatings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// iterate through listview items -> through user ratings
				for (int i=0; i<adapter.getCount(); i++) {
					View listItem = userRatings.getChildAt(i);
					CheckBox checkBox = listItem.findViewById(R.id.absenceCheckbox);

					// if participant was not absent
					if (!checkBox.isChecked()) {
						RatingBar ratingBar = listItem.findViewById(R.id.ratingBar);
						int rating = Math.round(ratingBar.getRating());

						// gets total score and number of ratings of participant; calculates new values and assigns them
						int totalScore = Integer.parseInt(dataSnapshot.child("users/" + userInformationList.get(i)[1] + "/totalScore").getValue(String.class)) + rating;
						int noOfRatings = Integer.parseInt(dataSnapshot.child("users/" + userInformationList.get(i)[1] + "/numberOfRatings").getValue(String.class)) + 1;
						// pushes new values in the database
						reference.child("users/" + userInformationList.get(i)[1] + "/totalScore").setValue(String.valueOf(totalScore));
						reference.child("users/" + userInformationList.get(i)[1] + "/numberOfRatings").setValue(String.valueOf(noOfRatings));
					}
				}

				// removes meeting reference from the user database record
				reference.child("users/" + mAuth.getCurrentUser().getUid() + "/meetings/" + meetingID).removeValue();
				// sets the value of the current user key in the meeting details reference to false; used to signal which users gave feedback
				reference.child("meetings/" + meetingID + "/members/" + mAuth.getCurrentUser().getUid()).setValue("false");

				// check if current user was the last one giving ratings
				int participantsRated = 0; // assume no participants completed
				for (DataSnapshot members : dataSnapshot.child("meetings/" + meetingID + "/members").getChildren())
					// if any participant still did not end meeting and complete ratings
					if (members.getValue(String.class).equals("true"))
						participantsRated++;

				// if all participants (except the user right now) left ratings, delete meeting and chat
				if (participantsRated <= 1) {
					reference.child("meetings/" + meetingID).removeValue();
					reference.child("chats/" + meetingID).removeValue();
				}

				// subtract 10 points if meeting is left
				helper.addPoints(dataSnapshot, reference, FirebaseAuth.getInstance().getCurrentUser().getUid(), 10);

				helper.goHome();
			}
		});
	}
}