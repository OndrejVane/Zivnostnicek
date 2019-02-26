package com.example.ondrejvane.zivnostnicek.activities.trader;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
    }

}
