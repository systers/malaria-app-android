package com.peacecorps.malaria.ui.user_profile;

/*
 * Created by Anamika Tripathi on 13/7/18.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.ui.base.BaseFragment;
import com.peacecorps.malaria.utils.InjectionClass;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserProfileFragment extends BaseFragment implements UserProfileContract.UserProfileMvpView {

    private OnUserFragmentListener mListener;
    private Context context;
    //Butterknife variables
    @BindView(R.id.edit_text_user_name)
    EditText userName;
    @BindView(R.id.edit_text_user_email)
    EditText userEmail;
    @BindView(R.id.edit_text_user_age)
    EditText userAge;
    @BindView(R.id.edit_text_user_medicine_type)
    EditText userMedicationType;

    private UserProfilePresenter<UserProfileFragment> presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_profile, container, false);
        context = getContext();
        // butterknife binding
        ButterKnife.bind(this, view);
        // instantiating presenter, passing datamanger, view & attaching view to the presenter
        presenter = new UserProfilePresenter<>(InjectionClass.provideDataManager(context), context);
        presenter.attachView(this);
        return view;
    }

    @Override
    protected int getContentResource() {
        return R.layout.fragment_user_profile;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // initial set up for fragment after views are created
        init();
    }

    // setup previous details from shared preferences
    @Override
    protected void init() {
        presenter.setPreviousDetails();
    }

    // Container Activity must implement this interface
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        if (context instanceof OnUserFragmentListener) {
            mListener = (OnUserFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUserFragmentListener");
        }
    }

    // setting null values to the listener, presenter
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        presenter.detachView();
        presenter = null;
    }

    // set initial details from shared preferences
    @Override
    public void setInitialValuesIfAvailable(String name, String email, int age, String medicine) {
        if(!name.equals(""))
            userName.setText(name);
        if(!email.equals(""))
            userEmail.setText(email);
        if(age>0)
            userAge.setText(String.valueOf(age));
        userMedicationType.setText(medicine);
    }

    // checks if edit-text is null or not, returns empty string or returns the trimmed name
    @Override
    public String getUserName() {
        if (userName == null) {
            return "";
        } else {
            return userName.getText().toString().trim();
        }
    }

    // checks if edit-text is null or not, returns empty string or returns the trimmed email
    @Override
    public String getUserEmail() {
        if (userEmail == null) {
            return "";
        } else {
            return userEmail.getText().toString().trim();
        }
    }

    // checks if edit-text is null or not, returns empty string or returns the trimmed age
    @Override
    public String getUserAge() {
        if (userAge == null) {
            return "";
        } else {
            return userAge.getText().toString().trim();
        }
    }

    // checks if age is valid(it is not empty & age is >0 & <=100), returns true else set error & return false
    @Override
    public boolean checkAgeError() {
        if(presenter.isAgeValid())
            return true;
        else {
            userAge.setError(userAge.getHint());
            return false;
        }
    }

    // if name is not empty, return true else false & Set error message
    @Override
    public boolean checkNameError() {
        if(presenter.testIsEmpty(getUserName())) {
            userName.setError(userName.getHint());
            return false;
        }
        return true;
    }

    // checks email validity and return true else set error message
    @Override
    public boolean checkEmailError() {
        if(presenter.isEmailValid())
            return true;
        else {
            userEmail.setError(userEmail.getHint());
            return false;
        }
    }

    // save button listener
    @OnClick(R.id.save_button)
    public void saveButtonListener(View view) {
        if(presenter.checkError()){
            // save value in preferences
            presenter.setNewDetails(getUserName(), getUserEmail(), Integer.parseInt(getUserAge()));
            Toast.makeText(context, "Values Saved!", Toast.LENGTH_SHORT).show();
            mListener.startHomeFragment();
        }
        else Toast.makeText(context, "Enter details again!", Toast.LENGTH_SHORT).show();
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnUserFragmentListener {
        void startHomeFragment();
    }
}
