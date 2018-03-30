package p3.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
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

	ArrayList<String> userChatsList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chats_list);

		chatsList = findViewById(R.id.chatsListMessages);

		reference.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/meetings").addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot data : dataSnapshot.getChildren())
					userChatsList.add(data.getKey());
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

	public void getMeetings (final String meetingKey) {
		reference.child("meetings").addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot category : dataSnapshot.getChildren())
					for (DataSnapshot meeting : category.getChildren())
						if (meeting.getKey().equals(meetingKey)) {
							userChatsList.add(meeting.child("name").getValue(String.class));
							break;
						}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}

	public void displayChats () {
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userChatsList);

		Log.d("mytag", String.valueOf(userChatsList.size()));

		chatsList.setAdapter(adapter);
	}
}
