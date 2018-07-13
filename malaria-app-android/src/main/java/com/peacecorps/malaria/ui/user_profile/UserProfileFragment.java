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

    public static UserProfileFragment newInstance() {
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return new UserProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.user_profile, container, false);
        context = getContext();
        if(getActivity()!=null)
            ButterKnife.bind(this, view);
        presenter = new UserProfilePresenter<>(InjectionClass.provideDataManager(context), context);
        presenter.attachView(this);
        return view;
    }

    @Override
    protected int getContentResource() {
        return R.layout.user_profile;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        presenter.setPreviousDetails();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserFragmentListener) {
            mListener = (OnUserFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

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

    @Override
    public String getUserName() {
        if (userName == null) {
            return "";
        } else {
            return userName.getText().toString().trim();
        }
    }

    @Override
    public String getUserEmail() {
        if (userEmail == null) {
            return "";
        } else {
            return userEmail.getText().toString().trim();
        }
    }

    @Override
    public String getUserAge() {
        if (userAge == null) {
            return "";
        } else {
            return userAge.getText().toString().trim();
        }
    }

    @Override
    public boolean checkAgeError() {
        if(presenter.isAgeValid())
            return true;
        else {
            userAge.setError(userAge.getHint());
            return false;
        }
    }

    @Override
    public boolean checkNameError() {
        if(presenter.testIsEmpty(getUserName())) {
            userName.setError(userName.getHint());
            return false;
        }
        return true;
    }

    @Override
    public boolean checkEmailError() {
        if(presenter.isEmailValid())
            return true;
        else {
            userEmail.setError(userEmail.getHint());
            return false;
        }
    }

    @OnClick(R.id.save_button)
    public void saveButtonListener(View view) {
        if(presenter.checkError()){
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
        // TODO: Update argument type and name
        void startHomeFragment();
    }
}
