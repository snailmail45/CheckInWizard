package com.evan.checkinwizard.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.evan.checkinwizard.R;
import com.evan.checkinwizard.data.model.Patient;
import com.evan.checkinwizard.util.Constants;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddInfoFragment extends DialogFragment {

    @BindView(R.id.et_name)
    TextInputEditText etName;

    @BindView(R.id.et_birthday)
    TextInputEditText etBirthday;

    @BindView(R.id.et_height)
    TextInputEditText etHeight;

    @BindView(R.id.et_weight)
    TextInputEditText etWeight;

    @BindView(R.id.et_ethnicity)
    TextInputEditText etEthnicity;

    @BindView(R.id.et_phone_number)
    TextInputEditText etPhoneNum;

    @BindView(R.id.et_email)
    TextInputEditText etEmail;

    @BindView(R.id.et_emergency_contact)
    TextInputEditText etEmergencyContact;

    private MainViewModel viewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_CheckInWizard);
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.FragmentDialogAnim;
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_info, container, false);
        ButterKnife.bind(this, view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        return view;
    }


    @OnClick(R.id.btn_next)
    void onNextBtnClick(){
        String name = etName.getText().toString().trim();
        String dateOfBirth = etBirthday.getText().toString().trim();
        String height = etHeight.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();
        String ethnicity = etEthnicity.getText().toString().trim();
        String number = etPhoneNum.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String emergencyContact = etEmergencyContact.getText().toString().trim();
        Patient patient = new Patient.Builder().name(name)
                .dateOfBirth(dateOfBirth)
                .height(height)
                .weight(weight)
                .ethnicity(ethnicity)
                .phoneNumber(number)
                .email(email)
                .emergencyContact(emergencyContact)
                .build();
        viewModel.setPatient(patient);

        DialogFragment fragmentAddDocuments = new AddDocumentsFragment();
        fragmentAddDocuments.show(getChildFragmentManager(), Constants.FRAGMENT_ADD_DOCUMENTS);
    }

    @OnClick(R.id.btn_back)
    void onBack(){
        this.dismiss();
    }
}
