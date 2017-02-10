package com.nextdots.marvelcomics.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.nextdots.marvelcomics.R;
import com.nextdots.marvelcomics.application.AppPreferences;
import com.nextdots.marvelcomics.di.component.AppComponent;
import com.nextdots.marvelcomics.fragments.ComicListFragment;
import com.nextdots.marvelcomics.rest.RestApi;
import com.nextdots.marvelcomics.rest.entity.Perfil;
import com.nextdots.marvelcomics.utils.Constants;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, ComicListFragment.OnFragmentInteractionListener{

    public static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    RestApi restApi;
    @Inject
    AppPreferences appPreferences;
    private ComicListFragment comicListFragment;
    private TextView tvNombreUsuario;
    private TextView tvCorreoUsuario;
    private ImageView ivIconoUsuario;

    @Override
    protected void setupComponent(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tvNombreUsuario = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvNombreUsuario);
        tvCorreoUsuario = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvCorreoUsuario);
        ivIconoUsuario = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ivIconoUsuario);

        Perfil perfil = gson.fromJson(appPreferences.getPreference(Constants.PERFIL, String.class), Perfil.class);
        tvNombreUsuario.setText(perfil.getNombre());
        tvCorreoUsuario.setText(perfil.getCorreo());
        Glide.with(this).load(perfil.getImg()).asBitmap().fitCenter().into(new BitmapImageViewTarget(ivIconoUsuario) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                ivIconoUsuario.setImageDrawable(circularBitmapDrawable);
            }
        });

        comicListFragment = ComicListFragment.newInstance(null, null);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, comicListFragment).commit();

    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            SearchView searchView = comicListFragment.getSearchView();
            if (searchView!= null && !searchView.isIconified()) {
                searchView.setIconified(true);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

        if (id == R.id.nav_cerrar_sesion) {
            appPreferences.getSharedPreferences().edit().remove(Constants.PERFIL).commit();
            Intent intent = new Intent(this, SplashActivity.class);
            finish();
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }



}
