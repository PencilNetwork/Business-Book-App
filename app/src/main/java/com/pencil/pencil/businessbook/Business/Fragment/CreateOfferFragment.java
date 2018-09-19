package com.pencil.pencil.businessbook.Business.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pencil.pencil.businessbook.Api.APIClient;
import com.pencil.pencil.businessbook.Api.APIInterface;
import com.pencil.pencil.businessbook.Business.Adapter.OfferAdapter;
import com.pencil.pencil.businessbook.Business.Adapter.RelatedFileAdapter;
import com.pencil.pencil.businessbook.Business.Pojo.GeneralResponseBody;
import com.pencil.pencil.businessbook.Business.Pojo.Offer;
import com.pencil.pencil.businessbook.Business.Pojo.RelatedFile;
import com.pencil.pencil.businessbook.R;
import com.pencil.pencil.businessbook.Util.FileUtils;
import com.pencil.pencil.businessbook.Util.PrefManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.pencil.pencil.businessbook.Util.FileUtils.ProgressDialog;

public class CreateOfferFragment extends Fragment {

    //UI variable
    private RecyclerView mOfferRecyclerView;
    private LinearLayout mOfferLinear;

    private Context mContext;
    private OfferAdapter mOfferAdapter;
    private ImageView offerIv;
    private EditText mCaptionET;
    private Button mConfirmBtn;
    //variable
    private static int REQUEST_GALARY = 1;
    private File mSelectedFile;
    private PrefManager mPrefManager;
    private Dialog  dialog;

    public CreateOfferFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_offer, container, false);
        mContext = getActivity();
        bindVariable(v);

        initialize();
        Listener();
        return v;
    }

    private void bindVariable(View view) {

        mOfferLinear = view.findViewById(R.id.offerLinear);
        offerIv = view.findViewById(R.id.offerIv);
        mCaptionET = view.findViewById(R.id.captionET);
        mConfirmBtn= view.findViewById(R.id.confirmBtn);
    }

    private void initialize() {
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();

        int width = display.getWidth();

        offerIv.getLayoutParams().width = width / 2;
        offerIv.getLayoutParams().height = width / 2;
        mPrefManager = new PrefManager(mContext);
        dialog= ProgressDialog(  mContext);

    }

    private void Listener() {
        mOfferLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, REQUEST_GALARY);
            }
        });
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCaptionET.getText().toString().trim().length()>0){
                    if(mSelectedFile!=null){
                        createOffer();
                    }else
                        Toast.makeText(mContext, getResources().getText(R.string.enter_image), Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(mContext, getResources().getText(R.string.enter_caption), Toast.LENGTH_LONG).show();
            }});
    }

    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALARY) {


                Uri selectedImage = data.getData();
                // h=1;
                //imgui = selectedImage;

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), selectedImage);


                    mSelectedFile = new File(FileUtils.getRealPathFromURI(mContext, FileUtils.getImageUri(mContext, bitmap)));

                    offerIv.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    private void createOffer() {

        dialog.show();
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", mSelectedFile.getName(), RequestBody.create(MediaType.parse("image/*"), mSelectedFile));
        RequestBody caption = RequestBody.create(MediaType.parse("text/plain"), mCaptionET.getText().toString().trim());

        RequestBody busseniss_id = RequestBody.create(MediaType.parse("text/plain"), mPrefManager.getDataInt(PrefManager.BUSINESS_ID) + "");

        Call<GeneralResponseBody<Object>> call = apiInterface.createOffer(filePart, caption,busseniss_id);

        call.enqueue(new Callback<GeneralResponseBody<Object>>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponseBody<Object>> call, @NonNull Response<GeneralResponseBody<Object>> response) {
                if(dialog!=null)
                    dialog.dismiss();
                if (response.isSuccessful()) {

//                    dialog.dismiss();
                    GeneralResponseBody<Object> body = response.body();

                    String flag = body.flag;
                    if (flag.equals("1")) {
                        Toast.makeText(mContext, getResources().getText(R.string.upload_sucess), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, "failed to Upload offer", Toast.LENGTH_LONG).show();
                    }
                    Log.v("XXXX", body.toString());

                } else {
                    String s = null;
                    try {
                        s = response.errorBody().string();Log.v("XXXX",  s);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponseBody<Object>> call, @NonNull Throwable t) {

                try {
                    if(dialog!=null)
                        dialog.dismiss();

                    Log.e("failed", t.getMessage() + call.request().url());
                    Toast.makeText(mContext, "Connection error", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
