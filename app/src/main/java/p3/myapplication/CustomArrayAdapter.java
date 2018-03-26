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
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomArrayAdapter extends ArrayAdapter<String[]> {

	DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

	private Context context;
	private List<String[]> values = new ArrayList<>();

	public CustomArrayAdapter (int resource, Context context, List<String[]> values) {
		super(context, resource , values);
		this.context = context;
		this.values = values;
	}

	@SuppressLint("ViewHolder")
	@NonNull
	@Override
	public View getView (final int position, @NonNull final View convertView, @NonNull ViewGroup parent) {
		View listItem = convertView;

		// handles null list of values
		listItem = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);

		// import all fields that need to be populated in a card
		TextView title = listItem.findViewById(R.id.titleLabel);
		final TextView hours = listItem.findViewById(R.id.hoursLabel);
		final TextView date = listItem.findViewById(R.id.dateLabel);
		TextView participants = listItem.findViewById(R.id.participantsLabel);

		// separate start and end times from start and end dates
		String[] startTokens = values.get(position)[1].split("[-|\\s]");
		String[] endTokens = values.get(position)[2].split("[-|\\s]");

		// format string into date
		Date dateObject = new Date();
		try {
			dateObject = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).parse(values.get(position)[1]);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		// set hours string as startHour:startMinute - endHour:endMinute
		final String hoursString = startTokens[3] + " - " + endTokens[3];
		// sets the date string resource in the [dayOfTheWeek, month day, year] format
		final String dateString = context.getResources().getString(R.string.date_field, // date resource
				new SimpleDateFormat("EEE", Locale.UK).format(dateObject), // friendly short day of week
				new SimpleDateFormat("MMM", Locale.UK).format(dateObject), // friendly short month
				new SimpleDateFormat("dd", Locale.UK).format(dateObject), // day of month
				new SimpleDateFormat("yyyy", Locale.UK).format(dateObject)); // year

		// gets number of participants as int
		int noOfParticipants = Integer.parseInt(values.get(position)[3]);

		title.setText(values.get(position)[0]);
		hours.setText(hoursString);
		date.setText(dateString);																					// v number displayed
		participants.setText(context.getResources().getQuantityString(R.plurals.no_of_participants, noOfParticipants, noOfParticipants));
																			// ^ syntax plurality check
		// import and set listeners on buttons
		Button viewButton = listItem.findViewById(R.id.viewButton);
		Button joinOrMessageButton = listItem.findViewById(R.id.secondaryButton);

		viewButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Intentions(context).goToViewMeeting(values.get(position)[4], values.get(position)[5], dateString, hoursString);
			}
		});

		// sets message / join button according to user participation
		if (Boolean.parseBoolean(values.get(position)[6])) {
			joinOrMessageButton.setText(R.string.message_button);
			// todo: set listener for messaging
		}
		else {
			joinOrMessageButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// add user to members of meeting and the meeting in the user participation list
					reference.child("meetings/" + values.get(position)[5] + "/" + values.get(position)[4] + "/members/" + values.get(position)[7]).setValue("true");
					// module							// meeting								// user id
					reference.child("users/" + values.get(position)[7] + "/meetings/" + values.get(position)[4]).setValue("true");
					// user id								// meeting
					new Intentions(context).goToViewMeeting(values.get(position)[4], values.get(position)[5], dateString, hoursString);
				}
			});
		}

		return listItem;
	}
}