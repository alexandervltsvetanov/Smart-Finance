package com.vladimircvetanov.smartfinance.favourites;

import com.vladimircvetanov.smartfinance.model.RowDisplayable;
import com.vladimircvetanov.smartfinance.transactionRelated.ItemType;

public interface IUpdateData {
     void sendData(RowDisplayable item, ItemType type, Boolean delete);
}
