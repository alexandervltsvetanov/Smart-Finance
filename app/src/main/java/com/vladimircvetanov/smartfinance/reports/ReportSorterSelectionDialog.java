package com.vladimircvetanov.smartfinance.reports;

import com.vladimircvetanov.smartfinance.model.Transaction;

import java.util.Comparator;

import androidx.fragment.app.DialogFragment;


public class ReportSorterSelectionDialog extends DialogFragment {

    //TODO ::IMPLEMENT::

    interface Communicator {
        void setSorter(Comparator<Transaction> sorter);
    }
}