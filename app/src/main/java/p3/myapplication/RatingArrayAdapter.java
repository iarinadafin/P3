package p3.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RatingArrayAdapter extends ArrayAdapter<String[]> {

	private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

	private Context context;
	private List<String[]> values = new ArrayList<>();
	private Intentions helper;

	RatingArrayAdapter (int resource, Context context, List<String[]> values) {
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

		helper = new Intentions(context);

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