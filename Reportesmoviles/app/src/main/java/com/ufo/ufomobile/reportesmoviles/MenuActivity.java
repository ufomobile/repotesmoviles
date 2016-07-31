package com.ufo.ufomobile.reportesmoviles;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import utilities.Report;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback {

    int[] imageIDs = {
            R.drawable.alcantarillado,
            R.drawable.alumbrado,
            R.drawable.acueducto,
            R.drawable.basura,
            R.drawable.limpieza,
            R.drawable.gas,
    };
    private GoogleMap mMap;
    View lastSelectedView=null;
    Gallery gallery;
    LinearLayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Toolbar -----------------------------------------------------------------------------
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Map ---------------------------------------------------------------------------------
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Drawer ------------------------------------------------------------------------------
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Navigationview ----------------------------------------------------------------------
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Gallery -----------------------------------------------------------------------------
        gallery = (Gallery) findViewById(R.id.gallery);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setUnselectedAlpha(1.0f);
        int selection=(int) (Integer.MAX_VALUE / 2) - ((Integer.MAX_VALUE / 2) % 6);
        gallery.setSelection((int) (Integer.MAX_VALUE / 2) - ((Integer.MAX_VALUE / 2) % imageIDs.length));
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //ImageView imageView = (ImageView) findViewById(R.id.image1);
                //imageView.setImageResource(imageIDs[i]);
                if (lastSelectedView != null) {
                    lastSelectedView.setLayoutParams(new Gallery.LayoutParams(130, 82));
                }
                if(view!=null) {
                    view.setLayoutParams(new Gallery.LayoutParams(130, 87));
                    lastSelectedView = view;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("POSITION CLICK", position + "");
                Intent goToReport = new Intent(MenuActivity.this,AddNewReportActivity.class);
                goToReport.putExtra("category_icon",imageIDs[position%imageIDs.length]);
                startActivity(goToReport);

            }
        });

        //Horizontal list -------------------------------------------------------------------------
        //----------------------------------------------------------------------------------------
        List<Report> data = new ArrayList<>();
        data.add(new Report("id123","String title", "String description",
                "String address", "String referencePoint", 0,
                0,0, Report.PUBLISHED, "String category","hoy",null));
        data.add(new Report("id123","String title", "String description",
                "String address", "String referencePoint", 0,
                0,0, Report.IN_PROCESS, "String category","hoy",null));
        data.add(new Report("id123","String title", "String description",
                "String address", "String referencePoint", 0,
                0,0, Report.SOLVED, "String category","hoy",null));
        //----------------------------------------------------------------------------------------
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView myList = (RecyclerView) findViewById(R.id.my_recycler_view);
        Recycler_View_Adapter adapter = new Recycler_View_Adapter(data, getApplication());
        myList.setAdapter(adapter);
        myList.setLayoutManager(layoutManager);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
