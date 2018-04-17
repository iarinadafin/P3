package p3.myapplication.ArrayAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
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

public class LeaderboardItem extends ArrayAdapter<String[]> {

	private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

	private Context context;

	private TextView leaderboardPosition;
	private TextView userName;

	private List<String[]> values = new ArrayList<>();

	public LeaderboardItem(int resource, Context context, List<String[]> values) {
		super(context, resource, values);
		this.context = context;
		this.values = values;
	}

	@SuppressWarnings("ConstantConditions")
	@SuppressLint("ViewHolder")
	@NonNull
	@Override
	public View getView(final int position, final View convertView, @NonNull ViewGroup parent) {
		final View listItem = LayoutInflater.from(context).inflate(R.layout.leaderboard_row_item, parent, false);

		final Helper helper = new Helper(context);

		// import all fields that need to be populated in a card
		leaderboardPosition = listItem.findViewById(R.id.leaderboardScore);
		userName = listItem.findViewById(R.id.userNameLeaderboard);

		String positionString = String.format(context.getResources().getString(R.string.leaderboard_position), values.get(position)[0]);
		leaderboardPosition.setText(positionString);

		String fullName = String.format(context.getResources().getString(R.string.full_name), values.get(position)[1], values.get(position)[2]);
		userName.setText(fullName);

		//  if user is current user (if equals is true)
		if (values.get(position)[3].equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
			userName.setTextColor(context.getColor(R.color.colorPrimaryDark));
			userName.setTypeface(null, Typeface.BOLD_ITALIC);
		}
		else
			listItem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					helper.goToProfile(false, values.get(position)[3]);
				}
			});

		return listItem;
	}
}
