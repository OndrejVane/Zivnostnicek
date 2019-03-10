package com.example.ondrejvane.zivnostnicek.activities.trader;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.database.TraderDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.model.Trader;

/**
 * A simple {@link Fragment} subclass.
 */
public class TraderShowFragment extends Fragment {

    //prvky fragmentu
    private EditText inputCompanyNameShow, inputContactPersonShow, inputTelephoneNumberShow;
    private EditText inputIdentificationNumberShow, inputTaxIdentificationNumberShow;
    private EditText inputCityShow, inputStreetShow, inputHouseNumberShow;
    private Button buttonShowMap;

    //pomocné globální proměnné
    private TraderDatabaseHelper traderDatabaseHelper;
    private int traderID;
    private Trader trader;


    public TraderShowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trader_show, container, false);
        //inicializace prvků aktivity
        initFragment(view);

        //zobrazení informací o bochodníkovi do fragmentu
        setTextToFragment();

        //nastavení listeneru pro zobrazení adresy obchodníka v mapě
        buttonShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap();
            }
        });

        // Inflate the layout for this fragment
        return view;

    }

    private void setTextToFragment() {
        inputCompanyNameShow.setText(trader.getName());
        inputContactPersonShow.setText(trader.getContactPerson());
        inputTelephoneNumberShow.setText(trader.getPhoneNumber());
        inputIdentificationNumberShow.setText(trader.getIN());
        inputTaxIdentificationNumberShow.setText(trader.getTIN());
        inputCityShow.setText(trader.getCity());
        inputStreetShow.setText(trader.getStreet());
        inputHouseNumberShow.setText(trader.getHouseNumber());
    }

    private void initFragment(View view) {
        traderDatabaseHelper = new TraderDatabaseHelper(getContext());
        traderID = Integer.parseInt(getActivity().getIntent().getExtras().get("TRADER_ID").toString());
        trader = traderDatabaseHelper.getTraderById(traderID);
        inputCompanyNameShow = view.findViewById(R.id.textInputEditTextCompanyNameShow);
        inputContactPersonShow = view.findViewById(R.id.textInputEditTextContactPersonShow);
        inputTelephoneNumberShow = view.findViewById(R.id.textInputEditTextTelephoneNumberShow);
        inputIdentificationNumberShow = view.findViewById(R.id.textInputEditTextIdentificationNumberShow);
        inputTaxIdentificationNumberShow = view.findViewById(R.id.textInputEditTextTaxIdentificationNumberShow);
        inputCityShow = view.findViewById(R.id.textInputEditTextCityShow);
        inputStreetShow = view.findViewById(R.id.textInputEditTextStreetShow);
        inputHouseNumberShow = view.findViewById(R.id.textInputEditTextHouseNumberShow);
        buttonShowMap = view.findViewById(R.id.buttonShowMap);
    }

    private void showMap() {
        String city = inputCityShow.getText().toString();
        String street = inputStreetShow.getText().toString();
        String houseNumber = inputHouseNumberShow.getText().toString();

        if (!city.isEmpty()) {
            String address = street + "," + houseNumber + "," + city;
            String map = "http://maps.google.co.in/maps?q=" + address;
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
            startActivity(i);
        } else {
            Toast.makeText(getContext(), "Message is empty", Toast.LENGTH_SHORT).show();
        }

    }

}
