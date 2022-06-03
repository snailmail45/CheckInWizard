package com.evan.checkinwizard.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.braintreepayments.api.DropInClient;
import com.braintreepayments.api.DropInRequest;
import com.braintreepayments.api.GooglePayRequest;
import com.evan.checkinwizard.R;
import com.evan.checkinwizard.util.Constants;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class PaymentFragment extends DialogFragment {

    @BindView(R.id.et_amount)
    TextInputEditText etAmount;

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
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel.getAccessTokenLiveData().observe(this, accessToken -> {
            DropInRequest dropInRequest = new DropInRequest();
            DropInClient dropInClient = new DropInClient(
                    requireActivity(),
                    viewModel.getAccessToken().getClientToken(),
                    dropInRequest
            );
            dropInClient.launchDropInForResult(
                    requireActivity(),
                    Constants.BRAIN_TREE_SELECT_PAYMENT_REQUEST_CODE
            );
        });
    }

    @OnClick(R.id.btn_select_payment_type)
    void onSelectPaymentType() {
        String amount = etAmount.getText().toString().trim();
        if(amount.equals("")){
            Toast.makeText(requireContext(), "Please entire an amount", Toast.LENGTH_LONG).show();
            return;
        }
        viewModel.setAmount(amount);
        viewModel.getToken();
    }

    @OnClick(R.id.btn_back)
    void onBack() {
        this.dismiss();
    }
}
