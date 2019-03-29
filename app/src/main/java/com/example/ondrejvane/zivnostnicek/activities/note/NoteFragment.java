package com.example.ondrejvane.zivnostnicek.activities.note;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.adapters.ListViewTraderAdapter;
import com.example.ondrejvane.zivnostnicek.database.ModelHelpers.NoteDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.utilities.ArrayUtility;

/**
 * Fragment pro zobrazení náhledu poznámek vybraného uživatele a
 * přůměnrého hodnocení.
 */
public class NoteFragment extends Fragment {

    //grafické prvky fragmentu
    private ListView listViewNote;
    private ListViewTraderAdapter listViewTraderAdapter;
    private RatingBar ratingBar;
    private TextView averageRating;

    //pomocné globální proměnné
    private int traderID;
    private NoteDatabaseHelper noteDatabaseHelper;
    private String[] noteTitle;
    private String[] noteRating;
    private int[] ID;
    private int globalPosition;


    /**
     * Prazdný konstruktor, který fragment vyžaduje.
     */
    public NoteFragment() {
        // Required empty public constructor
    }


    /**
     * Meotda, která je volána při vytvoření fragmentu a provede úkoný
     * k inicializaci fragmentu.
     *
     * @param inflater           inflater
     * @param container          container
     * @param savedInstanceState savedInstanceState
     * @return view aktivity
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        //inicializace fragmentu
        initFragment(view);

        //po stisknutí vybraného listview překne do aktivity pro zobrazní poznámky
        listViewNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                globalPosition = position;
                Intent intent = new Intent(getActivity(), NoteShowActivity.class);
                intent.putExtra("NOTE_ID", ID[globalPosition]);
                intent.putExtra("TRADER_ID", traderID);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    /**
     * Inializace všech prvků fragmentu.
     *
     * @param view view aktivtiy
     */
    private void initFragment(View view) {
        traderID = Integer.parseInt(getActivity().getIntent().getExtras().get("TRADER_ID").toString());
        noteDatabaseHelper = new NoteDatabaseHelper(getContext());
        ratingBar = view.findViewById(R.id.noteShowRatingBar);
        averageRating = view.findViewById(R.id.textViewNoteAvarageRating);
        String temp[][];
        temp = noteDatabaseHelper.getNotesData(traderID);
        ID = ArrayUtility.arrayStringToInteger(temp[0]);
        noteTitle = temp[1];
        noteRating = temp[2];

        listViewNote = view.findViewById(R.id.listViewTrader);
        listViewTraderAdapter = new ListViewTraderAdapter(getActivity(), noteTitle, noteRating);
        listViewNote.setAdapter(listViewTraderAdapter);

        float rating = ArrayUtility.countAverageRating(noteRating);
        ratingBar.setRating(rating);
        averageRating.append(Float.toString(rating));
    }

}
