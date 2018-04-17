package p3.myapplication;

import android.app.Activity;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import p3.myapplication.Model.User;

public class SignUpActivity extends Activity {

	private FirebaseAuth mAuth;
	DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

	EditText firstNameField;
	EditText lastNameField;
	Spinner courseSpinner;
	RadioGroup yearRadioGroup;
	EditText emailField;
	EditText passwordField;
	EditText confirmPasswordField;
	Button signUpButton;
	ProgressBar progressBar;

	User user;
	Helper helper = new Helper(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		mAuth = FirebaseAuth.getInstance();

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

	public void signUp (final String password) {

		mAuth.createUserWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
			@SuppressWarnings("ConstantConditions")
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				if (task.isSuccessful()) {
					// Sign in success, update UI with the signed-in user's information
					Log.d("mytag", "createUserWithEmail:success");

					pushToDatabase(user);

					helper.goToSignIn();
				} else {
					try {
						throw task.getException();
					} catch(FirebaseAuthWeakPasswordException e) {
						passwordField.setError("Weak password!");
						passwordField.requestFocus();
					} catch(FirebaseAuthUserCollisionException e) {
						Toast.makeText(SignUpActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
					} catch(Exception e) {
						Toast.makeText(SignUpActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
					}

					Log.w("mytag", "createUserWithEmail:failure", task.getException());
					progressBar.setVisibility(View.GONE);
				}
			}
		});
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	/**
	 * Pushes the created user object to the database
	 * @param user the object created with the user information
	 */
	public void pushToDatabase (User user) {
		if (mAuth.getCurrentUser() != null)
			reference.child("users/" + mAuth.getCurrentUser().getUid()).setValue(user);
	}

	/**
	 * Validates the sign up information fields
	 * @param password the password passed to the form
	 * @param confirmPassword the confirmation password passed to the form
	 * @return a boolean: true if all information is correct, false otherwise
	 */
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

		// also checks for email format match by comparing to regex
		if (emailField.getText().toString().equals("") || !emailField.getText().toString().matches("(?i:^[a-z][a-z]([a-z]?)[0-9][a-z][0-9][0-9](@soton.ac.uk)$)")) {
			focusChange(emailField, false);
			check = false;
		}

		// checks for empty password fields
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
}