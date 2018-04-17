package p3.myapplication;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class Helper {

	private Context c;

	public Helper(Context c) {
		this.c = c;
	}

	void goToSignIn () {
		FirebaseAuth.getInstance().signOut();
		Intent i = new Intent(c, SignInActivity.class);
		c.startActivity(i);
	}

	void goToSignUp () {
		Intent i = new Intent(c, SignUpActivity.class);
		c.startActivity(i);
	}

	void goHome () {
		Intent i = new Intent(c, HomeActivity.class);
		c.startActivity(i);
	}

	void goToMessages () {
		Intent i = new Intent(c, ChatsListActivity.class);
		c.startActivity(i);
	}

	public void goToProfile (boolean isOwnProfile, String userID) {
		Intent i = new Intent(c, UserProfileActivity.class);

		// if the user tries to access their own profile, send "true"; send "false" otherwise
		if (isOwnProfile)
			i.putExtra("p3.myapplication:isOwnProfile", "true");
		else {
			i.putExtra("p3.myapplication:isOwnProfile", "false");
			i.putExtra("p3.myapplication:userID", userID);
		}

		c.startActivity(i);
	}

	public void goToViewMeeting (String meetingID, String module, String dateString, String hoursString) {
		Intent i = new Intent(c, ViewMeetingActivity.class);
		i.putExtra("p3.myapplication:meeting_id", meetingID); // sends id of meeting
		i.putExtra("p3.myapplication:module_id", module); // sends module name
		i.putExtra("p3.myapplication:date", dateString); // sends date
		i.putExtra("p3.myapplication:hours", hoursString); // sends hours
		c.startActivity(i);
	}

	public void goToChat (String meetingID, String meetingName) {
		Intent i = new Intent(c, ChatActivity.class);
		i.putExtra("p3.myapplication:meeting_id", meetingID);
		i.putExtra("p3.myapplication:meeting_name", meetingName);
		c.startActivity(i);
	}

	public void goToRating (String meetingID, String meetingName) {
		Intent i = new Intent(c, RatingActivity.class);
		i.putExtra("p3.myapplication:meeting_id", meetingID);
		i.putExtra("p3.myapplication:meeting_name", meetingName);
		c.startActivity(i);
	}

	void deleteMeeting (String userID, DatabaseReference reference, String meetingID) {
		reference.child("meetings/" + meetingID).removeValue();
		reference.child("users/" + userID + "/meetings/" + meetingID).removeValue();
		reference.child("chats/" + meetingID).removeValue();
	}

	public void addPoints (DataSnapshot dataSnapshot, DatabaseReference reference, String userUid, int points) {
		int score = Integer.parseInt(dataSnapshot.child("users/" + userUid + "/score").getValue(String.class));
		reference.child("users/" + userUid + "/score").setValue(String.valueOf(score + points));
	}

	float getRating (DataSnapshot dataSnapshot) {
		return Float.valueOf(dataSnapshot.child("totalScore").getValue(String.class)) / Float.valueOf(dataSnapshot.child("numberOfRatings").getValue(String.class));
	}
}