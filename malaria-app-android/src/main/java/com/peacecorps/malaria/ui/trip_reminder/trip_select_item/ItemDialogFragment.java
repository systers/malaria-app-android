package com.peacecorps.malaria.ui.trip_reminder.trip_select_item;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.data.db.entities.Packing;
import com.peacecorps.malaria.ui.trip_reminder.trip_select_item.ItemAdapter.ItemOnClickListener;
import com.peacecorps.malaria.ui.trip_reminder.trip_select_item.ItemDialogContract.ItemDialogMvpView;
import com.peacecorps.malaria.utils.InjectionClass;
import com.peacecorps.malaria.utils.ToastLogSnackBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Anamika Tripathi on 31/7/18.
 */
public class ItemDialogFragment extends DialogFragment implements ItemDialogMvpView, ItemOnClickListener {
    private ItemDialogPresenter<ItemDialogFragment> presenter;
    private Context context;
    private ItemAdapter itemAdapter;
    private OnSaveDialogListener listener;

    private long medQuantity;

    @BindView(R.id.select_drug_name)
    EditText etSelectDrug;
    @BindView(R.id.et_packing_item)
    EditText etPackingItem;
    @BindView(R.id.tv_drug_quantity)
    TextView tvMedicineQuantity;
    @BindView(R.id.et_total_cash)
    EditText etTotalCash;

    /**
     * @param num : medicine quantity calculated by departure & arrival date
     * @return : instance of this fragment
     */
    public static ItemDialogFragment newInstance(long num) {
        ItemDialogFragment f = new ItemDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putLong("MED_QUANTITY", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // take medQuantity if available in bundle
        if (getArguments() != null) {
            medQuantity = getArguments().getLong("MED_QUANTITY");
        } else {
            ToastLogSnackBarUtil.showErrorLog("ItemDialogFragment: getArguments is null");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View view = inflater.inflate(R.layout.fragment_select_trip_item, container, false);

        setUpToolBar(view);
        ButterKnife.bind(this, view);
        return view;
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {

            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);

        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyFullScreenDialogTheme);
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // instantiate presenter and attach view
        context = getContext();
        presenter = new ItemDialogPresenter<>(InjectionClass.provideDataManager(context), context);
        presenter.attachView(this);

        if (presenter.isViewAttached()) {
            init(view);
        } else {
            ToastLogSnackBarUtil.showErrorLog("ItemDialogFragment: View not attached");
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        // clear activity's menu and attach menu_ok_cancel
        if (getActivity() != null) {
            getActivity().getMenuInflater().inflate(R.menu.menu_ok_cancel, menu);
        } else {
            ToastLogSnackBarUtil.showErrorLog("ItemDialogFragment: getActivity() is null");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            // save menu button clicked
            saveButtonListener();
            return true;
        } else if (id == android.R.id.home) {
            // close dialog fragment
            dismiss();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Container Activity i.e MainActivity must implement this interface
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception - showing message via Error log
        if (context instanceof OnSaveDialogListener) {
            listener = (OnSaveDialogListener) context;
        } else {
            // Show error Log for debugging & display snackbar to user
            ToastLogSnackBarUtil.showErrorLog(context.toString() + " must implement OnSaveDialogListener");
            if (getActivity() != null) {
                ToastLogSnackBarUtil.showSnackBar(context, getActivity().findViewById(android.R.id.content),
                        "Something went wrong!");
            }
        }
    }

    // called when data changed in recycler view
    @Override
    public void notifyAdapterForUpdate() {
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * @param quantity : calculated no of drugs to be packed
     */
    @Override
    public void setDrugQuantity(int quantity) {
        tvMedicineQuantity.setText(context.getString(R.string.blank_text, quantity));
    }

    // passes value via callback and dismisses fragment
    @Override
    public void closeDialog(Packing packing) {
        listener.passMedicineSelected(packing);
        dismiss();
    }

    // called if delete button is clicked in recycler view - deletes rows except first row i.e medicines & notifies adapter
    @Override
    public void onDeleteRowListener(int position) {

        if (position > 0) {
            presenter.deletePackingRow(position);
            itemAdapter.notifyItemRemoved(position);
            itemAdapter.notifyItemRangeChanged(position, presenter.getItemList().size());
        } else {
            if (getActivity() != null) {
                ToastLogSnackBarUtil.showSnackBar(context, getActivity().findViewById(android.R.id.content), "Pills are compulsory selection!");
            } else {
                ToastLogSnackBarUtil.showErrorLog("ItemDialogFragment: getActivity() is null");
            }
        }

    }

    @Override
    public boolean isNetworkConnected() {
        // not required to check here
        return false;
    }

    private void init(View view) {
        // starts single select dialog for medicine
        addDrugSelectDialog();
        // setup recycler view which contains item list
        setUpRecyclerView(view);
    }

    /**
     * sets on touch listener on drop-down button, calls @showMedicineSelectDialog if touched
     */
    @SuppressLint("ClickableViewAccessibility")
    private void addDrugSelectDialog() {

        etSelectDrug.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP &&
                        (event.getRawX() >= (etSelectDrug.getRight() - etSelectDrug.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))) {
                    // creating a single item select dialog with medicine names
                    showMedicineSelectDialog();
                    return true;
                }
                return false;
            }


        });
    }

