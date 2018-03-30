package p3.myapplication;

import android.arch.lifecycle.OnLifecycleEvent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		FloatingActionButton fab =
				(FloatingActionButton)findViewById(R.id.fab);

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				EditText input = (EditText)findViewById(R.id.input);

				Calendar calendar = Calendar.getInstance();

				// Read the input field and push a new instance
				// of ChatMessage to the Firebase database
				FirebaseDatabase.getInstance()
						.getReference("messages")
						.push()
						.setValue(new Message(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK).toString(),
												FirebaseAuth.getInstance().getCurrentUser().getUid(),
												input.getText().toString()));

				// Clear the input
				input.setText("");
			}
		});
	}

	@Override
	protected void onStart () {
		super.onStart();
	}

	@Override
	protected void onStop () {
		super.onStop();
	}
}
