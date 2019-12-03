package com.Get_to_know_your_city_again.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.Get_to_know_your_city_again.R;
import com.google.firebase.firestore.GeoPoint;

public class NewItemDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "NewItemDialog";

    //widgets
    private EditText eName,eDescription,eAddress;

    private TextView tAdd, tCancel;
    private GeoPoint geoPoint;
    private double latitude,longitude;

    //vars

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(style, theme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_new_item, container, false);
        eName = view.findViewById(R.id.name_item);
        eAddress = view.findViewById(R.id.address_item);
        eDescription = view.findViewById(R.id.description_item);
        tAdd = view.findViewById(R.id.add_item);
        tCancel = view.findViewById(R.id.cancel);


        tCancel.setOnClickListener(this);
        tAdd.setOnClickListener(this);

        getDialog().setTitle("Add new object");

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.add_item:{

                // insert the new note

                String name = eName.getText().toString();
                String address = eAddress.getText().toString();
                String description = eDescription.getText().toString();


                if(!name.equals("")){

                    getDialog().dismiss();
                }
                else{
                    Toast.makeText(getActivity(), "Enter a name of object", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.cancel:{
                getDialog().dismiss();
                break;
            }
        }
    }
}
