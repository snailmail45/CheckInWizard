package com.evan.checkinwizard.util;

import android.content.Context;

import com.brother.sdk.BrotherAndroidLib;
import com.brother.sdk.common.ConnectorDescriptor;
import com.brother.sdk.common.ConnectorManager;
import com.brother.sdk.common.IConnector;
import com.brother.sdk.common.device.CountrySpec;
import com.brother.sdk.network.NetworkControllerManager;
import com.brother.sdk.network.discovery.mfc.BrotherMFCNetworkConnectorDiscovery;
import com.brother.sdk.network.wifi.WifiNetworkController;
import com.brother.sdk.usb.discovery.UsbConnectorDiscovery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Credits to Lazaro
 * https://github.com/WriteCodeEveryday/Colorizer
 */
public class ScannerManager {
    private static List<ConnectorDescriptor>                    mDescriptors;
    private static UsbConnectorDiscovery                        mUsbDiscovery;
    private static BrotherMFCNetworkConnectorDiscovery          mNetworkDiscovery;
    private static ConnectorManager.OnDiscoverConnectorListener mDiscoverConnectorListener;

    public enum CONNECTION {WIFI, USB}

    public static void connect(CONNECTION conn, Context ctx) {
        if (conn == CONNECTION.WIFI) {
            discover();

            ArrayList<String> addresses = new ArrayList<>();
            //WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
            //DhcpInfo info = wifi.getDhcpInfo();
            //addresses.add(intToIp(info.gateway)); //TODO Look into WiFi Direct.

            BrotherAndroidLib.initialize(ctx);
            WifiNetworkController controller = NetworkControllerManager.getWifiNetworkController();
            addresses.add(controller.getBroadcastAddress().getHostAddress());

            executeNetworkDeviceDiscovery(addresses);
        } else if (conn == CONNECTION.USB) {
            discover();
            executeUsbDeviceDiscovery();
        }
    }

    public static boolean isConnected() {
        boolean connected = mDescriptors != null && mDescriptors.size() > 0;
        System.out.println("isConnected: " + connected);
        return connected;
    }

    public static IConnector createIConnector(Context context) {
        if (isConnected()) {
            IConnector connector = mDescriptors.get(0)
                    .createConnector(
                            CountrySpec
                                    .fromISO_3166_1_Alpha2(
                                            context
                                                    .getResources()
                                                    .getConfiguration()
                                                    .locale
                                                    .getCountry()
                                    )
                    );
            System.out.println("createIConnector: success");
            return connector;
        }
        System.out.println("createIConnector: failure");
        return null;
    }

    private static void discover() {
        if (mDiscoverConnectorListener != null) {
            return;
        }
        System.out.println("discover()");
        mDiscoverConnectorListener = new ConnectorManager.OnDiscoverConnectorListener() {
            @Override
            public void onDiscover(ConnectorDescriptor descriptor) {
                if (descriptor.support(ConnectorDescriptor.Function.Scan)) {
                    if (mDescriptors == null) {
                        System.out.println("discover() creating descriptors");
                        mDescriptors = new ArrayList<ConnectorDescriptor>();
                    }

                    if (!mDescriptors.contains(descriptor)) {
                        System.out.println("discover() adding descriptor");
                        mDescriptors.add(descriptor);
                    }
                }
            }
        };
    }


    private static void executeUsbDeviceDiscovery() {
        System.out.println("executeUsbDeviceDiscovery()");
        mUsbDiscovery = new UsbConnectorDiscovery();
        mUsbDiscovery.startDiscover(mDiscoverConnectorListener);
    }

    private static void executeNetworkDeviceDiscovery(List<String> searchAddresses) {
        System.out.println("executeNetworkDeviceDiscovery(): " + Arrays.toString(searchAddresses.toArray()));
        mNetworkDiscovery = new BrotherMFCNetworkConnectorDiscovery(searchAddresses);
        mNetworkDiscovery.startDiscover(mDiscoverConnectorListener);
    }
}
