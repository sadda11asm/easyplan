package ca.javajeff.btsdigital;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,  MyAdapter.MyAdapterOnClickHandler  {

    private static RecyclerView myCategoriesView;
    private static MyAdapter myAdapter;
    public ArrayList<String> guests = new ArrayList<>();
    public static ArrayList<String> numbers = new ArrayList<>();

    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference myRef;
    public static
    TextView textView;

    TextView navigationName;
    TextView navigationNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myCategoriesView = findViewById(R.id.list_view);


        textView = findViewById(R.id.textView2);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        myCategoriesView.setLayoutManager(layoutManager);
        myCategoriesView.setHasFixedSize(true);

        myAdapter = new MyAdapter(this);

        myCategoriesView.setAdapter(myAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(view.getContext(), AddGuestActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        navigationName = headerView.findViewById(R.id.navigation_name);
        navigationNumber = headerView.findViewById(R.id.navigation_number);
        navigationName.setText(AccountKit.getCurrentAccessToken().getAccountId());
        navigationNumber.setVisibility(View.INVISIBLE);

        loadData();
    }

    private void loadData() {
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if (accessToken!=null) {
            Log.i("accessToken", accessToken.getAccountId());
            String token = accessToken.getAccountId();
            myRef.child("Profiles").child(token).child("Guests").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("datasnapshot", String.valueOf(dataSnapshot.getChildrenCount()));
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String name = ds.getKey();
                        Log.i("name", name);
                        String phone = ds.child("Phone").getValue().toString();
//                        boolean isChild = (boolean) ds.child("isChild").getValue();
//                        String child = null;
//                        if (isChild) child = "Child";
                        guests.add(name);
                        numbers.add(phone);
                    }
                    myAdapter.setInfoData(guests, numbers);
                    myCategoriesView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("cancelled", "dsfsd");
                }
            });
        } else {
            myCategoriesView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//         Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Intent intent = new Intent(this, AddGuestActivity.class);
//            startActivity(intent);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(this, SmartSeatingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_logout) {
            AccountKit.logOut();
            new CountDownTimer(2000, 250) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Toast.makeText(MainActivity.this, "Use the same method of logging in to enter your account back", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFinish() {
                    Intent intent = new Intent(MainActivity.this , LoginActivity.class);
                    startActivity(intent);
                }
            }.start();
        } else if (id == R.id.nav_send) {
            onClickWhatsApp();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void onClickWhatsApp() {

            PackageManager pm=getPackageManager();
            try {

                Intent waIntent = new Intent(Intent.ACTION_VIEW);
                for (int i=0;i<guests.size();i++) {
                    String name = guests.get(i);
                    Log.i("whats", name);
                    if (name.equals("Асет Малик")) {
                        String number = numbers.get(i);
                        waIntent.setType(number);
                        String text = "Dear " + name + ", come to our Wedding!";

                        PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                        //Check if package exists or not. If not then code
                        //in catch block will be called
                        waIntent.setPackage("com.whatsapp");

                        waIntent.putExtra(Intent.EXTRA_TEXT, text);
                        startActivity(Intent.createChooser(waIntent, "Share with"));
                        waIntent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + number + "&text=" + text));
                        startActivity(waIntent);
                    }
                }

            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                        .show();
            }

        }

    @Override
    public void onClick(String id) {

    }
}