    // sets up custom tool bar for dialog fragment
    private void setUpToolBar(View view) {
        Toolbar toolbar = view.findViewById(R.id.select_item_toolbar);
        ActionBar actionBar;

        toolbar.setTitle("Select Trip Item");
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.action_cancel);
            }
        }
        setHasOptionsMenu(true);
    }

    private void setUpRecyclerView(View view) {
        ToastLogSnackBarUtil.showDebugLog("set up recycler view");
        RecyclerView itemRecyclerView = view.findViewById(R.id.trip_packing_list);

        // use this setting to improve performance of the RecyclerView
        itemRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        itemRecyclerView.setLayoutManager(mLayoutManager);

        SimpleItemDividerDecoration mDividerItemDecoration = new SimpleItemDividerDecoration(itemRecyclerView.getContext());
        itemRecyclerView.addItemDecoration(mDividerItemDecoration);

        // specify an adapter (see also next example)
        itemAdapter = new ItemAdapter(presenter, this);
        itemRecyclerView.setAdapter(itemAdapter);
    }

    private void showMedicineSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setTitle(R.string.label_medicine_selector);

        //list of items
        final String[] items = getResources().getStringArray(R.array.array_drugs);
        builder.setSingleChoiceItems(items, 2,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // item selected logic
                        if (checkViewNotNull(etSelectDrug)) {
                            etSelectDrug.setText(items[which]);
                            presenter.calculateDrugQuantity(which, medQuantity);
                        } else {
                            ToastLogSnackBarUtil.showErrorLog("ItemDialogFragment: Edit text is null");
                        }
                    }
                });

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Todo  which is -1 here no matter which item is selected, resolve the problem

                        if (checkViewNotNull(etSelectDrug) && checkViewNotNull(tvMedicineQuantity)) {
                            String item = etSelectDrug.getText().toString();
                            if (!presenter.testIsEmpty(item)) {
                                /*
                                 * tvMedicineQuantity can't be empty as etSelectDrug is not empty
                                 */
                                int quantity = Integer.parseInt(tvMedicineQuantity.getText().toString());
                                presenter.replaceMedicineName(item, quantity);
                            } else {
                                etPackingItem.setError("Add Item");
                            }
                        } else {
                            ToastLogSnackBarUtil.showErrorLog("ItemDialogFragment: etSelectDrug/tvMedicineQuantity is null");
                        }

                        dialog.dismiss();
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    @OnClick(R.id.btn_add_item)
    public void btnAddItemListener() {
        if (checkViewNotNull(etPackingItem)) {
            String item = etPackingItem.getText().toString();
            if (!presenter.testIsEmpty(item)) {
                // adds value to list & refresh list, clear the edit text
                presenter.addToPackingList(item);
                ToastLogSnackBarUtil.showDebugLog("Adding item");
                etPackingItem.getText().clear();
            } else {
                etPackingItem.setError("Add Item");
            }
        } else {
            ToastLogSnackBarUtil.showErrorLog("ItemDialogFragment: etPackingItem is null");
        }


    }

    private boolean checkViewNotNull(View view) {
        return view != null;
    }

    private void saveButtonListener() {
        boolean isValid = checkCashValidity();
        if (isValid) {
            /*
             * input type is number, can parse string without exception
             * add valid cash amount to db
             */
            presenter.addCashToPacking(Integer.parseInt(etTotalCash.getText().toString()));
            ToastLogSnackBarUtil.showToast(context, "saved");

        }
        presenter.getMedDetailsAndCloseDialog();
    }

    // returns false if etTotalCash is null or empty else returns false
    private boolean checkCashValidity() {
        if (checkViewNotNull(etTotalCash)) {
            String cashText = etTotalCash.getText().toString();
            return !presenter.testIsEmpty(cashText);
        } else {
            ToastLogSnackBarUtil.showErrorLog("ItemDialogFragment: etTotalCash is null");
        }
        return false;
    }

    // implemented by MainActivity - used for callback to pass packing details - used in PlanTripFragment
    public interface OnSaveDialogListener {
        void passMedicineSelected(Packing packing);
    }
}
