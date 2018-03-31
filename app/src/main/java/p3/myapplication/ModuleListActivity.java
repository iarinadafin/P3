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

	FirebaseAuth mAuth;
	FirebaseUser currentUser;
	DatabaseReference reference;

	ListView modulesList;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_module_list);

		mAuth = FirebaseAuth.getInstance();
		currentUser = mAuth.getCurrentUser();
		reference = FirebaseDatabase.getInstance().getReference();

		modulesList = findViewById(R.id.moduleListCreate);

		BottomNavigationView navigation = findViewById(R.id.navigationModuleList);
		navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				return new Intentions(ModuleListActivity.this).chooseMenuItem(item);
			}
		});
		navigation.getMenu().findItem(navigation.getSelectedItemId()).setCheckable(false);

		reference.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				Pair userCourseData = getUserCourse(dataSnapshot);

				String course = (String) userCourseData.first;
				int year = (int) userCourseData.second;

				customiseModules(course, year);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {}
		});

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

	@SuppressWarnings("ConstantConditions")
	public Pair<String, Integer> getUserCourse (DataSnapshot dataSnapshot) {
		return new Pair<> (dataSnapshot.getValue(User.class).getCourse(), dataSnapshot.getValue(User.class).getYear());
	}

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
	
	public void setModuleValues (int resource, boolean compSciY2) {
		ArrayAdapter<String> itemsAdapter;
		String[] moduleArray;

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

	public void goToModuleMeetingsList (String module) {
		Intent i = new Intent(this, ModuleMeetingsActivity.class);
		i.putExtra("p3.myapplication:module", module);

		startActivity(i);
	}
}
