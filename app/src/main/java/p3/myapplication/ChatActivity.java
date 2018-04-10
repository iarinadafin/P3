package p3.myapplication;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

	DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

	String meetingID;
	List<String[]> messageDetails = new ArrayList<>();

	ListView chatMessages;

	@SuppressWarnings("ConstantConditions")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// todo: rename and re-id components
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		meetingID = getIntent().getExtras().getString("p3.myapplication:meeting_id");

		FloatingActionButton sendMessage = findViewById(R.id.sendMessageButton);
		TextView title = findViewById(R.id.chatTitleLabel);
		chatMessages = findViewById(R.id.chatMessages);

		title.setText(getIntent().getExtras().getString("p3.myapplication:meeting_name"));

		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				// clears the adapter that contains all messages at database data change
				messageDetails.clear();
				for (DataSnapshot message : dataSnapshot.child("chats/" + meetingID).getChildren())
					messageDetails.add(new String[] {
										dataSnapshot.child("users/" + message.child("userID").getValue(String.class) + "/firstName").getValue(String.class), // first name of the message sender [0]
										message.child("content").getValue(String.class), // message content [1]
										message.child("timestamp").getValue(String.class).substring(0, 16), // message timestamp [2]
										String.valueOf(message.child("userID").getValue(String.class).equals("system")), // if the sender is the system [3]
										message.child("userID").getValue(String.class)}); // sender ID [4]
				showMessages();
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				throw databaseError.toException();
			}
		});

		sendMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				EditText input = findViewById(R.id.input);

				// checks that the message is not empty
				if (!input.getText().toString().equals("")) {
					Calendar calendar = Calendar.getInstance(Locale.UK);
					calendar.add(Calendar.HOUR_OF_DAY, 1);

					// Read the input field and push a new instance
					// of ChatMessage to the Firebase database
					FirebaseDatabase.getInstance()
							.getReference("chats/" + meetingID)
							.push()
							.setValue(new Message(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK).format(calendar.getTime()),
									FirebaseAuth.getInstance().getCurrentUser().getUid(),
									input.getText().toString()));

					// Clear the input
					input.setText("");
				}
			}
		});
	}

	@Override
	protected void onStart () {
		super.onStart();
	}

	@Override
	protected void onStop () {
		super.onStop();
	}

	void showMessages () {
		ChatMessageArrayAdapter adapter = new ChatMessageArrayAdapter(0, this, messageDetails);
		chatMessages.setAdapter(adapter);
	}
}