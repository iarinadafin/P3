package p3.myapplication.ArrayAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import p3.myapplication.R;

/**
 * Created by Iarina Dafin.
 */

public class ParticipantDetailsArrayAdapter extends ArrayAdapter<String[]> {

	private Context context;

	private List<String[]> values = new ArrayList<>();

	public ParticipantDetailsArrayAdapter (int resource, Context context, List<String[]> values) {
		super(context, resource , values);
		this.context = context;
		this.values = values;
	}

	@SuppressLint("ViewHolder")
	@NonNull
	@Override
	public View getView (final int position, final View convertView, @NonNull ViewGroup parent) {
		View listItem;

		// handles null list of values
		listItem = LayoutInflater.from(context).inflate(R.layout.participant_row_item, parent, false);

		TextView participantName = listItem.findViewById(R.id.participantNameView);
		TextView ratingLabel = listItem.findViewById(R.id.participantRatingView);

		String nameString = String.format(context.getResources().getString(R.string.full_name), values.get(position)[0], values.get(position)[1]);
		participantName.setText(nameString);

		ratingLabel.setText(values.get(position)[2]);

		return listItem;
	}
}
