package com.vladimircvetanov.smartfinance;

import com.vladimircvetanov.smartfinance.model.RowDisplayable;
import com.vladimircvetanov.smartfinance.transactionRelated.ItemType;

public interface IFragment {
    void updateData(RowDisplayable item, ItemType type, Boolean delete);
}
