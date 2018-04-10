package p3.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Iarina Dafin.
 */

public class MainActivity extends Activity {

	private FirebaseAuth mAuth;
	private FirebaseAuth.AuthStateListener mAuthListener;

	// components in the view
	private EditText emailLoginField;
	private EditText passwordLoginField;
	protected Button loginButton;
	protected Button signUpButton;

	Intent goToSignUp; // todo: change this into a method

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		try {
			FirebaseDatabase.getInstance().setPersistenceEnabled(true);
		} catch (Throwable t) {
			Log.e("mytag", "FirbaseDatabase setPersistanceEnabled exception");
		}
		*/
		// todo: re-enable and keepsynced(true)

		mAuth = FirebaseAuth.getInstance();
		goToSignUp = new Intent(this, SignUpActivity.class);

		emailLoginField = findViewById(R.id.emailLogin);
		passwordLoginField = findViewById(R.id.passwordLogin);
		loginButton = findViewById(R.id.loginButtonConfirm);
		signUpButton = findViewById(R.id.signUp);

		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View view) {
				signIn();
			}
		});

		signUpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View v) {
				startActivity(goToSignUp);
			}
		});

		// listens to the authentication state of the user
		mAuthListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged (@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user != null) {
					// User is signed in
					Log.d("mytag", "onAuthStateChanged:signed_in:" + user.getUid());
					goHome();
				} else {
					// User is signed out
					Log.d("mytag", "onAuthStateChanged:signed_out");
				}
			}
		};
	}

	@Override
	public void onStart() {
		super.onStart();
		mAuth.addAuthStateListener(mAuthListener); // initialises user auth listener
		//mAuth.signOut();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mAuthListener != null) {
			mAuth.removeAuthStateListener(mAuthListener);
		}
	}

	public void signIn () {
		final String email = emailLoginField.getText().toString();
		String password = passwordLoginField.getText().toString();

		if (verifyFields(email, password))
			mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
				@Override
				public void onComplete(@NonNull Task<AuthResult> task) {
					if (task.isSuccessful()) {
						// if sign in success, update UI
						Log.d("mytag", "signInWithEmail:success");
						goHome();
					} else {
						// if sign in fails, display a message
						Log.w("mytag", "signInWithEmail:failure", task.getException());
						Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
					}
				}
			});

	}

	public boolean verifyFields (String email, String password) {
		Boolean check = true;
		if (email.equals("")) {
			emailLoginField.setError("Required!");
			check = false;
		}
		if (password.equals("")) {
			passwordLoginField.setError("Required!");
			check = false;
		}

		return check;
	}

	public void goHome () {
		Intent i = new Intent(this, HomeActivity.class);
		startActivity(i);
	}
}
