package com.evan.checkinwizard.ui;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.braintreepayments.api.DropInResult;
import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;
import com.brother.sdk.common.IConnector;
import com.brother.sdk.common.device.MediaSize;
import com.brother.sdk.scan.ScanJob;
import com.brother.sdk.scan.ScanJobController;
import com.brother.sdk.scan.ScanParameters;
import com.evan.checkinwizard.R;
import com.evan.checkinwizard.data.model.CreditCard;
import com.evan.checkinwizard.data.model.Patient;
import com.evan.checkinwizard.data.model.Purchase;
import com.evan.checkinwizard.data.model.Transaction;
import com.evan.checkinwizard.util.Constants;
import com.evan.checkinwizard.util.ScannerManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.parentLayout)
    ViewGroup parentLayout;

    private AlertDialog progressDialog;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getCheckoutLiveData().observe(this, checkoutResult -> {
            dismissAllDialogs(getSupportFragmentManager());

            View view = LayoutInflater.from(this)
                    .inflate(R.layout.layout_print, parentLayout, false);
            view.setVisibility(View.INVISIBLE);
            TextView patientInfo = view.findViewById(R.id.tv_patient_info);
            TextView date = view.findViewById(R.id.tv_date);
            TextView reason = view.findViewById(R.id.tv_reason);
            TextView txInfo = view.findViewById(R.id.tv_transaction_info);
            TextView numDocuments = view.findViewById(R.id.tv_num_documents);

            StringBuilder sb = new StringBuilder();

            Patient patient = mainViewModel.getPatient();
            sb.append(patient.getName()).append("\n")
                    .append(patient.getPhoneNumber())
                    .append("\n")
                    .append(patient.getEmail())
                    .append("\n")
                    .append("Patient Id: ")
                    .append(mainViewModel.getPatientId());
            patientInfo.setText(sb.toString());

            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_PATTERN, Locale.US);
            date.setText(dateFormat.format(System.currentTimeMillis()));
            reason.setText(mainViewModel.getReason());

            sb.setLength(0);
            sb.append("Reason for checkup:")
                    .append("\n")
                    .append(mainViewModel.getReason());
            reason.setText(sb.toString());

            sb.setLength(0);
            Transaction transaction = mainViewModel.getResult().getTransaction();
            CreditCard creditCard = transaction.getCreditCard();
            sb.append("Payment Information:")
                    .append("\n")
                    .append("Transaction Id: ")
                    .append(transaction.getId())
                    .append("\n")
                    .append(creditCard.getCardType())
                    .append("xxxx-xxxx-xxxx-")
                    .append(creditCard.getLastFour())
                    .append("\n")
                    .append("$")
                    .append(transaction.getAmount())
                    .append(" copay");
            txInfo.setText(sb.toString());

            String documents = mainViewModel.getScannedImages().size() + " images scanned";
            numDocuments.setText(documents);

            parentLayout.addView(view);
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    new Thread(() -> print(loadBitmapFromView(view))).start();
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        });

        SharedViewModel sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.getScanLiveData().observe(this, isScanning -> {
            if (isScanning) {
                scanItem();
            }
        });
    }

    @OnClick(R.id.btn_new_patient)
    void onNewPatient() {
        DialogFragment addInfoFragment = new AddInfoFragment();
        addInfoFragment.show(getSupportFragmentManager(), Constants.FRAGMENT_ADD_INFO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.BRAIN_TREE_SELECT_PAYMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                String paymentMethodNonce = result.getPaymentMethodNonce().getString();
                Purchase purchase = new Purchase(
                        paymentMethodNonce,
                        mainViewModel.getAmount()
                );
                mainViewModel.makePurchase(purchase);

            } else if (resultCode == RESULT_CANCELED) {
                Timber.d("User canceled");
            } else {
                Exception exception = (Exception) data.getSerializableExtra(DropInResult.EXTRA_ERROR);
                Timber.e("Something went wrong: " + exception);
            }
        }
    }

    private void scanItem() {
        showProgressBar();
        new Thread(() -> {
            ScannerManager.connect(ScannerManager.CONNECTION.WIFI, getApplicationContext());

            while (!ScannerManager.isConnected()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Still Connecting...");
            }
            System.out.println("Connected");
            System.out.println("Attempting scan...");

            executeImageScan(ScannerManager.createIConnector(getApplicationContext()));
        }).start();
    }

    public void executeImageScan(IConnector connector) {
        ScanJob scanJob = null;
        try {
            ScanParameters scanParameters = new ScanParameters();
            scanParameters.documentSize = MediaSize.Letter;
            scanParameters.autoDocumentSizeScan = true;
            scanJob = new ScanJob(scanParameters, this, new ScanJobController(this.getFilesDir()) {
                public void onUpdateProcessProgress(int value) {
                }

                public void onNotifyProcessAlive() {
                }

                public void onImageReadToFile(String scannedImagePath, int pageIndex) {
                    Timber.d("Image scanned: " + scannedImagePath);
                    mainViewModel.addScannedImage(scannedImagePath);
                    runOnUiThread(() -> hideProgressBar());
                }
            });
            connector.submit(scanJob);
        } catch (Exception e) {
            Timber.e("Error scanning: " + e);
            scanJob.cancel();
        } finally {
            scanJob = null;
        }
    }

    public static Bitmap loadBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        view.draw(canvas);
        return returnedBitmap;
    }

    @SuppressLint("MissingPermission")
    private static List<BluetoothDevice> getPairedBluetoothDevice(BluetoothAdapter bluetoothAdapter) {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices == null || pairedDevices.size() == 0) {
            return new ArrayList<>();
        }
        ArrayList<BluetoothDevice> devices = new ArrayList<>();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getType() != BluetoothDevice.DEVICE_TYPE_LE) {
                devices.add(device);
            }
        }
        return devices;
    }

    private static String dashToLower(String val) {
        return val.replace("-", "_");
    }

    @SuppressLint("MissingPermission")
    private void print(Bitmap bitmap) {
        try {
            Printer myPrinter = new Printer();
            PrinterInfo myPrinterInfo = myPrinter.getPrinterInfo();
            myPrinterInfo.printerModel = PrinterInfo.Model.QL_1110NWB;

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null) {
                List<BluetoothDevice> pairedDevices = getPairedBluetoothDevice(bluetoothAdapter);
                for (BluetoothDevice device : pairedDevices) {
                    String printerModel = "QL-1110NWB";
                    if (device.getName().contains(printerModel)) {
                        PrinterInfo.Model model = PrinterInfo.Model.valueOf(dashToLower(printerModel));
                        myPrinter.setBluetooth(BluetoothAdapter.getDefaultAdapter());
                        myPrinterInfo.printerModel = model;
                        myPrinterInfo.port = PrinterInfo.Port.BLUETOOTH;
                        myPrinterInfo.macAddress = device.getAddress();
                    }
                }
            }

            myPrinterInfo.orientation = PrinterInfo.Orientation.PORTRAIT;
            myPrinterInfo.printMode = PrinterInfo.PrintMode.FIT_TO_PAGE;
            myPrinterInfo.numberOfCopies = 1;
            myPrinterInfo.ipAddress = "192.168.118.1";

            myPrinterInfo.workPath = this.getFilesDir().getAbsolutePath() + "/";

            myPrinterInfo.labelNameIndex = LabelInfo.QL1100.W102.ordinal();
            myPrinterInfo.isAutoCut = true;
            myPrinterInfo.isCutAtEnd = false;
            myPrinterInfo.isHalfCut = false;
            myPrinterInfo.isSpecialTape = false;

            myPrinter.setPrinterInfo(myPrinterInfo);
            myPrinter.startCommunication();
            PrinterStatus status = myPrinter.printImage(bitmap);
            Timber.e("Error code: " + status.errorCode);
            myPrinter.endCommunication();
        } catch (Exception e) {
            Timber.e("Failed to print");
        }

    }

    private void hideProgressBar() {
        progressDialog.dismiss();
    }

    private void showProgressBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alertDialogTheme);
        builder.setView(R.layout.progress_scanning_image);
        progressDialog = builder.create();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public static void dismissAllDialogs(FragmentManager manager) {
        List<Fragment> fragments = manager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof DialogFragment) {
                DialogFragment dialogFragment = (DialogFragment) fragment;
                dialogFragment.dismissAllowingStateLoss();
            }
        }
    }

}