/*
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitude-0.01, longitude-0.01);
        LatLng sydney2 = new LatLng(latitude+0.01, longitude-0.01);
        LatLng sydney3 = new LatLng(latitude-0.01, longitude+0.01);

        Bitmap b1 = drawableToBitmap(getResources().getDrawable(R.drawable.alcantarillado));
        Bitmap bhalfsize1=Bitmap.createScaledBitmap(b1, b1.getWidth() / 4, b1.getHeight() / 4, false);
        mMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney")
                .icon(BitmapDescriptorFactory.fromBitmap(bhalfsize1)));

        Bitmap b2 = drawableToBitmap(getResources().getDrawable(R.drawable.alumbrado));
        Bitmap bhalfsize2=Bitmap.createScaledBitmap(b2, b2.getWidth() / 4, b2.getHeight() / 4, false);
        mMap.addMarker(new MarkerOptions().position(sydney2)
                .title("Marker in Sydney2")
                .icon(BitmapDescriptorFactory.fromBitmap(bhalfsize2)));

        Bitmap b = drawableToBitmap(getResources().getDrawable(R.drawable.acueducto));
        Bitmap bhalfsize=Bitmap.createScaledBitmap(b, b.getWidth() / 4, b.getHeight() / 4, false);
        mMap.addMarker(new MarkerOptions().position(sydney3)
                .title("Marker in Sydney3")
                .icon(BitmapDescriptorFactory.fromBitmap(bhalfsize)));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(13)
                .bearing(0)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String tit = marker.getTitle();
                if (tit.equals("Marker in Sydney")) {
                    layoutManager.scrollToPositionWithOffset(0, 20);
                } else if (tit.equals("Marker in Sydney2")) {
                    layoutManager.scrollToPositionWithOffset(1, 20);
                } else if (tit.equals("Marker in Sydney3")) {
                    layoutManager.scrollToPositionWithOffset(2, 20);
                }
                return false;
            }
        });
    }


    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }



    //--------------------------------------------------------------------------------------------------------------
    //IMAGE ADAPTER FOR GALLERY
    //--------------------------------------------------------------------------------------------------------------

    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private int itemBackground;
        public ImageAdapter(Context c)
        {
            context = c;
            // sets a grey background; wraps around the images
/*
            TypedArray a =obtainStyledAttributes(R.styleable.MyGallery);
            itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
            a.recycle();*/

        }
        // returns the number of images
        public int getCount() {
            //return imageIDs.length;
            return Integer.MAX_VALUE;
        }
        // returns the ID of an item
        public Object getItem(int position) {
            return position;
        }
        // returns the ID of an item
        public long getItemId(int position) {
            int pos = position % imageIDs.length;
            return pos;
        }
        // returns an ImageView view
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;

            if (convertView == null) {
                // Create a new view into the list.
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.gallery_adapter_layout, parent, false);
            }
            /*
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(imageIDs[position].getRecurso());
            imageView.setLayoutParams(new Gallery.LayoutParams(100, 100));
            imageView.setBackgroundResource(itemBackground);
            return imageView;
            */
            position=position%imageIDs.length;
            ImageView img = (ImageView)rowView.findViewById(R.id.img);
            img.setImageResource(imageIDs[position%imageIDs.length]);
            //image.setImageResource(imageIDs[position%imageIDs.length]);
            //image.setImageResource(R.drawable.acueducto);
            rowView.setLayoutParams(new Gallery.LayoutParams(130, 82));
            //rowView.setBackgroundResource(itemBackground);
            return rowView;
        }
    }

    //--------------------------------------------------------------------------------------------------------------
    //RECYCLER VIEW ADAPTER FOR HORIZONTAL SCROLL
    //--------------------------------------------------------------------------------------------------------------
    public class Recycler_View_Adapter extends RecyclerView.Adapter<View_Holder> {

        List<Report> list = Collections.emptyList();
        Context context;

        public Recycler_View_Adapter(List<Report> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Inflate the layout, initialize the View Holder
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_row_layout, parent, false);
            View_Holder holder = new View_Holder(v);

            return holder;

        }

        @Override
        public void onBindViewHolder(View_Holder holder, final int position) {

            String state=list.get(position).getState();
            String category=list.get(position).getCategory();

            //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
            holder.title.setText(list.get(position).getTitle());
            holder.description.setText(list.get(position).getDescription());
            holder.state.setText(state);
            if(state.equals(Report.PUBLISHED)){
                holder.state.setTextColor(getResources().getColor(R.color.colorPublished));
            }else if(state.equals(Report.IN_PROCESS)){
                holder.state.setTextColor(getResources().getColor(R.color.colorInPorcess));
            }else{
                holder.state.setTextColor(getResources().getColor(R.color.colorSolved));
            }
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("CLICK",position+"");
                    Report report=list.get(position);
                    Intent goToDescription = new Intent(getApplicationContext(),ReportDescriptionActivity.class);
                    goToDescription.putExtra("report",report);
                    startActivity(goToDescription);
                }
            });

            //animate(holder);

        }

        @Override
        public int getItemCount() {
            //returns the number of elements the RecyclerView will display
            return list.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        // Insert a new item to the RecyclerView on a predefined position
        public void insert(int position, Report data) {
            list.add(position, data);
            notifyItemInserted(position);
        }

        // Remove a RecyclerView item containing a specified Data object
        public void remove(String data) {
            int position = list.indexOf(data);
            list.remove(position);
            notifyItemRemoved(position);
        }

    }

    //Auxiliar class holder ------------------------------------------------------------
    public class View_Holder extends RecyclerView.ViewHolder {

        TextView title,description,state,more;

        View_Holder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            state = (TextView) itemView.findViewById(R.id.state);
            more = (TextView) itemView.findViewById(R.id.more);
        }
    }
}