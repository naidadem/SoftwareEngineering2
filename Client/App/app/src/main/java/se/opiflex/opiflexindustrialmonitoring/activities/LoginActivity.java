package se.opiflex.opiflexindustrialmonitoring.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import se.opiflex.opiflexindustrialmonitoring.R;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener
{

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView mStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        setContentView(R.layout.activity_login);
        mEmailField = findViewById(R.id.etUsername);
        mPasswordField = findViewById(R.id.etPassword);
        mStatusTextView = findViewById(R.id.StatusField);
        findViewById(R.id.bLogin).setOnClickListener(LoginActivity.this);

        mAuth = FirebaseAuth.getInstance();
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is already signed in (non-null).
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startNewIntent(currentUser);
        }
    }
    // [END on_start_check_user]

    private void signIn(String email, String password){
        Log.d(TAG, "signIn:" + email);

        if(!validateForm()) {
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Sign in success, update UI and proceed to Robot page
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser(); // IMPORTANT OBJECT
                            startNewIntent(user);

                        } else {
                            // If sign in fails, display message to user
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()){
                            mStatusTextView.setText("Login Failed");
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void startNewIntent(FirebaseUser user){
        if(user != null){
            Log.e("startNewIntent","Uid: " + user.getUid());
            FirebaseFirestore.getInstance().document("Users/" + user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String companyId = new String();
                        Intent intent = new Intent(LoginActivity.this, RobotListActivity.class);
                        DocumentSnapshot result = task.getResult();
                        if (result.exists()) {
                            companyId = result.getString("companyid");
                            intent.putExtra("companyid", companyId);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            LoginActivity.this.startActivity(intent);
                            LoginActivity.this.finish();
                        }
                    }
                }
            });
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if(TextUtils.isEmpty(email)){
            mEmailField.setError("Required.");
            valid = false;
        } else{
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if(TextUtils.isEmpty(password)){
            mPasswordField.setError("Required.");
            valid = false;
        } else{
            mPasswordField.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v){
        int i = v.getId();

        if (i == R.id.bLogin) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
    }

}
