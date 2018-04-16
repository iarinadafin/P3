package p3.myapplication.ArrayAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import p3.myapplication.Helper;
import p3.myapplication.R;

/**
 * Created by Iarina Dafin.
 */

public class ParticipantDetailsArrayAdapter extends ArrayAdapter<String[]> {

	DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

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

		final Helper helper = new Helper(context);

		TextView participantName = listItem.findViewById(R.id.participantNameView);
		TextView ratingLabel = listItem.findViewById(R.id.participantRatingView);

		String nameString = String.format(context.getResources().getString(R.string.full_name), values.get(position)[1], values.get(position)[2]);
		participantName.setText(nameString);

		ratingLabel.setText(values.get(position)[3]);

		listItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (values.get(position)[0].equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
					helper.goToProfile(true, values.get(position)[0]);
				else
					helper.goToProfile(false, values.get(position)[0]);
			}
		});

		return listItem;
	}
}
