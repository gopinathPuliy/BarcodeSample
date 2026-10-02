package com.honeywell.barcodeexample;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;

import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;


import com.honeywell.IExecutor;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReaderInfo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class ScannerSelectionBarcodeActivityTest1 {

    private ScannerSelectionBarcodeActivity scannerSelectionBarcodeActivity;
    //    @Mock
//    com.honeywell.aidc.AidcManager mAidcManager;
    @Mock
    Context context;
    @Mock
    ServiceConnection serviceConnection;
    @Mock
    IExecutor iExecutor;
    @Mock
    Bundle savedInstanceState;

    @Before
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        scannerSelectionBarcodeActivity= spy(new ScannerSelectionBarcodeActivity());//.create().get();
    }

    //    @Test
//    public void initialize() throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
//        Constructor<com.honeywell.aidc.AidcManager> constructor = com.honeywell.aidc.AidcManager.class.getDeclaredConstructor(
//                Context.class, ServiceConnection.class, IExecutor.class);
//        constructor.setAccessible(true);
//        com.honeywell.aidc.AidcManager mAidcManager = constructor.newInstance(context,serviceConnection,iExecutor);
//        Field field=ScannerSelectionBarcodeActivity.class.getDeclaredField("mAidcManager");
//        field.setAccessible(true);
//        field.set(scannerSelectionBarcodeActivity,mAidcManager);
//        List<BarcodeReaderInfo> scanners=new ArrayList<>();
//        //Mockito.when(mAidcManager.listConnectedBarcodeDevices()).thenReturn(scanners);
//        scannerSelectionBarcodeActivity.initialize();
//    }
    @Test
    public void onBarcodeEvent()throws Exception{
        Constructor<BarcodeReadEvent> constructor = BarcodeReadEvent.class.getDeclaredConstructor(
                Object.class, String.class, String.class, String.class, String.class, String.class, String.class);
        constructor.setAccessible(true);
        BarcodeReadEvent event = constructor.newInstance(this, "12345", "UTF-8", "Code128", "AIM_ID", "2025-02-07T11:35:00", "LABEL_TYPE");
        scannerSelectionBarcodeActivity.onBarcodeEvent(event);
        try {
            ShadowLooper.runUiThreadTasks();
        }catch (Exception e){}
        Assert.assertNotNull(event.getBarcodeData());
    }
    @Test
    public void onTriggerEvent(){
        scannerSelectionBarcodeActivity.onTriggerEvent(null);
    }
    @Test
    public void onFailureEvent(){
        scannerSelectionBarcodeActivity.onFailureEvent(null);
    }
    @Test
    public void scannerSelection() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<BarcodeReaderInfo> scanners=new ArrayList<>();
        Method method=ScannerSelectionBarcodeActivity.class.getDeclaredMethod("scannerSelection", List.class);
        method.setAccessible(true);
        method.invoke(scannerSelectionBarcodeActivity,scanners);

    }
}
