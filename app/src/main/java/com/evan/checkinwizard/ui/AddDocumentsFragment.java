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
import androidx.recyclerview.widget.RecyclerView;

import com.evan.checkinwizard.R;
import com.evan.checkinwizard.adapters.DocumentAdapter;
import com.evan.checkinwizard.util.Constants;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddDocumentsFragment extends DialogFragment implements
        DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder> {

    @BindView(R.id.recycler)
    DiscreteScrollView recycler;

    private DocumentAdapter adapter;
    private SharedViewModel sharedViewModel;

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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_add_documents, container, false);
        ButterKnife.bind(this, view);

        MainViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getImagesLiveData().observe(this, b -> {
            if(b){
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new DocumentAdapter(viewModel.getScannedImages());
        recycler.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        recycler.addOnItemChangedListener(this);
        recycler.setSlideOnFling(true);
        recycler.setAdapter(adapter);

        return view;
    }

    @OnClick(R.id.btn_back)
    void onBack(){
        this.dismiss();
    }

    @OnClick(R.id.btn_next)
    void onNext(){
        DialogFragment checkUpFragment = new CheckUpFragment();
        checkUpFragment.show(getChildFragmentManager(), Constants.FRAGMENT_CHECK_UP);
    }

    @OnClick(R.id.btn_scan_document)
    void scan(){
        sharedViewModel.scanDocument();
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int i) {

    }
}
