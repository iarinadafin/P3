package p3.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class CustomArrayAdapter extends ArrayAdapter<String[]> {

	private Context context;
	private List<String[]> values = new ArrayList<>();

	CustomArrayAdapter (int resource, Context context, List<String[]> values) {
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
		TextView hours = listItem.findViewById(R.id.hoursLabel);
		TextView date = listItem.findViewById(R.id.dateLabel);
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
		String hoursString = startTokens[3] + " - " + endTokens[3];
		// sets the date string resource in the [dayOfTheWeek, month day, year] format
		String dateString = context.getResources().getString(R.string.date_field, // date resource
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
				Intent i = new Intent(context, ViewMeetingActivity.class);
				i.putExtra("p3.myapplication:module_id", values.get(position)[4]);
				context.startActivity(i);
			}
		});

		return listItem;
	}
}