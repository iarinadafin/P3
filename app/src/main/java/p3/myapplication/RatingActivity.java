package p3.myapplication;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RatingActivity extends AppCompatActivity {

	DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
	FirebaseAuth mAuth = FirebaseAuth.getInstance();

	TextView meetingTitle;
	ListView userRatings;
	Button submitRatings;

	RatingArrayAdapter adapter;
	List<String[]> userInformationList = new ArrayList<>();
	String meetingID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rating);

		meetingTitle = findViewById(R.id.meetingTitleRating);
		userRatings = findViewById(R.id.listOfRatings);
		submitRatings = findViewById(R.id.submitRatings);

		meetingTitle.setText(getIntent().getExtras().getString("p3.myapplication:meeting_name"));

		meetingID = getIntent().getExtras().getString("p3.myapplication:meeting_id");

		reference.addValueEventListener(new ValueEventListener() {
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
					View listItem = userRatings.getChildAt(0);
					CheckBox checkBox = listItem.findViewById(R.id.absenceCheckbox);

					// if participant was not absent
					if (!checkBox.isChecked()) {
						RatingBar ratingBar = listItem.findViewById(R.id.ratingBar);
						int rating = Math.round(ratingBar.getRating());

						//dataSnapshot
					}
				}
			}
		});
	}
}
