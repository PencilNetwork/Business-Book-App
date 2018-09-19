package com.pencil.pencil.businessbook.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.google.android.gms.common.api.Api;
import com.pencil.pencil.businessbook.Business.Activity.LoginBusinessActivity;
import com.pencil.pencil.businessbook.R;
import com.pencil.pencil.businessbook.Util.PrefManager;

public class HomeActivity extends AppCompatActivity {

    private Button mCreateYourBusinesBtn;
    private Button mSearchBusinessBtn;
    private PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bindVariable();
        prefManager = new PrefManager(this);
        prefManager.setLanguage("eng");

        listener();
    }

    private void bindVariable() {

        mCreateYourBusinesBtn=findViewById(R.id.createBusinessBtn);
        mSearchBusinessBtn=findViewById(R.id.searchBusinessBtn);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.lang_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.engItem:
                prefManager.setLanguage("eng");
                return true;
            case R.id.arbItem:
                prefManager.setLanguage("arb");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void listener(){

        mCreateYourBusinesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, LoginBusinessActivity.class));
               // finish();
            }});
        mSearchBusinessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }});
    }
}
