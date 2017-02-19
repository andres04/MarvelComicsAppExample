package com.nextdots.marvelcomics.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.nextdots.marvelcomics.R;
import com.nextdots.marvelcomics.application.AppPreferences;
import com.nextdots.marvelcomics.di.component.AppComponent;
import com.nextdots.marvelcomics.fragments.ComicListFragment;
import com.nextdots.marvelcomics.fragments.DetalleComicFragment;
import com.nextdots.marvelcomics.fragments.LoginFragment;
import com.nextdots.marvelcomics.fragments.SplashFragment;
import com.nextdots.marvelcomics.messages.FragmentMessage;
import com.nextdots.marvelcomics.messages.ToolbarMessage;
import com.nextdots.marvelcomics.rest.RestApi;
import com.nextdots.marvelcomics.rest.entity.Perfil;
import com.nextdots.marvelcomics.utils.Constants;
import com.nextdots.marvelcomics.utils.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int ADD = 0;
    private static final int REPLACE = 1;

    @Inject
    RestApi restApi;
    @Inject
    AppPreferences appPreferences;
    @BindView(R.id.content_main)
    FrameLayout contentMain;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.clMain)
    CoordinatorLayout clMain;
    @BindView(R.id.flFullScreenMain)
    FrameLayout flFullScreenMain;

    private TextView tvNombreUsuario;
    private TextView tvCorreoUsuario;
    private ImageView ivIconoUsuario;

    private Fragment currentFragment;
    private Toolbar toolbar;
    private DrawerLayout drawer;

    @Override
    protected void setupComponent(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tvNombreUsuario = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvNombreUsuario);
        tvCorreoUsuario = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvCorreoUsuario);
        ivIconoUsuario = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ivIconoUsuario);

        showToolbar(false);
        currentFragment = SplashFragment.newInstance();
        String tag = currentFragment.getClass().getSimpleName();

        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
                currentFragment = fragmentManager.findFragmentByTag(fragmentTag);
                if (currentFragment != null) {
                    Log.e("fragment=", currentFragment.getClass().getSimpleName());
                }
            }
        });

        getSupportFragmentManager().beginTransaction()
                .add(R.id.flFullScreenMain, currentFragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    private void setupToolbar(Toolbar t) {
        setSupportActionBar(t);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, t, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void showToolbar(boolean show) {
        if (show) {
            appBar.setVisibility(View.VISIBLE);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            appBar.setVisibility(View.GONE);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    private void loadPerfil() {
        Perfil perfil = Util.getGson().fromJson(appPreferences.getPreference(Constants.PERFIL, String.class), Perfil.class);
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
    }

    public void removePerfil() {
        appPreferences.getSharedPreferences().edit().remove(Constants.PERFIL).commit();
        ivIconoUsuario.setImageDrawable(null);
        tvNombreUsuario.setText(null);
        tvCorreoUsuario.setText(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(FragmentMessage event) {
        switch (event.getFragmentAction()) {
            case FragmentMessage.FROM_SPLASH_TO_LOGIN:
                startFragment(LoginFragment.newInstance(), true, R.id.flFullScreenMain);
                break;
            case FragmentMessage.FROM_SPLASH_TO_COMIC_LIST:
                showToolbar(true);
                startFragment(ComicListFragment.newInstance(false), true, R.id.content_main);
                loadPerfil();
                break;
            case FragmentMessage.FROM_LOGIN_TO_COMIC_LIST:
                showToolbar(true);
                startFragment(ComicListFragment.newInstance(false), true, R.id.content_main);
                loadPerfil();
                break;
            case FragmentMessage.FROM_NAV_FAVORITOS_TO_COMIC_LIST:
                showToolbar(true);
                startFragment(ComicListFragment.newInstance(event.getBundle().getBoolean(Constants.OFFLINE)), true, R.id.content_main);
                loadPerfil();
                break;

            case FragmentMessage.FROM_COMIC_LIST_TO_DETALLE_COMIC:
                showToolbar(false);
                Bundle bundle = event.getBundle();
                startFragment(
                        DetalleComicFragment.newInstance(
                                bundle.getInt(Constants.ID_COMIC)
                                , bundle.getString(Constants.COMICS_LIST_RESULT_ITEM))
                        , false, R.id.flFullScreenMain);
                break;
            case FragmentMessage.LOGOFF:
                showToolbar(false);
                removePerfil();
                startFragment(LoginFragment.newInstance(), true, R.id.flFullScreenMain);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(ToolbarMessage event) {
        setupToolbar(event.getToolbar());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void startFragment(Fragment fragment, boolean clearBackStack, int viewId) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = supportFragmentManager
                    .beginTransaction();
            if (clearBackStack) {
                clearBackStack();
            }
            if(viewId == R.id.flFullScreenMain){
                flFullScreenMain.setVisibility(View.VISIBLE);
                clMain.setVisibility(View.GONE);
            } else if(viewId == R.id.content_main){
                flFullScreenMain.setVisibility(View.GONE);
                clMain.setVisibility(View.VISIBLE);
            }
            fragmentTransaction.add(viewId, fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commit();

        }
        Log.e("backstack", supportFragmentManager.getBackStackEntryCount() + "");
    }

    private void clearBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //back del drawer
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        //Back para el buscador de la lista de comics
        else if (currentFragment != null && currentFragment instanceof ComicListFragment) {
            SearchView searchView = ((ComicListFragment) currentFragment).getSearchView();
            if (searchView != null && !searchView.isIconified()) {
                searchView.setIconified(true);
            } else {
                back();
            }
        }
        //back genérico
        else {
            back();
        }
    }

    private void back() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 1) {
            super.onBackPressed();
            if (currentFragment instanceof ComicListFragment) {
                setupToolbar(toolbar);
                showToolbar(true);
                clMain.setVisibility(View.VISIBLE);
                flFullScreenMain.setVisibility(View.GONE);
            }
            Log.e("backstack back", getSupportFragmentManager().getBackStackEntryCount() + "");
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (currentFragment != null && currentFragment instanceof LoginFragment) {
            currentFragment.onActivityResult(requestCode, resultCode, data);
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
        if (id == R.id.nav_comics) {
            EventBus.getDefault().post(new FragmentMessage(FragmentMessage.FROM_LOGIN_TO_COMIC_LIST, null));
        }
        if (id == R.id.nav_favoritos) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.OFFLINE, true);
            EventBus.getDefault().post(new FragmentMessage(FragmentMessage.FROM_NAV_FAVORITOS_TO_COMIC_LIST, bundle));
        }
        if (id == R.id.nav_cerrar_sesion) {
            EventBus.getDefault().post(new FragmentMessage(FragmentMessage.LOGOFF, null));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
