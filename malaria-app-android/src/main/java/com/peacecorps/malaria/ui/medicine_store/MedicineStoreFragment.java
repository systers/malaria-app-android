package com.peacecorps.malaria.ui.medicine_store;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.ui.base.BaseFragment;
import com.peacecorps.malaria.ui.medicine_store.MedicineStoreContract.MedicineMvpView;
import com.peacecorps.malaria.utils.InjectionClass;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Anamika Tripathi on 18/7/18.
 */
public class MedicineStoreFragment extends BaseFragment implements MedicineMvpView {

    @BindView(R.id.tv_medicine_name)
    TextView medicineName;
    @BindView(R.id.tv_days_left)
    TextView daysLeft;
    private Context context;
    private MedicineStorePresenter<MedicineStoreFragment> presenter;
    private int PERMISSIONS_REQUEST_SEND_SMS = 7;
    private boolean smsPermissionGranted = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.medicine_store, container, false);
        // set up butterknife
        ButterKnife.bind(this, view);
        context = getContext();
        // set up presenter, passing datamanger and context & attaching view to presenter
        presenter = new MedicineStorePresenter<>(InjectionClass.provideDataManager(context), context);
        presenter.attachView(this);
        return view;
    }

    @Override
    protected int getContentResource() {
        return R.layout.medicine_store;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        presenter.checkMedicineStore();
    }

    @Override
    public void displayMedicineStoreValues(String drugName, String days) {
        medicineName.setText(drugName);
        daysLeft.setText(days);
    }

    @OnClick(R.id.btn_add_medicine)
    public void addMedicineListener() {
        final Dialog addMedicineDialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        addMedicineDialog.setContentView(R.layout.add_medicine_dialog);

        // Setting Dialog Title
        final EditText medicineQuantityEt = addMedicineDialog.findViewById(R.id.add_medicine_quantity);
        addMedicineDialog.findViewById(R.id.add_medicine_quantity).requestFocus();
        addMedicineDialog.findViewById(R.id.btn_dialog_add_medicine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (presenter.checkMedicineNumberValidity(medicineQuantityEt.getText().toString().trim())) {
                    presenter.updateMedicineStoreValue(medicineQuantityEt.getText().toString().trim());
                    addMedicineDialog.dismiss();
                } else {
                    medicineQuantityEt.setError("Quantity Required");
                    medicineQuantityEt.requestFocus();
                }
            }
        });
        addMedicineDialog.findViewById(R.id.btn_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicineDialog.dismiss();
            }
        });

        // Showing Alert Message
        addMedicineDialog.show();
    }

    @OnClick(R.id.btn_order_medicine)
    public void orderMedicineListener() {
        final Dialog orderMedicineDialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        orderMedicineDialog.setContentView(R.layout.order_medicine_dialog);

        // Setting Dialog Title
        final EditText quantity = orderMedicineDialog.findViewById(R.id.btn_order_quantity);

        orderMedicineDialog.findViewById(R.id.btn_order_quantity).requestFocus();

        //implement the email button
        orderMedicineDialog.findViewById(R.id.btn_order_by_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (presenter.checkMedicineNumberValidity(quantity.getText().toString().trim())) {

                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"anamikatripathi1601@gmail.com", "yatna.verma.ece13@itbhu.ac.in"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "URGENT: Reqiured Malaria Medicines");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(
                            presenter.getMessageBodyForOrder()
                                    + "<b>" + Integer.parseInt(quantity.getText().toString()) + "</b>"));
                    startActivity(Intent.createChooser(emailIntent, "Send mail via..."));
                    orderMedicineDialog.dismiss();

                } else {
                    //send and email
                    quantity.setError("Quantity Required");
                    quantity.requestFocus();

                }
            }
        });
        //implement the message button

        orderMedicineDialog.findViewById(R.id.btn_order_by_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!presenter.checkMedicineNumberValidity(quantity.getText().toString().trim())) {
                    quantity.setError("Quantity Required");
                    quantity.requestFocus();
                } else {
                    //Entered Order quantity is greater than 0
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted
                        // Ask for permission
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.SEND_SMS},
                                    PERMISSIONS_REQUEST_SEND_SMS);

                    }
                    else{
                        smsPermissionGranted = true;
                    }

                    if(smsPermissionGranted) {
                        SmsManager smsManager = SmsManager.getDefault();
                        //Sms cannot parse html tags so remove them and use appropriate alternative
                        String message = (presenter.getMessageBodyForOrder() +Integer.parseInt(quantity.getText().toString())).toString();
                        String patternRegex = "<[/]*b>";                  // To match <b> and </b>
                        message = message.replaceAll("<br>","\n");
                        message = message.replaceAll(patternRegex," ");
                        String phoneNo = "121";
                        smsManager.sendTextMessage(phoneNo, null,message, null, null);
                        Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_LONG).show();
                        orderMedicineDialog.dismiss();
                    }
                    else{
                        //Show Toast message
                        Toast.makeText(getActivity(), "Cannot use this feature without Send SMS permission", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        // Showing Alert Message
        orderMedicineDialog.show();
    }

    // setting button listener in medicine store fragment
    @OnClick(R.id.btn_dialog_setting)
    public void settingDialogListener() {

        final Dialog settingsDialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        settingsDialog.setContentView(R.layout.reminder_screen_setting_dialog);

        // Setting Dialog Title
        final EditText time = settingsDialog.findViewById(R.id.time);
        settingsDialog.findViewById(R.id.time).requestFocus();
        TextView warningTv = settingsDialog.findViewById(R.id.warning_textview);

        //if drug is weekly
        if (presenter.isDrugWeekly()) {
            warningTv.setText(R.string.warning_reminder_weeks);
        }
        //if drug is daily
        else {
            warningTv.setText(R.string.warning_reminder_days);
        }

        //get previous value if any
        final int prevAlertTime = presenter.getAlertReminderNumber();
        //fill editText with previous set values
        if (prevAlertTime != -1) {
            time.setText(String.valueOf(prevAlertTime));
        }
        settingsDialog.findViewById(R.id.set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (presenter.checkAlertReminderValidity(time.getText().toString().trim())) {
                    presenter.updateAlertReminder(Integer.parseInt(time.getText().toString()));
                    //Todo add a reminder as per input
                    settingsDialog.dismiss();
                } else {

                    time.setError(getString(R.string.error_store_alert_reminder));
                    time.requestFocus();
                }
            }
        });
        settingsDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDialog.dismiss();
            }
        });

        // Showing Alert Message
        settingsDialog.show();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_SEND_SMS && grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
            smsPermissionGranted = true;
        }
    }

}
