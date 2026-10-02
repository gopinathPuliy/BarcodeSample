package com.honeywell.barcodeexample;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.widget.Toast;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.InvalidScannerNameException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.UnsupportedPropertyException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AidcManager.class,BarcodeReader.class, Toast.class})
public class ScannerSelectionBarcodeActivityTest {

    @Mock
    com.honeywell.aidc.AidcManager mAidcManager;
    @Mock
    Toast toast;
    @Mock
    BarcodeReader mBarcodeReader;
    @Mock
    Context context;
    @Mock
    BarcodeReader.BarcodeListener barcodeListener;
    private ScannerSelectionBarcodeActivity scannerSelectionBarcodeActivity;
    @Before
    public void setUp(){
        scannerSelectionBarcodeActivity= new ScannerSelectionBarcodeActivity();
    }
    @Test
    public void testclaimBarcodeReader(){
        scannerSelectionBarcodeActivity.claimBarcodeReader();
    }

    @Test(expected = RuntimeException.class)
    public void testInstilaze() throws NoSuchFieldException, IllegalAccessException {
        PowerMockito.mockStatic(AidcManager.class);
        Field field=ScannerSelectionBarcodeActivity.class.getDeclaredField("mAidcManager");
        field.setAccessible(true);
        field.set(scannerSelectionBarcodeActivity,mAidcManager);
        scannerSelectionBarcodeActivity.initialize();
    }
    @Test
    public void claimBarcodeReader() throws NoSuchFieldException, IllegalAccessException, ScannerUnavailableException {
        PowerMockito.mockStatic(BarcodeReader.class);
        Field field=ScannerSelectionBarcodeActivity.class.getDeclaredField("mBarcodeReader");
        field.setAccessible(true);
        field.set(scannerSelectionBarcodeActivity,mBarcodeReader);
        scannerSelectionBarcodeActivity.claimBarcodeReader();
        verify(mBarcodeReader).claim();
    }

    @Test(expected = NullPointerException.class)
    public void testcreateBarcodeReaderConnection() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {

        PowerMockito.mockStatic(Toast.class);
        PowerMockito.mockStatic(BarcodeReader.class);
        PowerMockito.mockStatic(AidcManager.class);
        Field field=ScannerSelectionBarcodeActivity.class.getDeclaredField("mBarcodeReader");
        field.setAccessible(true);
        field.set(scannerSelectionBarcodeActivity,mBarcodeReader);
        Field field1=ScannerSelectionBarcodeActivity.class.getDeclaredField("mAidcManager");
        field1.setAccessible(true);
        field1.set(scannerSelectionBarcodeActivity,mAidcManager);
        Method method=ScannerSelectionBarcodeActivity.class.getDeclaredMethod("createBarcodeReaderConnection",String.class);
        method.setAccessible(true);
        method.invoke(scannerSelectionBarcodeActivity,"testing");
    }
    @Test
    public void claimBarcodeReader_catch()throws Exception{
        PowerMockito.mockStatic(BarcodeReader.class);
        PowerMockito.mockStatic(Toast.class);
        Field field=ScannerSelectionBarcodeActivity.class.getDeclaredField("mBarcodeReader");
        field.setAccessible(true);
        field.set(scannerSelectionBarcodeActivity,mBarcodeReader);
        Constructor<ScannerUnavailableException> constructor=ScannerUnavailableException.class.getDeclaredConstructor(String.class);
        constructor.setAccessible(true);
        ScannerUnavailableException scannerUnavailableException=constructor.newInstance("Error occurs");
        PowerMockito.doThrow(scannerUnavailableException).when(mBarcodeReader).claim();
        PowerMockito.when(Toast.makeText(any(),eq("Scanner unavailable"),eq( Toast.LENGTH_SHORT))).thenReturn(toast);
        scannerSelectionBarcodeActivity.claimBarcodeReader();
        verify(toast).show();
    }
    @Test
    public void createBarcodeReaderConnection() throws Exception {
        PowerMockito.mockStatic(Toast.class);
        Field field=ScannerSelectionBarcodeActivity.class.getDeclaredField("mConnectedScanner");
        field.setAccessible(true);
        field.set(scannerSelectionBarcodeActivity,"testing");
        Method method=ScannerSelectionBarcodeActivity.class.getDeclaredMethod("createBarcodeReaderConnection",String.class);
        method.setAccessible(true);
        Field field1=ScannerSelectionBarcodeActivity.class.getDeclaredField("mAidcManager");
        field1.setAccessible(true);
        field1.set(scannerSelectionBarcodeActivity,mAidcManager);
        PowerMockito.when(mAidcManager.createBarcodeReader(eq("test"))).thenReturn(mBarcodeReader);

        //setFinalInstanceField(scannerSelectionBarcodeActivity, "mContext", null);
        ReflectionHelpers.setField(scannerSelectionBarcodeActivity, "mContext", null);

        method.invoke(scannerSelectionBarcodeActivity,"test");
        verify(mBarcodeReader).addBarcodeListener(null);
    }
    @Test
    public void createBarcodeReaderConnection_catch() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, InvalidScannerNameException, InstantiationException {
        PowerMockito.mockStatic(Toast.class);
        Field field=ScannerSelectionBarcodeActivity.class.getDeclaredField("mConnectedScanner");
        field.setAccessible(true);
        field.set(scannerSelectionBarcodeActivity,"testing");
        Field field1=ScannerSelectionBarcodeActivity.class.getDeclaredField("mAidcManager");
        field1.setAccessible(true);
        field1.set(scannerSelectionBarcodeActivity,mAidcManager);
        PowerMockito.when(Toast.makeText(any(), any(), eq(Toast.LENGTH_SHORT))).thenReturn(toast);
        Method method=ScannerSelectionBarcodeActivity.class.getDeclaredMethod("createBarcodeReaderConnection",String.class);
        method.setAccessible(true);
        try {
            method.invoke(scannerSelectionBarcodeActivity,"test");
        }catch (Exception e){}
    }
    public static void setFinalInstanceField(Object instance, String fieldName, Object newValue) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(instance, newValue);
    }

}