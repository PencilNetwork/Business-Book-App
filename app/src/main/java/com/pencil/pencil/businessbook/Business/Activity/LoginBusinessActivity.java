package com.pencil.pencil.businessbook.Business.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.sax.StartElementListener;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pencil.pencil.businessbook.Api.APIClient;
import com.pencil.pencil.businessbook.Api.APIInterface;
import com.pencil.pencil.businessbook.Api.ResponseBody;
import com.pencil.pencil.businessbook.Business.Pojo.Business;
import com.pencil.pencil.businessbook.Business.Pojo.BusinessProfileResponse;
import com.pencil.pencil.businessbook.Business.Pojo.ErrorBody;
import com.pencil.pencil.businessbook.Business.Pojo.GeneralResponseBody;
import com.pencil.pencil.businessbook.Business.Pojo.SignUpInput;
import com.pencil.pencil.businessbook.Business.Pojo.Owner;
import com.pencil.pencil.businessbook.R;
import com.pencil.pencil.businessbook.Util.FileUtils;
import com.pencil.pencil.businessbook.Util.PrefManager;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.pencil.pencil.businessbook.Util.FileUtils.ProgressDialog;

public class LoginBusinessActivity extends AppCompatActivity {
    //UI
    private EditText mNameET;
    private EditText mPasswordET;
    private Button mLoginBtn;
    private CheckBox mRememberCB;
    private TextView mCreateAccountTv;
    private TextView mForgetPasswordTv;

