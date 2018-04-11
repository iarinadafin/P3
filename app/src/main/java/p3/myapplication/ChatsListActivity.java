package p3.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatsListActivity extends AppCompatActivity {

	DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

	ListView chatsList;

	ArrayList<String[]> userChatsList = new ArrayList<>();
	String userUid = FirebaseAuth.getInstance().getUid();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chats_list);

		chatsList = findViewById(R.id.chatsListMessages);

		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot meeting : dataSnapshot.child("meetings").getChildren()) {
					// checks if the meeting has the user as a participant and if the chat of the meeting has any messages
					//														   if not, the chat will not be displayed in the chats list
					if (meeting.child("members/" + userUid).exists() && dataSnapshot.child("chats/" + meeting.getKey()).exists())
						userChatsList.add(new String[] {meeting.getKey(), meeting.child("name").getValue(String.class)});

				}
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

	public void displayChats () {
		userChatsList.clear();
		MeetingChatArrayAdapter adapter = new MeetingChatArrayAdapter(0, this, userChatsList);
		chatsList.setAdapter(adapter);
	}
}