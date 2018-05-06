package ca.javajeff.btsdigital;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Саддам on 05.05.2018.
 */

public class AddGuestActivity extends AppCompatActivity{

    Button myButton;
    EditText nameText;
    EditText lastText;
    EditText phoneText;
    RadioButton childButton;
    RadioButton adultButton;

    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference myRef;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        final AccessToken accessToken = AccountKit.getCurrentAccessToken();
        myButton = findViewById(R.id.main_button);
        nameText = findViewById(R.id.name_text);
        lastText = findViewById(R.id.last_text);
        phoneText = findViewById(R.id.phone_text);
        childButton = findViewById(R.id.child_button);
        adultButton = findViewById(R.id.adult_button);
        childButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true) {
                    adultButton.setChecked(false);
                }
            }
        });
        adultButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true) {
                    childButton.setChecked(false);
                }
            }
        });

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = accessToken.getAccountId();
                String name = String.valueOf(nameText.getText());
                String last = String.valueOf(lastText.getText());
                String phone = String.valueOf(phoneText.getText());
                myRef.child("Profiles").child(token.toString()).child("Guests").child(name + " " + last).child("Phone").setValue(phone);
                myRef.child("Profiles").child(token.toString()).child("Guests").child(name + " " + last).child("isChild").setValue(isChild());
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
