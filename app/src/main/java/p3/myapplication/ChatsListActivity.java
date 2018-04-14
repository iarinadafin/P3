package p3.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import p3.myapplication.ArrayAdapters.MeetingChatArrayAdapter;

public class ChatsListActivity extends AppCompatActivity {

	DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

	ListView chatsList;

	ArrayList<String[]> userChatsList = new ArrayList<>();
	String userUid = FirebaseAuth.getInstance().getUid();

	@SuppressWarnings("ConstantConditions")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chats_list);

		chatsList = findViewById(R.id.chatsListMessages);

		// set navbar behaviour
		BottomNavigationView navigation = findViewById(R.id.navigationChatsList);
		navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				Helper helper = new Helper(ChatsListActivity.this);
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
		navigation.getMenu().getItem(1).setCheckable(true);

		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				// clear the array, so that the listview can be refreshed
				userChatsList.clear();

				for (DataSnapshot meeting : dataSnapshot.child("meetings").getChildren()) {
					// if the user is a participant
					if(meeting.child("members/" + userUid).exists())
						// checks if the meeting has the user did not leave review and if the chat of the meeting has any messages
						//														   	   if not, the chat will not be displayed in the chats list
						if (meeting.child("members/" + userUid).getValue(String.class).equals("true") && dataSnapshot.child("chats/" + meeting.getKey()).exists())
							userChatsList.add(new String[] {meeting.getKey(), meeting.child("name").getValue(String.class)});
				}
				// displays all of the user's chats
				displayChats();
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}

	@Override
	protected void onStart(){
		super.onStart();
	}

	@Override
	protected void onStop () {
		super.onStop();
	}

	/**
	 * Adds the chats information list to the adapter and displays the chats
	 */
	public void displayChats () {
		MeetingChatArrayAdapter adapter = new MeetingChatArrayAdapter(0, this, userChatsList);
		chatsList.setAdapter(adapter);
	}
}