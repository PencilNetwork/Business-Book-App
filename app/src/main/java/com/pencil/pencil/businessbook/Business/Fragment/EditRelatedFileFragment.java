package com.pencil.pencil.businessbook.Business.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.pencil.pencil.businessbook.Api.APIClient;
import com.pencil.pencil.businessbook.Api.APIInterface;
import com.pencil.pencil.businessbook.Business.Activity.BusinessProfileActivity;
import com.pencil.pencil.businessbook.Business.Activity.CreateBusinessActivity;
import com.pencil.pencil.businessbook.Business.Adapter.OfferAdapter;
import com.pencil.pencil.businessbook.Business.Adapter.RelatedFileAdapter;
import com.pencil.pencil.businessbook.Business.Interface.EditProfileInterface;
import com.pencil.pencil.businessbook.Business.Pojo.CreateBusinessInput;
import com.pencil.pencil.businessbook.Business.Pojo.GeneralResponseBody;
import com.pencil.pencil.businessbook.Business.Pojo.Offer;
import com.pencil.pencil.businessbook.Business.Pojo.RelatedFile;
import com.pencil.pencil.businessbook.R;
import com.pencil.pencil.businessbook.Util.FileUtils;
import com.pencil.pencil.businessbook.Util.PrefManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

public class EditRelatedFileFragment extends Fragment {
    //UI
    private RecyclerView mRelatedFileRecyclerView;
    private RelatedFileAdapter mRelatedFileAdapter;
    private LinearLayout mRelatedFileLinear;
    private ImageView mRelatedFileIv;
    private Button mConfirmBtn;
    //variable
    private Context mContext;
    private static int REQUEST_GALARY = 1;
    private PrefManager mPrefManager;
    private ArrayList<File> mFiles=new ArrayList<>();
    private int mFileIndex=-1;
    //LOADING_DIALOG
    private Dialog dialog;

    public EditRelatedFileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_related_file, container, false);
        bindVariable(view);
        initialize();

        listener();
        return view;
    }

    private void bindVariable(View view) {

        mRelatedFileRecyclerView = view.findViewById(R.id.relatedFileRecycleView);
        mRelatedFileLinear = view.findViewById(R.id.relatedFileLinear);
        mRelatedFileIv = view.findViewById(R.id.relatedFileIv);
        mConfirmBtn=view.findViewById(R.id.confirmBtn);
    }

    private void initialize() {
        mContext = getActivity();
        dialog= ProgressDialog(  mContext);
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();

        int width = display.getWidth();
        mRelatedFileIv.getLayoutParams().width = width / 2;
        mRelatedFileIv.getLayoutParams().height = width / 2;


        mRelatedFileRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));


        mRelatedFileAdapter = new RelatedFileAdapter(mContext,new ArrayList<RelatedFile>(), true);

        mRelatedFileRecyclerView.setAdapter(mRelatedFileAdapter);
        mPrefManager = new PrefManager(mContext);
    }

    private void listener() {
        mRelatedFileLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, REQUEST_GALARY);
            }
        });
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRelatedFile();
            }});
    }

    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALARY) {

                try {
                Uri selectedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), selectedImage);
                //Bitmap bitmap1 = BitmapFactory.decodeFile(selectedImage.getPath());




                mFileIndex++;




               File file = new File(FileUtils.getRealPathFromURI(mContext, FileUtils.getImageUri(mContext, bitmap)));

                    mFiles.add(file);

                    RelatedFile relatedFile = new RelatedFile(bitmap);
                    mRelatedFileAdapter.add(relatedFile);
                    mRelatedFileIv.setVisibility(View.GONE);
                    mRelatedFileRecyclerView.setVisibility(View.VISIBLE);


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

    private void addRelatedFile() {

        dialog.show();
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        ArrayList<MultipartBody.Part> multiPartList = new ArrayList<>();
        MultipartBody.Part filePart;

        for (int i = 0; i < mFiles.size(); i++) {
            filePart = MultipartBody.Part.createFormData("image[]", mFiles.get(i).getName(), RequestBody.create(MediaType.parse("image/*"), mFiles.get(i)));
            multiPartList.add(filePart);
        }
        RequestBody busseniss_id = RequestBody.create(MediaType.parse("text/plain"), mPrefManager.getDataInt(PrefManager.BUSINESS_ID) + "");

        Call<GeneralResponseBody<Object>> call = apiInterface.addRelatedFile(multiPartList, busseniss_id);

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
