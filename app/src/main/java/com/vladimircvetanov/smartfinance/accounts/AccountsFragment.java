package com.vladimircvetanov.smartfinance.accounts;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vladimircvetanov.smartfinance.IFragment;
import com.vladimircvetanov.smartfinance.R;
import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.favourites.AddCategoryDialogFragment;
import com.vladimircvetanov.smartfinance.favourites.IconsAdapter;
import com.vladimircvetanov.smartfinance.model.Manager;
import com.vladimircvetanov.smartfinance.model.RowDisplayable;
import com.vladimircvetanov.smartfinance.transactionRelated.ItemType;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AccountsFragment extends Fragment implements IFragment {

    private RecyclerView accountsList;
    private RecyclerView moreAccountIconsList;

    private AccountsAdapter accountsAdapter;
    private IconsAdapter iconsAdapter;

    private DBAdapter adapter;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        adapter = DBAdapter.getInstance(getActivity());
        context = getActivity();

        accountsList = (RecyclerView) view.findViewById(R.id.accounts_list);
        final ArrayList<RowDisplayable> accounts = new ArrayList<>();
        accounts.addAll(adapter.getCachedAccounts().values());

        accountsAdapter = new AccountsAdapter(accounts, getActivity(), getParentFragmentManager());
        accountsList = (RecyclerView) view.findViewById(R.id.accounts_list);
        accountsList.setAdapter(accountsAdapter);
        accountsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        iconsAdapter = new IconsAdapter(Manager.getInstance().getAllAccountIcons(), getActivity());
        moreAccountIconsList = (RecyclerView) view.findViewById(R.id.accounts_icons_list);
        moreAccountIconsList.setAdapter(iconsAdapter);
        moreAccountIconsList.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        moreAccountIconsList.addOnItemTouchListener(
                new RecyclerItemClickListener(context, moreAccountIconsList, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override public void onItemClick(View view, int position) {

                        AddCategoryDialogFragment dialog = new AddCategoryDialogFragment();
                        Bundle arguments = new Bundle();
                        int iconId = Manager.getInstance().getAllAccountIcons().get(position);

                        arguments.putInt(getString(R.string.EXTRA_ICON), iconId);
                        arguments.putString("ROW_DISPLAYABLE_TYPE", "ACCOUNT");

                        dialog.setArguments(arguments);
                        dialog.show(getParentFragmentManager(), String.valueOf(R.string.add_category_dialog));
                    }
                })
        );
        return view;
    }

    @Override
    public void updateData(RowDisplayable item, ItemType type, Boolean delete) {
        accountsAdapter.addAccount(item);
        accountsAdapter.notifyDataSetChanged();
    }
}
