package com.peacecorps.malaria.activities;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.peacecorps.malaria.R;

/**
 * Created by yatna on 17/8/16.
 */
public class MedicineStoreTest extends ActivityInstrumentationTestCase2 {

    private MedicineStore mActivity;
    private TextView medicineName;
    private TextView daysLeft;
    private Button addMedicine;
    private Button orderMedicine;
    private Button settings;

    public MedicineStoreTest() {
        super("com.peacecorps.malaria.activities",MedicineStore.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        mActivity= (MedicineStore)getActivity();
        medicineName=(TextView)mActivity.findViewById(R.id.tv_medicine_name);
        daysLeft=(TextView)mActivity.findViewById(R.id.tv_days_left);
        addMedicine=(Button)mActivity.findViewById(R.id.btn_add_medicine);
        orderMedicine=(Button)mActivity.findViewById(R.id.btn_order_medicine);
        settings=(Button)mActivity.findViewById(R.id.btn_dialog_setting);

    }
    public void testMedicineNameTvExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, medicineName);
    }
    public void testDaysLeftTvExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, daysLeft);
    }
    public void testAddMedicineButtonsExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, addMedicine);
    }
    public void testOrderMedicineButtonsExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, orderMedicine);
    }
    public void testSettingsButtonsExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, settings);
    }
}