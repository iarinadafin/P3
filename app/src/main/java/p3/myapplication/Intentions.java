package p3.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

class Intentions {

	Context c;

	Intentions (Context c) {
		this.c = c;
	}

	void goToSignIn () {
		FirebaseAuth.getInstance().signOut();
		Intent i = new Intent(c, MainActivity.class);
		c.startActivity(i);
	}

	void goHome () {
		Intent i = new Intent(c, HomeActivity.class);
		c.startActivity(i);
	}

	void goToMessages () {
		Intent i = new Intent(c, MainActivity.class);
		c.startActivity(i);
	}

	void goToProfile () {
		Intent i = new Intent(c, MainActivity.class);
		c.startActivity(i);
	}

	boolean chooseMenuItem (MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_home: {
				goHome();
				return true;
			}
			case R.id.action_messages: {
				goToMessages();
				return true;
			}
			case R.id.action_profile: {
				goToProfile();
				return true;
			}
		}
		return false;
	}
}