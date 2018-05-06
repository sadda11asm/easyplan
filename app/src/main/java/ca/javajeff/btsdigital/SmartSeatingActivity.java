package ca.javajeff.btsdigital;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static ca.javajeff.btsdigital.MainActivity.numbers;

/**
 * Created by Саддам on 06.05.2018.
 */

public class SmartSeatingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SmartAdapter.SmartAdapterOnClickHandler {

    RecyclerView list;
    SmartAdapter adapter;
    ArrayList<String> tables = new ArrayList<>();
    public static ArrayList<String> guests = new ArrayList<>();
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference myRef;
    public static String token;
    public static int number;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("SmartActivity", "createeed");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_seating);
        list = findViewById(R.id.list);

        token = AccountKit.getCurrentAccessToken().getAccountId();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);

        adapter = new SmartAdapter(this);

        list.setAdapter(adapter);

        list.addOnItemTouchListener(
                new RecyclerItemClickListener(this, list ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(final View view, final int position) {
                        Log.i("clicked", "yeees");
                        final boolean[] checkedItems;
                        checkedItems = new boolean[guests.size()];
                        final ArrayList<Integer> mUserItems = new ArrayList<>();
                        myRef.child("Tables").child(token).child(String.valueOf(position)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot dt:dataSnapshot.getChildren()) {
                                        for (int i=0;i<guests.size();i++) {
                                            if (guests.get(i).equals(dt.getKey())) {
                                                mUserItems.add(i);
                                                checkedItems[i]=true;
                                            }
                                        }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        final String[] guests_= new String[guests.size()];
                        for (int i=0;i<guests.size();i++) {
                            guests_[i]=guests.get(i);
                        }
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SmartSeatingActivity.this);
                        mBuilder.setTitle("Выберете соответствующих по вашему мнению гостей");
                        mBuilder.setMultiChoiceItems(guests_, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    if (!mUserItems.contains(which)) {
                                        mUserItems.add(which);
                                    }
                                } else if (mUserItems.contains(which)) {
                                        mUserItems.remove(which);
                                }
                            }
                        });
                        mBuilder.setCancelable(false);
                        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String item="";
                                for (int i=0;i<guests.size();i++) {
                                    if (checkedItems[i]) {
                                        myRef.child("Tables").child(token).child(String.valueOf(position)).child(guests_[i]).setValue(1);
                                    } else {
                                        myRef.child("Tables").child(token).child(String.valueOf(position)).child(guests_[i]).setValue(null);
                                    }
                                }
                                Context c =view.getContext();
                                Intent intent = new Intent(view.getContext(), SmartSeatingActivity.class);
                                c.startActivity(intent);
                            }
                        });
                        mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        mBuilder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String item="";
                                for (int i=0;i<checkedItems.length;i++) {
                                    checkedItems[i]=false;
                                }
                                mUserItems.clear();
//                                for (int i=0;i<10;i++) {
                                    myRef.child("Tables").child(token).child(String.valueOf(position)).removeValue();
//                                }
                                Context c =view.getContext();
                                Intent intent = new Intent(view.getContext(), SmartSeatingActivity.class);
                                c.startActivity(intent);
                            }
                        });
                        AlertDialog mDialog = mBuilder.create();
                        mDialog.show();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final boolean[] checkedItems;
                checkedItems = new boolean[guests.size()];
                final ArrayList<Integer> mUserItems = new ArrayList<>();
//                myRef.child("Tables").child(token).child(String.valueOf(number)).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for(DataSnapshot dt:dataSnapshot.getChildren()) {
//                            for (int i=0;i<guests.size();i++) {
//                                if (guests.get(i).equals(dt.getKey())) {
//                                    mUserItems.add(i);
//                                    checkedItems[i]=true;
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
                final String[] guests_= new String[guests.size()];
                for (int i=0;i<guests.size();i++) {
                    guests_[i]=guests.get(i);
                }
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SmartSeatingActivity.this);
                mBuilder.setTitle("Выберете соответствующих по вашему мнению гостей");
                mBuilder.setMultiChoiceItems(guests_, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            if (!mUserItems.contains(which)) {
                                mUserItems.add(which);
                            }
                        } else if (mUserItems.contains(which)) {
                            mUserItems.remove(which);
                        }
                    }
                });
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item="";
                        for (int i=0;i<guests.size();i++) {
                            if (checkedItems[i]) {
                                myRef.child("Tables").child(token).child(String.valueOf(number)).child(guests_[i]).setValue(1);
                            } else {
                                myRef.child("Tables").child(token).child(String.valueOf(number)).child(guests_[i]).setValue(null);
                            }
                        }
                        Context c =view.getContext();
                        Intent intent = new Intent(view.getContext(), SmartSeatingActivity.class);
                        c.startActivity(intent);
//                        loadTables();
                    }
                });
                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mBuilder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item="";
                        for (int i=0;i<checkedItems.length;i++) {
                            checkedItems[i]=false;
                        }
                        mUserItems.clear();
//                                for (int i=0;i<10;i++) {
                        myRef.child("Tables").child(token).child(String.valueOf(number)).removeValue();
//                                }
                        Context c =view.getContext();
                        Intent intent = new Intent(view.getContext(), SmartSeatingActivity.class);
                        c.startActivity(intent);
//                        loadTables();
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view2);
        navigationView.setNavigationItemSelectedListener(this);
        loadTables();
        loadGuests();

    }

    private static void loadGuests() {
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if (accessToken!=null) {
            Log.i("accessToken", accessToken.getAccountId());
            String token = accessToken.getAccountId();
            myRef.child("Profiles").child(token).child("Guests").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    guests.clear();
                    Log.i("datasnapshot", String.valueOf(dataSnapshot.getChildrenCount()));
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String name = ds.getKey();
                        Log.i("name", name);
                        String phone = ds.child("Phone").getValue().toString();
//                        boolean isChild = (boolean) ds.child("isChild").getValue();
//                        String child = null;
//                        if (isChild) child = "Child";
                        guests.add(name);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("cancelled", "dsfsd");
                }
            });
        } else {

        }
    }

    private void loadTables() {
        myRef.child("Tables").child(token).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                for (DataSnapshot dt:dataSnapshot.getChildren()) {
                    String names="Стол " + (i+1) + ":\n";
                    int j=0;
                    for (DataSnapshot ds: dt.getChildren()) {
                        names+=ds.getKey();
                        if (j!=dt.getChildrenCount()-1) names+=", ";
                        j++;
                    }
                    tables.add(names);
                    i++;
                }
                adapter.setInfoData(tables);
                number=i;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(this, SmartSeatingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_logout) {
            AccountKit.logOut();
            new CountDownTimer(2000, 250) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Toast.makeText(SmartSeatingActivity.this, "Use the same method of logging in to enter your account back", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFinish() {
                    Intent intent = new Intent(SmartSeatingActivity.this , LoginActivity.class);
                    startActivity(intent);
                }
            }.start();
        } else if (id == R.id.nav_send) {
            onClickWhatsApp();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(String id) {
        Intent intent = new Intent(SmartSeatingActivity.this, TableActivity.class);
        startActivity(intent);
    }

    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            public void onItemClick(View view, int position);

            public void onLongItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null) {
                        mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
                return true;
            }
            return false;
        }

        @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }




        @Override
        public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}
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
}
