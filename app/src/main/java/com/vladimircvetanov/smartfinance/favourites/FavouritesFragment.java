package com.vladimircvetanov.smartfinance.favourites;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vladimircvetanov.smartfinance.IFragment;
import com.vladimircvetanov.smartfinance.R;
import com.vladimircvetanov.smartfinance.accounts.RecyclerItemClickListener;
import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.model.CategoryExpense;
import com.vladimircvetanov.smartfinance.model.Manager;
import com.vladimircvetanov.smartfinance.model.RowDisplayable;
import com.vladimircvetanov.smartfinance.transactionRelated.ItemType;

import java.util.ArrayList;
import java.util.Collection;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavouritesFragment extends Fragment implements IFragment {

    private RecyclerView favouritesList;
    private RecyclerView additionalIconsList;
    private RecyclerView allCategoriesList;

    private TextView moreIconsTitle;
    private TextView favouritesTitle;
    private TextView allCategoriesTitle;
    private DBAdapter adapter;

    private RowDisplayableAdapter rowDisplayableAdapter;
    private RowDisplayableAdapter allCategoriesRowDisplayableAdapter;
    private IconsAdapter iconsAdapter;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favourites, container, false);

        adapter = DBAdapter.getInstance(getActivity());
        favouritesTitle = (TextView) root.findViewById(R.id.fav_icons_title);
        allCategoriesTitle = (TextView) root.findViewById(R.id.all_categories_title);
        moreIconsTitle = (TextView) root.findViewById(R.id.more_icons_title);
        context = getActivity();

        ArrayList<RowDisplayable> categories = new ArrayList<>();
        categories.addAll(adapter.getCachedFavCategories().values());

        rowDisplayableAdapter = new RowDisplayableAdapter(categories, getActivity());
        favouritesList = (RecyclerView) root.findViewById(R.id.favourites_list);
        favouritesList.setAdapter(rowDisplayableAdapter);
        favouritesList.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        allCategoriesList = (RecyclerView) root.findViewById(R.id.all_categories_list);
        final ArrayList<RowDisplayable> allCategories = new ArrayList<>();
        allCategories.addAll(adapter.getCachedExpenseCategories().values());
        allCategoriesRowDisplayableAdapter = new RowDisplayableAdapter(allCategories, getActivity());
        allCategoriesList.setAdapter(allCategoriesRowDisplayableAdapter);
        allCategoriesList.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        allCategoriesList.addOnItemTouchListener(
                new RecyclerItemClickListener(context, allCategoriesList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        CategoryExpense cat = (CategoryExpense) allCategories.get(position);
                        AddToFavDeleteDialogFragment dialog = new AddToFavDeleteDialogFragment();
                        Bundle arguments = new Bundle();
                        int iconId = Manager.getInstance().getAllExpenseIcons().get(position);

                        arguments.putInt("KEY_ICON", iconId);
                        arguments.putSerializable("ROW_DISPLAYABLE_TYPE", cat);

                        dialog.setArguments(arguments);
                        dialog.show(getParentFragmentManager(), "Add to favorite dialog");

                    }
                })
        );

        iconsAdapter = new IconsAdapter(Manager.getInstance().getAllExpenseIcons(), getActivity());
        additionalIconsList = (RecyclerView) root.findViewById(R.id.additional_icons_list);
        additionalIconsList.setAdapter(iconsAdapter);
        additionalIconsList.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        additionalIconsList.addOnItemTouchListener(
            new RecyclerItemClickListener(context, additionalIconsList, new RecyclerItemClickListener.OnItemClickListener() {

                @Override
                public void onItemClick(View view, int position) {

                    AddCategoryDialogFragment dialog = new AddCategoryDialogFragment();
                    Bundle arguments = new Bundle();
                    int iconId = Manager.getInstance().getAllExpenseIcons().get(position);

                    arguments.putInt("KEY_ICON", iconId);
                    arguments.putInt("POSITION", position);
                    arguments.putString("ROW_DISPLAYABLE_TYPE", "CATEGORY");

                    dialog.setArguments(arguments);
                    dialog.show(getParentFragmentManager(), "Add category dialog");
                }

            }));
        return root;
    }

    @Override
    public void updateData(RowDisplayable item, ItemType type, Boolean delete) {

        if(delete) {
            allCategoriesRowDisplayableAdapter.removeCategory(item);
            allCategoriesRowDisplayableAdapter.notifyDataSetChanged();
            return;
        }

        Collection<CategoryExpense> testCat = adapter.getCachedFavCategories().values();

        if(type == ItemType.FAVOURITECATEGORY) {
            allCategoriesRowDisplayableAdapter.removeCategory(item);
            allCategoriesRowDisplayableAdapter.notifyDataSetChanged();
            rowDisplayableAdapter.addCategory(item);
            rowDisplayableAdapter.notifyItemInserted(rowDisplayableAdapter.getItemCount());
            rowDisplayableAdapter.getHolder().removeButton.setVisibility(View.GONE);
            rowDisplayableAdapter.getHolder().image.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
        } else if(type == ItemType.CATEGORY) {
            allCategoriesRowDisplayableAdapter.addCategory(item);
            allCategoriesRowDisplayableAdapter.notifyDataSetChanged();
        }
    }
}
