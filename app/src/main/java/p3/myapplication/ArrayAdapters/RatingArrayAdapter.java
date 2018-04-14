package p3.myapplication.ArrayAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import p3.myapplication.R;

public class RatingArrayAdapter extends ArrayAdapter<String[]> {

	private Context context;

	private List<String[]> values = new ArrayList<>();

	public RatingArrayAdapter (int resource, Context context, List<String[]> values) {
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
		listItem = LayoutInflater.from(context).inflate(R.layout.rating_row_item, parent, false);

		// import all fields that need to be populated in a card
		TextView participantName = listItem.findViewById(R.id.participantNameRating);
		CheckBox absenceCheckbox = listItem.findViewById(R.id.absenceCheckbox);
		final RatingBar ratingBar = listItem.findViewById(R.id.ratingBar);

		absenceCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					ratingBar.setEnabled(false);
				else
					ratingBar.setEnabled(true);
			}
		});

		participantName.setText(values.get(position)[0]);

		return listItem;
	}
}