package com.fsociety.nirapotta_v3;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fsociety.nirapotta_v3.Model.User;
import com.fsociety.nirapotta_v3.Subscription.CheckSubscription;
import com.fsociety.nirapotta_v3.Subscription.MyCallback;
import com.fsociety.nirapotta_v3.Subscription.Operations;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class StartingActivity extends AppCompatActivity {

    private final static int RC_SIGN_IN = 2;

    FrameLayout rootLayout;
    Button loginButton;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    public static String my_preferernce = "login";
    public static String switchState = "on";
    public static String uid = "uid";
    SharedPreferences sp;

    //google sign in
    SignInButton btn_google;
    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
        //init firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        rootLayout = findViewById(R.id.rootLayout);
        loginButton = findViewById(R.id.btn_login);
        btn_google = findViewById(R.id.btn_glogin);

        TextView textView = (TextView) btn_google.getChildAt(0);
        textView.setText("Sign in with Google");
        textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    signIn();
                } catch (Exception e) {
                    Toast.makeText(StartingActivity.this, "For Robi and Airtel Users only.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        sp = getSharedPreferences(uid, MODE_PRIVATE);
        Operations.currentUid = sp.getString("uid", null);
        sp = getSharedPreferences(my_preferernce, MODE_PRIVATE);


        if (sp.getBoolean("logged", false)) {
            Log.d("radif", "onCreate: auto login");
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        }


        new AppEULA(StartingActivity.this).show();
        Log.d("radif", "onCreate: auto login");

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
//        checkSubscriber();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("google123", "onActivityResult: " + resultCode);
//                Operations.currentUid=auth.getCurrentUser().getUid();


                final android.app.AlertDialog waitingdialog = new SpotsDialog.Builder().setContext(StartingActivity.this).build();
                waitingdialog.show();
                firebaseAuthWithGoogle(account);


            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("google123", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("google123", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("google123", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            addUser(user);
                            //slight changed by hridoy
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("google123", "signInWithCredential:failure", task.getException());
                            Snackbar.make(rootLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void addUser(FirebaseUser user) {
//        Log.d("radif", "addUser: "+user.getDisplayName());
//        Log.d("radif", "addUser: "+user.getEmail());
//        Log.d("radif", "addUser: "+user.getPhoneNumber());
//        Log.d("radif", "addUser: "+user.getUid());
        Log.d("radif", "addUser: Creating new user");
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setName(user.getDisplayName());
        newUser.setPhone(user.getPhoneNumber());
        newUser.setSubscription("3");
        newUser.setSubscriberID("");
        sp = getSharedPreferences(uid, MODE_PRIVATE);
        sp.edit().putString("uid", user.getUid()).apply();
        Operations.currentUid = user.getUid();
        Map<String, Object> values = new HashMap<>();
        values.put(user.getUid(), newUser);


        //use email to key
//        FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("subscription").setValue("asdsad");
//        try {
//            FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("subscription");
//            Log.d("radif", "addUser: try"+user.getUid());
//        }catch (NullPointerException np){
//            Log.d("radif", "addUser: catch");
//            users.updateChildren(value);
//        }
        Operations.getSubscriberID(new MyCallback() {
            @Override
            public void onCallback(String value) {

            }

            @Override
            public void onCallbackId(String value) {
                try {
                    if (value.equals("")) {
                    }
                    Log.d("radif", "addUser: if " + user.getUid());
//                        users.updateChildren(values);

                    sp = getSharedPreferences(my_preferernce, MODE_PRIVATE);
                    sp.edit().putBoolean("logged", true).apply();
                    Intent i = new Intent(StartingActivity.this, HomeActivity.class);
                    startActivity(i);

                } catch (Exception e) {
                    Log.d("radif", "onCallbackId: Adding user to database...");
                    users.updateChildren(values);
                    Intent i = new Intent(StartingActivity.this, HomeActivity.class);
                    startActivity(i);
//                  checkSubscriber();
                }
            }
        });

    }

    public void onLogin(View view) {


        showLoginDialog();
    }

    public void onRegister(View view) {

        showRegisterDialog();
    }


    private void showLoginDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login, null);

        final EditText edtEmail = login_layout.findViewById(R.id.email);
        final EditText edtPassword = login_layout.findViewById(R.id.password);


        dialog.setView(login_layout);

        //set button
        dialog.setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                //disable sign in button if in process
                loginButton.setEnabled(false); //must come back

                //checking validation

                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter Email", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                final android.app.AlertDialog waitingdialog = new SpotsDialog.Builder().setContext(StartingActivity.this).build();
                waitingdialog.show();

                //login

                auth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

//                                final String[] subCount = {""};

                                Operations.currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                sp = getSharedPreferences(uid, MODE_PRIVATE);
                                Log.d("radif", "onSuccess: " + Operations.currentUid);
                                sp.edit().putString("uid", Operations.currentUid).apply();
                                waitingdialog.dismiss();
                                loginButton.setEnabled(true);
                                checkVerification();

//                                checkSubscriber();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingdialog.dismiss();
                        Snackbar.make(rootLayout, "Failed" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        loginButton.setEnabled(true);//enable it again now
                    }
                });

            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        dialog.show();


    }

    private void checkVerification() {
        boolean verified = FirebaseAuth.getInstance().getCurrentUser().isEmailVerified();

        if (verified) {
            Intent intent = new Intent(StartingActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        } else {
            SharedPreferences sp = getSharedPreferences(my_preferernce, MODE_PRIVATE);
            sp.edit().putBoolean("logged", true).apply();
        }
        Intent i = new Intent(StartingActivity.this, EmailVerification.class);
        startActivity(i);
//        }
    }

//
//    private void checkSubscriber(){
//
//        Operations.getSubscriberID(new MyCallback() {
//            @Override
//            public void onCallback(String value) {
//
//            }
//
//            @Override
//            public void onCallbackId(String value) {
//                if(value.equals("")){
//                    startActivity(new Intent(StartingActivity.this, CheckSubscription.class));
//                }
//                else{
//                    sp=getSharedPreferences(my_preferernce,MODE_PRIVATE);
//                    sp.edit().putBoolean("logged", true).apply();
//                    Intent i = new Intent(StartingActivity.this, HomeActivity.class);
//                    startActivity(i);
//
//                }
//            }
//        });

//        users.child(Operations.currentUid).child("subscriberID").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
////                                        subCount[0] = String.valueOf(dataSnapshot.getValue());
//                if(dataSnapshot!=null) {
//                    String subCount = String.valueOf(dataSnapshot.getValue());
//                    Log.d("radif", "onDataChange checksubscriber: " + subCount);
////                                        Log.d("tag", "onSuccess: " + subCount[0]);
//                    if (subCount.equals("")) {
////                                            Log.d("tag", "onSuccess: " + subCount[0]);
//                        startActivity(new Intent(StartingActivity.this, CheckSubscription.class));//
//                    } else {
//                        Log.d("radif", "onDataChange: auto" + sp.getString("uid", ""));
//                        sp=getSharedPreferences(my_preferernce,MODE_PRIVATE);
//                        sp.edit().putBoolean("logged", true).apply();
//                        Intent i = new Intent(StartingActivity.this, HomeActivity.class);
//                        startActivity(i);
//
//                    }
//                    finish();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


    private void showRegisterDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_reg, null);

        final EditText edtEmail = register_layout.findViewById(R.id.email);
        final EditText edtPassword = register_layout.findViewById(R.id.password);
        final EditText edtName = register_layout.findViewById(R.id.name);
        final EditText edtPhone = register_layout.findViewById(R.id.phone);

        dialog.setView(register_layout);

        //set button
        dialog.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                //checking validation

                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter Email", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edtName.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter Name", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edtPhone.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter Phone number", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter Email", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (edtPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "Password too short!!", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (!(edtPhone.getText().toString().matches("01[6789]{1}[0-9]{8}"))) {
                    Snackbar.make(rootLayout, "Invalid phone number.", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                //register new user
                auth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                //save user to db
                                User user = new User();
                                user.setEmail(edtEmail.getText().toString());
                                user.setName(edtName.getText().toString());
                                user.setPassword(edtPassword.getText().toString());
                                user.setPhone(edtPhone.getText().toString());
                                user.setSubscription("3");
                                user.setSubscriberID("");
                                //use email to key
                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootLayout, "Registered successfully!!", Snackbar.LENGTH_SHORT).show();

                                            }

                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                            }
                                        });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                        Log.d("tag", "onFailure: " + e.getMessage());
                    }
                });

            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
