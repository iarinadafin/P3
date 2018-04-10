package p3.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageArrayAdapter extends ArrayAdapter<String[]> {

	private Context context;
	private List<String[]> values = new ArrayList<>();

	ChatMessageArrayAdapter(int resource, Context context, List<String[]> values) {
		super(context, resource, values);
		this.context = context;
		this.values = values;
	}

	@SuppressWarnings("ConstantConditions")
	@SuppressLint("ViewHolder")
	@NonNull
	@Override
	public View getView(final int position, final View convertView, @NonNull ViewGroup parent) {
		final View listItem = LayoutInflater.from(context).inflate(R.layout.chat_message_row_item, parent, false);

		// if message is a user message
		if (!Boolean.parseBoolean(values.get(position)[3])) {
			listItem.findViewById(R.id.systemMessage).setVisibility(View.GONE);

			TextView messageSender = listItem.findViewById(R.id.messageSender);
			TextView messageContent = listItem.findViewById(R.id.messageContent);
			TextView messageTimestamp = listItem.findViewById(R.id.messageTimestamp);

			messageSender.setText(values.get(position)[0]);
			messageContent.setText(values.get(position)[1]);
			messageTimestamp.setText(values.get(position)[2]);

			// if sender is the current user
			if (values.get(position)[4].equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
				messageSender.setTextColor(context.getColor(R.color.condensedGrey));
		}
		// if message is a system message
		else {
			listItem.findViewById(R.id.userMessage).setVisibility(View.GONE);
			listItem.findViewById(R.id.messageContent).setVisibility(View.GONE);

			String systemMessage = String.format(context.getResources().getString(R.string.user_joined_or_left_message), values.get(position)[1]);
			TextView systemMessageField = listItem.findViewById(R.id.systemMessage);
			systemMessageField.setText(systemMessage);
		}

		return listItem;
	}
}