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
import com.evan.checkinwizard.util.Constants;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckUpFragment extends DialogFragment {
    @BindView(R.id.et_reason)
    TextInputEditText etReason;

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
        View view = inflater.inflate(R.layout.fragment_check_up_reason, container, false);
        ButterKnife.bind(this, view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        return view;
    }

    @OnClick(R.id.btn_back)
    void onBack() {
        this.dismiss();
    }

    @OnClick(R.id.btn_next)
    void onNext() {
        String reason = etReason.getText().toString().trim();
        viewModel.setReason(reason);
        DialogFragment paymentFragment = new PaymentFragment();
        paymentFragment.show(getChildFragmentManager(), Constants.FRAGMENT_PAYMENT);
    }
}