    //variable
    private Boolean mRemember = false;
    private PrefManager prefManager;
    private Context mContext;
    //LOADING_DIALOG
    private Dialog dialog;
    //static
    private static final String USER_NAME = "username";
    private static final String PASSWORD = "password";
    private static final String OWNER_ID = "owner";
    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_business);
        prefManager = new PrefManager(this);
        mContext = LoginBusinessActivity.this;
        dialog= ProgressDialog(  mContext);
        bindVariable();
        initialize();
        listener();
    }

    private void bindVariable() {
        mNameET = findViewById(R.id.userNameET);
        mPasswordET = findViewById(R.id.passwordET);
        mLoginBtn = findViewById(R.id.loginBtn);
        mRememberCB = findViewById(R.id.remember);
        mCreateAccountTv = findViewById(R.id.createAccountTv);
        mForgetPasswordTv = findViewById(R.id.forgetPasswordTv);
    }

    private void listener() {
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    if (mRemember) {
                        prefManager.setLoginData(mNameET.getText().toString().trim(), mPasswordET.getText().toString().trim());
                    } else {
                        prefManager.resetRemember();
                    }

                    login(mNameET.getText().toString().trim(), mPasswordET.getText().toString().trim(),"fdfdf");
                }
            }
        });
        mRememberCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRemember = true;
                } else
                    mRemember = false;
            }
        });
        mCreateAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopup();

            }
        });
        mForgetPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginBusinessActivity.this, ForgetPasswordActivity.class));

            }
        });

    }

    private void initialize() {
        if (prefManager.isRemember()) {
            mNameET.setText(prefManager.getUsername());
            mPasswordET.setText(prefManager.getPassword());
        }
        mCreateAccountTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        mForgetPasswordTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    private Boolean isValid(
    ) {

        Boolean valid = true;
        if (mNameET.getText().toString().trim().isEmpty()) {
            valid = false;
            mNameET.setError(getResources().getString(R.string.username_required));
            mNameET.requestFocus();
        } else if (mPasswordET.getText().toString().trim().isEmpty()) {
            valid = false;
            mPasswordET.setError(getResources().getString(R.string.password_required));
            mPasswordET.requestFocus();
        } else if (mPasswordET.getText().toString().trim().length() < 6) {
            valid = false;
            mPasswordET.setError(getResources().getString(R.string.password_validation));
            mPasswordET.requestFocus();
        }
        return valid;
    }

    public void showPopup() {

        final EditText userName;
        final EditText email;
        final EditText password;
        Button okBtn;


        final AlertDialog.Builder myDialog = new AlertDialog.Builder(mContext);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        myDialog.setTitle(getResources().getString(R.string.create_your_profile));
        View dialogView = inflater.inflate(R.layout.signup_popup, null);
        myDialog.setView(dialogView);

        userName = dialogView.findViewById(R.id.userNameET);
        email = dialogView.findViewById(R.id.emailET);
        password = dialogView.findViewById(R.id.passwordET);

        okBtn = (Button) dialogView.findViewById(R.id.okBtn);
        final AlertDialog alertDialog = myDialog.create();
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidSignUp(userName,
                        password, email)) {


                    dialog.show();
                    SignUp(userName.getText().toString().trim(), email.getText().toString().trim(), password.getText().toString().trim(), "ewrtet",alertDialog);
                    // startActivity(new Intent(LoginBusinessActivity.this, CreateBusinessActivity.class));
                }
            }
        });

        alertDialog.show();
    }

    private Boolean isValidSignUp(EditText username, EditText password, EditText email
    ) {
        String emailStr = email.getText().toString().trim();

        String emailPattern = FileUtils.EMAIL_PATTERN;
        Boolean valid = true;
        if (username.getText().toString().trim().isEmpty()) {
            valid = false;
            username.setError(getResources().getString(R.string.username_required));
            username.requestFocus();
        } else if (password.getText().toString().trim().isEmpty()) {
            valid = false;
            password.setError(getResources().getString(R.string.password_required));
            password.requestFocus();
        } else if (password.getText().toString().trim().length() < 6) {
            valid = false;
            password.setError(getResources().getString(R.string.password_validation));
            password.requestFocus();
        } else if (emailStr.isEmpty()) {
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

    private void SignUp(final String username, final String email, final String password, String token,final AlertDialog alertDialog) {
        disableSSLCertificateChecking();
        //APIInterface service = APIClient.getApiService();
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        SignUpInput signUpInput=new SignUpInput(username,password,email,token);
        Call<ResponseBody> call = apiInterface.signup(signUpInput);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (dialog != null)
                    dialog.dismiss();
                if (response.isSuccessful()) {

//                    dialog.dismiss();
                    ResponseBody body = response.body();
                    Owner data = body.data;
                    if (data != null) {
                        prefManager.setData(USER_NAME, username);
                        prefManager.setData(EMAIL, email);
                        prefManager.setData(PASSWORD, password);
                        prefManager.setDataInt(OWNER_ID, data.getOwner_id());
                        prefManager.commit();
                        alertDialog.dismiss();
                        startActivity(new Intent(LoginBusinessActivity.this, CreateBusinessActivity.class));
                        Log.d("XXXX", "sucess");
                    } else {
                        if (body.flage.equals("0")) {

                        }
                        Log.d("XXXX", "fail");
                    }

                } else {
                    Gson gson = new Gson();
                    String s ="";
                    try {
                        s = response.errorBody().string();


                        ErrorBody errorBody = gson.fromJson(s, ErrorBody.class);
                        if (errorBody.errors != null) {
                            ArrayList<String> emailArr = errorBody.errors.email;
                            if (emailArr != null) {

                                if (emailArr.size() > 0) {
                                    Toast.makeText(mContext, errorBody.errors.email.get(0), Toast.LENGTH_LONG).show();

                                }

                            } else if (errorBody.errors.name != null) {
                                if (errorBody.errors.name.size() > 0) {
                                    Toast.makeText(mContext, errorBody.errors.name.get(0), Toast.LENGTH_LONG).show();

                                }
                            } else if (errorBody.errors.token != null) {
                                if (errorBody.errors.token.size() > 0) {
                                    Toast.makeText(mContext, errorBody.errors.token.get(0), Toast.LENGTH_LONG).show();

                                }
                            }
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (JsonSyntaxException e){
                        e.printStackTrace();
                        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

                try {
                    if(dialog!=null)
                        dialog.dismiss();
//                    dialog.dismiss();
//                    SnackBar(getContext(), "Connection error !");
                    Log.e("failed", t.getMessage()+call.request().url());
                    Toast.makeText(mContext, "Connection error", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private static void disableSSLCertificateChecking() {

        System.out.println("INSIDE DISABLE SSLC");
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void login(final String username, final String password, String token) {

        dialog.show();
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Map<String, String> params = new HashMap<>();
        params.put("name", username);

        params.put("password", password);
        params.put("token", token);

        Call<GeneralResponseBody<Owner>> call = apiInterface.login(params);

        call.enqueue(new Callback<GeneralResponseBody<Owner>>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponseBody<Owner>> call, @NonNull Response<GeneralResponseBody<Owner>> response) {
                if (dialog != null)
                    dialog.dismiss();
                if (response.isSuccessful()) {

//                    dialog.dismiss();
                    GeneralResponseBody<Owner> body = response.body();

                    Owner data = body.data;
                    if(data.getBussines()!=null) {
                        Business business = data.getBussines().get(0);
                        if (data != null) {
                            prefManager.setData(PrefManager.USER_NAME, username);

                            prefManager.setData(PrefManager.PASSWORD, password);
                            prefManager.setData(PrefManager.OWNER_ID, business.getOwner_id());
                            prefManager.setDataInt(PrefManager.BUSINESS_ID, business.getId());
                            prefManager.commit();
                            startActivity(new Intent(LoginBusinessActivity.this, BusinessProfileActivity.class));

                            Log.d("XXXX", "sucess");
                        } else {
                            if (body.flag.equals("0")) {
                                Toast.makeText(mContext, body.errors, Toast.LENGTH_LONG).show();
                            }
                            Log.d("XXXX", "fail");
                        }
                    }else
                        Toast.makeText(mContext, body.errors, Toast.LENGTH_LONG).show();
                } else {
                    Gson gson = new Gson();
                    try {
                        String s = response.errorBody().string();


                        ErrorBody errorBody = gson.fromJson(s, ErrorBody.class);
                        if(errorBody!=null){
                        if (errorBody.errors != null) {
                            ArrayList<String> emailArr = errorBody.errors.email;
                            if (emailArr != null) {

                                if (emailArr.size() > 0) {
                                    Toast.makeText(mContext, errorBody.errors.email.get(0), Toast.LENGTH_LONG).show();

                                }

                            } else if (errorBody.errors.name != null) {
                                if (errorBody.errors.name.size() > 0) {
                                    Toast.makeText(mContext, errorBody.errors.name.get(0), Toast.LENGTH_LONG).show();

                                }
                            } else if (errorBody.errors.token != null) {
                                if (errorBody.errors.token.size() > 0) {
                                    Toast.makeText(mContext, errorBody.errors.token.get(0), Toast.LENGTH_LONG).show();

                                }
                            }
                        }}else
                            Toast.makeText(mContext, "error connection", Toast.LENGTH_LONG).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponseBody<Owner>> call, @NonNull Throwable t) {

                try {
                    if(dialog!=null)
                        dialog.dismiss();

                    Log.e("failed", t.getMessage()+call.request().url());
                    Toast.makeText(mContext, "Connection error", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}