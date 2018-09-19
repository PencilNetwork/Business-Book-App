package com.pencil.pencil.businessbook.Business.Activity;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
//import android.support.design.widget.NavigationView;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.pencil.pencil.businessbook.Activity.HomeActivity;
import com.pencil.pencil.businessbook.Api.APIClient;
import com.pencil.pencil.businessbook.Api.APIInterface;
import com.pencil.pencil.businessbook.Business.Fragment.BProfileFragment;
import com.pencil.pencil.businessbook.Business.Fragment.BusinessProfileFragment;
import com.pencil.pencil.businessbook.Business.Fragment.CreateOfferFragment;
import com.pencil.pencil.businessbook.Business.Fragment.EditOfferFragment;
import com.pencil.pencil.businessbook.Business.Fragment.EditProfileFragment;
import com.pencil.pencil.businessbook.Business.Fragment.EditRelatedFileFragment;
import com.pencil.pencil.businessbook.Business.Pojo.BusinessProfileResponse;
import com.pencil.pencil.businessbook.Business.Pojo.GeneralResponseBody;
import com.pencil.pencil.businessbook.Business.Pojo.Offer;
import com.pencil.pencil.businessbook.Business.Pojo.RelatedFile;
import com.pencil.pencil.businessbook.R;
import com.pencil.pencil.businessbook.Util.PrefManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.pencil.pencil.businessbook.Util.FileUtils.ProgressDialog;

public class BusinessProfileActivity extends AppCompatActivity {
    //UI Variable
    private DrawerLayout mDrawerLayout; //left menu
    private Toolbar mToolbar; //toolbar

    private NavigationView mNvDrawer;

    private FrameLayout mainFrameLayout;
    //variable
    private Context mContext;
    PrefManager mPrefManager;
    //LOADING_DIALOG
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_main);
        mContext = BusinessProfileActivity.this;

        bindVariable();
        initializeMenu();
        initializeFragment();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("menu clicked" + mContext);
        if (mContext != null) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                    break;

                default:
                    return false;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindVariable() {
        mToolbar = findViewById(R.id.toolbar_top);
        //add menu icon to toolbar


        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNvDrawer = findViewById(R.id.nvView);
        mainFrameLayout = findViewById(R.id.fragmentLayout);
    }

    private void initializeFragment() {

        // Fragment fragment = new CreateOfferFragment();
        Fragment fragment = new BusinessProfileFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragmentLayout, fragment, fragment.getClass().getName()).commit();
    }

    private void initializeMenu() {
        ((Activity)mContext).setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.right_menu));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mDrawerLayout.closeDrawers();
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        setupDrawerContent(mNvDrawer);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        final FragmentManager manager=getSupportFragmentManager();;
        // drawer layout left menu select item listener
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {


                            case R.id.add_related_file:
                                // open Profile activity
                                EditRelatedFileFragment editRelatedFileFragment = new EditRelatedFileFragment();

                                manager.beginTransaction().replace(R.id.fragmentLayout, editRelatedFileFragment, editRelatedFileFragment.getClass().getName()).commit();


                                // close the menu
                                mDrawerLayout.closeDrawers();

                                break;
                            case R.id.edit_business_profile:
                                EditProfileFragment  editProfileFragment = new EditProfileFragment();
                                manager.beginTransaction().replace(R.id.fragmentLayout, editProfileFragment,editProfileFragment.getClass().getName()).commit();

                                // close the menu
                                mDrawerLayout.closeDrawers();

                                break;
                            case R.id.edit_offer:
                                EditOfferFragment editOfferFragment = new EditOfferFragment();
                                manager.beginTransaction().replace(R.id.fragmentLayout, editOfferFragment,editOfferFragment.getClass().getName()).commit();

                                mDrawerLayout.closeDrawers();

                                break;
                            case R.id.log_out:
                                Intent intent = new Intent(mContext,HomeActivity.class );
                                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                               startActivity(intent);
                                // close the menu
                                mDrawerLayout.closeDrawers();

                                break;


                            default:
                                // close the menu
                                mDrawerLayout.closeDrawers();
                        }

                        return true;
                    }
                });
    }

}
