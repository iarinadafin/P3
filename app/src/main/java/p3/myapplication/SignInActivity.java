package p3.myapplication;

import android.app.Activity;
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

public class SignInActivity extends Activity {

	private FirebaseAuth mAuth = FirebaseAuth.getInstance();
	private FirebaseAuth.AuthStateListener mAuthListener;

	// components in the view
	private EditText emailLoginField;
	private EditText passwordLoginField;
	protected Button loginButton;
	protected Button signUpButton;

	Helper helper = new Helper(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// used if a persistent database is used
		/*
		try {
			FirebaseDatabase.getInstance().setPersistenceEnabled(true);
		} catch (Throwable t) {
			Log.e("mytag", "FirbaseDatabase setPersistanceEnabled exception");
		}
		*/

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
				helper.goToSignUp();
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
					helper.goHome();
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
						helper.goHome();
					} else {
						// if sign in fails, display a message
						Log.w("mytag", "signInWithEmail:failure", task.getException());
						Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
					}
				}
			});

	}

	/**
	 * Used to verify that the login fields are correctly filled in
	 * @param email the user's email address
	 * @param password the user's password
	 * @return a boolean: true if all information is correct, false otherwise
	 */
	public boolean verifyFields (String email, String password) {
		Boolean check = true; // initially assumes all information is correct

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
}