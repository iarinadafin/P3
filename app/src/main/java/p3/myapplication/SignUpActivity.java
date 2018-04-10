package p3.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends Activity {

	private FirebaseAuth mAuth;
	private FirebaseUser currentUser;

	FirebaseDatabase database;
	DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
	DatabaseReference cond = reference.child("users");

	User user;

	EditText firstNameField;
	EditText lastNameField;
	Spinner courseSpinner;
	RadioGroup yearRadioGroup;
	EditText emailField;
	EditText passwordField;
	EditText confirmPasswordField;
	Button signUpButton;

	ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		mAuth = FirebaseAuth.getInstance();
		database = FirebaseDatabase.getInstance();
		reference = database.getReference();

		firstNameField = findViewById(R.id.firstNameSignUp);
		lastNameField = findViewById(R.id.lastNameSignUp);
		courseSpinner = findViewById(R.id.courseSpinnerSignUp);
		yearRadioGroup = findViewById(R.id.yearRadioGroupSignUp);
		emailField = findViewById(R.id.emailSignUp);
		passwordField = findViewById(R.id.passwordSignUp);
		confirmPasswordField = findViewById(R.id.confirmPasswordSignUp);
		signUpButton = findViewById(R.id.signUpButtonConfirm);
		progressBar = findViewById(R.id.progressBar);

		((RadioButton) yearRadioGroup.getChildAt(0)).setChecked(true);
		progressBar.setVisibility(View.GONE);

		signUpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (validateForm(passwordField.getText().toString(), confirmPasswordField.getText().toString())) {
					progressBar.setVisibility(View.VISIBLE);

					//int yearValue = Integer.parseInt(((RadioButton) findViewById(yearRadioGroup.getCheckedRadioButtonId())).getText().toString());
					user = new User(firstNameField.getText().toString(), lastNameField.getText().toString(), courseSpinner.getSelectedItem().toString(), ((RadioButton) findViewById(yearRadioGroup.getCheckedRadioButtonId())).getText().toString(), emailField.getText().toString());

					signUp(passwordField.getText().toString());
				}
				else
					Toast.makeText(SignUpActivity.this, "Please review your details.", Toast.LENGTH_SHORT).show();
			}
		});

		emailField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				focusChange(emailField, hasFocus);
			}
		});

		passwordField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				focusChange(emailField, hasFocus);
			}
		});

		confirmPasswordField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				focusChange(emailField, hasFocus);
			}
		});

		// todo: set listeners for all necessary fields
	}

	public void focusChange (EditText field, boolean hasFocus) {
		if (!hasFocus && field.getText().toString().equals(""))
			field.setError("Required");
	}

	public void signUp (String password) {

		mAuth.createUserWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				if (task.isSuccessful()) {
					// Sign in success, update UI with the signed-in user's information
					Log.d("mytag", "createUserWithEmail:success");

					currentUser = mAuth.getCurrentUser();
					pushToDatabase(user);

					goToLogin();
				} else {
					// If sign in fails, display a message to the user.
					Log.w("mytag", "createUserWithEmail:failure", task.getException());
					Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
					progressBar.setVisibility(View.GONE);
				}
			}
		});
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public void pushToDatabase (User user) {
		currentUser = mAuth.getCurrentUser();

		if (currentUser != null)
			cond.child(currentUser.getUid()).setValue(user);
	}

	public boolean validateForm (String password, String confirmPassword) {
		// assumes everything is right
		boolean check = true;

		// checks if any fields are empty

		if (firstNameField.getText().toString().equals("")) {
			focusChange(firstNameField, false);
			check = false;
		}

		if (lastNameField.getText().toString().equals("")) {
			focusChange(lastNameField, false);
			check = false;
		}

		// also checks for email match by comparing to regex
		if (emailField.getText().toString().equals("") || !emailField.getText().toString().matches("(?i:^[a-z][a-z]([a-z]?)[0-9][a-z][0-9][0-9](@soton.ac.uk)$)")) {
			focusChange(emailField, false);
			check = false;
		}

		if (passwordField.getText().toString().equals("")) {
			focusChange(passwordField, false);
			check = false;
		}

		if (confirmPasswordField.getText().toString().equals("")) {
			focusChange(confirmPasswordField, false);
			check = false;
		}

		// checks if password and confirmed password match
		if (!password.equals(confirmPassword)) {
			focusChange(passwordField, false);
			focusChange(confirmPasswordField, false);
			check = false;
		}

		return check;
	}

	public void goToLogin () {
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}
}
