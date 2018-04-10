package p3.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MeetingChatArrayAdapter extends ArrayAdapter<String[]> {

	private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

	private Context context;
	private List<String[]> values = new ArrayList<>();

	private TextView lastMessage;
	private TextView lastMessageSender;

	MeetingChatArrayAdapter(int resource, Context context, List<String[]> values) {
		super(context, resource, values);
		this.context = context;
		this.values = values;
	}

	@SuppressWarnings("ConstantConditions")
	@SuppressLint("ViewHolder")
	@NonNull
	@Override
	public View getView(final int position, final View convertView, @NonNull ViewGroup parent) {
		final View listItem = LayoutInflater.from(context).inflate(R.layout.meeting_chat_row_item, parent, false);

		// import all fields that need to be populated in a card
		final TextView title = listItem.findViewById(R.id.titleLabelChats);
		LinearLayout item = listItem.findViewById(R.id.leftColumn);

		// sets the chat title
		title.setText(values.get(position)[1]);

		// sets the last message of the chat
		final Query query = reference.child("chats/" + values.get(position)[0]).orderByChild("timestamp").limitToLast(1);
		query.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot data : dataSnapshot.getChildren()) {
					lastMessage = listItem.findViewById(R.id.lastMessageChats);
					lastMessage.setText(data.child("content").getValue(String.class));

					// checks if the message belongs to the current user
					if (data.child("userID").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
						lastMessageSender = listItem.findViewById(R.id.senderNameLabel);
						String message = String.format(context.getResources().getString(R.string.last_message_sender), "You");
						lastMessageSender.setText(message);
					}
					// if the message is a system message
					else if (data.child("userID").getValue(String.class).equals("system")) {
						lastMessageSender = listItem.findViewById(R.id.senderNameLabel);
						lastMessageSender.setVisibility(View.GONE);

						String message = String.format(context.getResources().getString(R.string.user_joined_or_left_message), data.child("content").getValue(String.class));
						lastMessage.setText(message);
					}
					// if the message is a registered user's message
					else {
						reference.child("users/" + data.child("userID").getValue(String.class)).addValueEventListener(new ValueEventListener() {
							@Override
							public void onDataChange(DataSnapshot dataSnapshot) {
								lastMessageSender = listItem.findViewById(R.id.senderNameLabel);
								String message = String.format(context.getResources().getString(R.string.last_message_sender), dataSnapshot.child("firstName").getValue(String.class));
								lastMessageSender.setText(message);
							}

							@Override
							public void onCancelled(DatabaseError databaseError) {
								throw databaseError.toException();
							}
						});
					}
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				throw databaseError.toException();
			}
		});

		item.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, ChatActivity.class);
				i.putExtra("p3.myapplication:meeting_id", values.get(position)[0]);
				i.putExtra("p3.myapplication:meeting_name", values.get(position)[1]);
				context.startActivity(i);
			}
		});

		return listItem;
	}
}