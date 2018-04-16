package p3.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class ModuleListActivity extends AppCompatActivity {

	FirebaseAuth mAuth = FirebaseAuth.getInstance();
	DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

	ListView modulesList;

	ArrayAdapter<String> itemsAdapter;
	String[] moduleArray;

	@SuppressWarnings("ConstantConditions")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_module_list);

		modulesList = findViewById(R.id.moduleListCreate);

		BottomNavigationView navigation = findViewById(R.id.navigationModuleList);
		navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				Helper helper = new Helper(ModuleListActivity.this);
				switch (item.getItemId()) {
					case R.id.action_home: {
						helper.goHome();
						return false;
					}
					case R.id.action_messages: {
						helper.goToMessages();
						return false;
					}
					case R.id.action_profile: {
						helper.goToProfile(true, mAuth.getCurrentUser().getUid());
						return false;
					}
				}
				return false;
			}
		});
		navigation.getMenu().findItem(navigation.getSelectedItemId()).setCheckable(false);

		// gets the current user, their course and year of study
		reference.child("users/" + mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				Pair<String, String> userCourseData = getUserCourse(dataSnapshot);

				String course = userCourseData.first;
				int year = Integer.parseInt(userCourseData.second);

				customiseModules(course, year);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {}
		});

		// the onclick listener for each ListView item (module)
		modulesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				goToModuleMeetingsList((String) parent.getItemAtPosition(position));
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	/**
	 * Gets the user's course and year of study
	 * @param dataSnapshot the DataSnapshot used to retrieve the data
	 * @return a pair containing a string of the course name and a string of the year of study
	 */
	public Pair<String, String> getUserCourse (DataSnapshot dataSnapshot) {
		return new Pair<> (dataSnapshot.child("course").getValue(String.class), dataSnapshot.child("year").getValue(String.class));
	}

	/**
	 * Customises the module list according to the user's course and year of study
	 * @param course the current user's course
	 * @param year the current user's year of study
	 */
	public void customiseModules (String course, int year) {
		switch (course) {
			case "Computer Science": {
				if (year == 1)
					setModuleValues(R.array.csse_courses_year_1, false);
				else if (year == 2)
					setModuleValues(R.array.csse_courses_year_2, true);
				else if (year == 3)
					setModuleValues(R.array.csse_courses_year_3, false);
				else
					setModuleValues(R.array.csse_courses_year_4, false);
				break;
			}
			case "Software Engineering": {
				if (year == 1)
					setModuleValues(R.array.csse_courses_year_1, false);
				else if (year == 2)
					setModuleValues(R.array.csse_courses_year_2, false);
				else if (year == 3)
					setModuleValues(R.array.csse_courses_year_3, false);
				else
					setModuleValues(R.array.csse_courses_year_4, false);
				break;
			}
			case "Electronic Engineering": {
				if (year == 1)
					setModuleValues(R.array.ee_courses_year_1, false);
				else if (year == 2)
					setModuleValues(R.array.ee_courses_year_2, false);
				else if (year == 3)
					setModuleValues(R.array.ee_courses_year_3, false);
				else
					setModuleValues(R.array.ee_courses_year_4, false);
				break;
			}
		}
	}

	/**
	 * Sets the list of the adapter that will populate the ListView of the modules
	 * Also checks for special modules in certain courses
	 * @param resource the appropriate string array resource that contains the course's modules for the user's year of study
	 * @param compSciY2 signals if special course needs to be added
	 */
	public void setModuleValues (int resource, boolean compSciY2) {
		// adds an extra module for comp sci yr 2
		if (compSciY2) {
			List<String> tempList = Arrays.asList(getResources().getStringArray(resource));
			tempList.add("Change and Equilibrium");
			moduleArray = tempList.toArray(new String[tempList.size()]);
		}
		else
			moduleArray = getResources().getStringArray(resource);

		itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, moduleArray);
		modulesList.setAdapter(itemsAdapter);
	}

	/**
	 * Starts the module meetings list activity
	 * @param module string of the module chosen, passed over to the activity
	 */
	public void goToModuleMeetingsList (String module) {
		Intent i = new Intent(this, ModuleMeetingsActivity.class);
		i.putExtra("p3.myapplication:module", module);

		startActivity(i);
	}
}