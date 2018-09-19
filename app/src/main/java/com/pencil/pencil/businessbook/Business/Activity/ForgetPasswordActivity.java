package com.pencil.pencil.businessbook.Business.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pencil.pencil.businessbook.Api.APIClient;
import com.pencil.pencil.businessbook.Api.APIInterface;
import com.pencil.pencil.businessbook.Business.Pojo.Business;
import com.pencil.pencil.businessbook.Business.Pojo.ErrorBody;
import com.pencil.pencil.businessbook.Business.Pojo.GeneralResponseBody;
import com.pencil.pencil.businessbook.Business.Pojo.Owner;
import com.pencil.pencil.businessbook.R;
import com.pencil.pencil.businessbook.Util.FileUtils;
import com.pencil.pencil.businessbook.Util.PrefManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.pencil.pencil.businessbook.Util.FileUtils.ProgressDialog;

public class ForgetPasswordActivity extends AppCompatActivity {
    private Context mContext;
    private Dialog dialog;
    //UI variable
    @BindView(R.id.emailET)
    public EditText mEmailET;
    @BindView(R.id.send)
    public Button mSendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        mContext = ForgetPasswordActivity.this;
        ButterKnife.bind(this);
        dialog = ProgressDialog(mContext);
        listener();
    }

    private void listener() {
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid(mEmailET)) {
                    forgetPassword();
                }
            }
        });
    }

    private void forgetPassword() {

        dialog.show();
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<GeneralResponseBody<Object>> call = apiInterface.forgetPassword(mEmailET.getText().toString().trim());

        call.enqueue(new Callback<GeneralResponseBody<Object>>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponseBody<Object>> call, @NonNull Response<GeneralResponseBody<Object>> response) {
                if (dialog != null)
                    dialog.dismiss();
                if (response.isSuccessful()) {

//                    dialog.dismiss();
                    GeneralResponseBody<Object> body = response.body();


                    if (body.flag.equals("0")) {
                        Toast.makeText(mContext, body.errors, Toast.LENGTH_LONG).show();
                    }else   Toast.makeText(mContext, "mail is send ", Toast.LENGTH_LONG).show();
                    Log.d("XXXX", "fail");


                } else {
                    Gson gson = new Gson();

                    Toast.makeText(mContext, "error connection", Toast.LENGTH_LONG).show();


                }
            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponseBody<Object>> call, @NonNull Throwable t) {

                try {
                    if (dialog != null)
                        dialog.dismiss();

                    Log.e("failed", t.getMessage() + call.request().url());
                    Toast.makeText(mContext, "Connection error", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private Boolean isValid(EditText email
    ) {
        String emailStr = email.getText().toString().trim();

        String emailPattern = FileUtils.EMAIL_PATTERN;
        Boolean valid = true;
        if (emailStr.isEmpty()) {
            valid = false;
            email.setError(getResources().getString(R.string.email_required));
            email.requestFocus();
        } else if (!emailStr.matches(emailPattern)) {
            valid = false;
            email.setError(getResources().getString(R.string.email_validation));
            email.requestFocus();
        }
        return valid;
    }

}
