package p3.myapplication.ArrayAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import p3.myapplication.Helper;
import p3.myapplication.Model.Message;
import p3.myapplication.R;

public class MeetingDetailsArrayAdapter extends ArrayAdapter<String[]> {

	private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

	private Context context;

	private List<String[]> values = new ArrayList<>();

	public MeetingDetailsArrayAdapter(int resource, Context context, List<String[]> values) {
		super(context, resource , values);
		this.context = context;
		this.values = values;
	}

	@SuppressLint("ViewHolder")
	@SuppressWarnings("ConstantConditions")
	@NonNull
	@Override
	public View getView (final int position, final View convertView, @NonNull ViewGroup parent) {
		View listItem;

		// handles null list of values
		listItem = LayoutInflater.from(context).inflate(R.layout.meeting_details_row_item, parent, false);

		final Helper helper = new Helper(context);

		// import all fields that need to be populated in a card
		TextView title = listItem.findViewById(R.id.titleLabel);
		final TextView hours = listItem.findViewById(R.id.hoursLabel);
		final TextView date = listItem.findViewById(R.id.dateLabel);
		TextView participants = listItem.findViewById(R.id.participantsLabel);

		// separate start and end times from start and end dates
		String[] startTokens = values.get(position)[1].split("[-|\\s]");
		String[] endTokens = values.get(position)[2].split("[-|\\s]");

		// format string into date
		Date startDate = new Date();
		Date endDate = new Date();
		try {
			startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).parse(values.get(position)[1]);
			endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).parse(values.get(position)[2]);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		// set hours string as startHour:startMinute - endHour:endMinute
		final String hoursString = startTokens[3] + " - " + endTokens[3];
		// sets the date string resource in the [dayOfTheWeek, month day, year] format
		final String dateString = context.getResources().getString(R.string.date_field, // date resource
				new SimpleDateFormat("EEE", Locale.UK).format(startDate), // friendly short day of week
				new SimpleDateFormat("MMM", Locale.UK).format(startDate), // friendly short month
				new SimpleDateFormat("dd", Locale.UK).format(startDate), // day of month
				new SimpleDateFormat("yyyy", Locale.UK).format(startDate)); // year

		// gets number of participants as int
		int noOfParticipants = Integer.parseInt(values.get(position)[3]);

		title.setText(values.get(position)[0]);
		hours.setText(hoursString);
		date.setText(dateString);																					// v number displayed
		participants.setText(context.getResources().getQuantityString(R.plurals.no_of_participants, noOfParticipants, noOfParticipants));
																			// ^ syntax plurality check
		// import and set listeners on buttons
		Button viewButton = listItem.findViewById(R.id.viewButton);
		final Button joinOrMessageButton = listItem.findViewById(R.id.secondaryButton);

		viewButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				helper.goToViewMeeting(values.get(position)[4], values.get(position)[5], dateString, hoursString);
			}
		});

		// calculates the current date and time
		Calendar calendar = Calendar.getInstance(Locale.UK);
		calendar.add(Calendar.HOUR_OF_DAY, 1);

		// sets message / join button according to user participation
		// if user is meeting participant
		if (Boolean.parseBoolean(values.get(position)[6])) {
			// checks if the meeting has more than one participant and if the endTime for the meeting has passed
			// in which case, it will change the message button into an end meeting button
			if (noOfParticipants > 1 && endDate.before(calendar.getTime())) {
				joinOrMessageButton.setText(R.string.end_button);
				joinOrMessageButton.setTextColor(context.getColor(R.color.colorAccent));

				// NOTE: despite the name, it here has the function of ending the meeting
				joinOrMessageButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						helper.goToRating(values.get(position)[4], values.get(position)[0]);
					}
				});
			}
			// if the end date has not passed
			else {
				joinOrMessageButton.setText(R.string.message_button);
				joinOrMessageButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new Helper(context).goToChat(values.get(position)[4], values.get(position)[0]);
					}
				});
			}
		}
		// if user is not a meeting participant
		else {
			joinOrMessageButton.setTextColor(context.getColor(R.color.colorAccent));
			joinOrMessageButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// add user to members of meeting and the meeting in the user participation list
					reference.child("meetings/" + values.get(position)[4] + "/members/" + values.get(position)[7]).setValue("true");
					// variables: meeting and user id
					reference.child("users/" + values.get(position)[7] + "/meetings/" + values.get(position)[4]).setValue("true");
					// variables: user id and meeting
					helper.goToViewMeeting(values.get(position)[4], values.get(position)[5], dateString, hoursString);

					Calendar calendar = Calendar.getInstance(Locale.UK);
					calendar.add(Calendar.HOUR_OF_DAY, 1);
					reference.child("chats/" + values.get(position)[4]).push().setValue(new Message(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK).format(calendar.getTime()),
																								"system",
																								values.get(position)[8] + " joined"));
					// adds 10 points when meeting is joined
					reference.addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							helper.addPoints(dataSnapshot, reference, FirebaseAuth.getInstance().getCurrentUser().getUid(), 10);
						}

						@Override
						public void onCancelled(DatabaseError databaseError) {

						}
					});

				}
			});
		}

		return listItem;
	}
}