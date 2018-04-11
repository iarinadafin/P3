package p3.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

class Intentions {

	private Context c;

	Intentions (Context c) {
		this.c = c;
	}

	void goToSignIn () {
		FirebaseAuth.getInstance().signOut();
		Intent i = new Intent(c, MainActivity.class);
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

	void goToProfile () {
		Intent i = new Intent(c, UserProfileActivity.class);
		c.startActivity(i);
	}

	void goToViewMeeting (String meetingID, String module, String dateString, String hoursString) {
		Intent i = new Intent(c, ViewMeetingActivity.class);
		i.putExtra("p3.myapplication:meeting_id", meetingID); // sends id of meeting
		i.putExtra("p3.myapplication:module_id", module); // sends module name
		i.putExtra("p3.myapplication:date", dateString); // sends date
		i.putExtra("p3.myapplication:hours", hoursString); // sends hours
		c.startActivity(i);
	}

	void goToChat (String meetingID, String meetingName) {
		Intent i = new Intent(c, ChatActivity.class);
		i.putExtra("p3.myapplication:meeting_id", meetingID);
		i.putExtra("p3.myapplication:meeting_name", meetingName);
		c.startActivity(i);
	}

	void goToRating (String meetingID, String meetingName) {
		Intent i = new Intent(c, RatingActivity.class);
		i.putExtra("p3.myapplication:meeting_id", meetingID);
		i.putExtra("p3.myapplication:meeting_name", meetingName);
		c.startActivity(i);
	}

	boolean chooseMenuItem (MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_home: {
				goHome();
				return true;
			}
			case R.id.action_messages: {
				goToMessages();
				return true;
			}
			case R.id.action_profile: {
				goToProfile();
				return true;
			}
		}
		return false;
	}

	// used
	void deleteMeeting (String userID, DatabaseReference reference, DataSnapshot snapshot, String meetingID) {
		reference.child("meetings/" + meetingID).removeValue();
		reference.child("users/" + userID + "/meetings/" + meetingID).removeValue();
		reference.child("chats/" + meetingID).removeValue();
	}
}