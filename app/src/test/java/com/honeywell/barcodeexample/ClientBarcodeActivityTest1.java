package com.honeywell.barcodeexample;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import android.widget.Toast;

import com.honeywell.IExecutor;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BarcodeReader.class, Toast.class})
public class ClientBarcodeActivityTest1 {

    ClientBarcodeActivity clientBarcodeActivity;
    @Mock
    IExecutor iExecutor;
    @Mock
    BarcodeReader barcodeReader;
    @Mock
    Toast toast;
    @Before
    public void setUp(){
        PowerMockito.mockStatic(BarcodeReader.class);
        PowerMockito.mockStatic(Toast.class);
        clientBarcodeActivity=new ClientBarcodeActivity();
    }

    @Test
    public void onTriggerEvent() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchFieldException, ScannerNotClaimedException, ScannerUnavailableException {
        Constructor<TriggerStateChangeEvent> constructor = TriggerStateChangeEvent.class.getDeclaredConstructor(
                Object.class, boolean.class);
        constructor.setAccessible(true);
        TriggerStateChangeEvent event = constructor.newInstance(this,true);

        Field field=ClientBarcodeActivity.class.getDeclaredField("barcodeReader");
        field.setAccessible(true);
        field.set(clientBarcodeActivity,barcodeReader);
        clientBarcodeActivity.onTriggerEvent(event);
        verify(barcodeReader).decode(event.getState());
    }
    @Test
    public void onTriggerEvent_catch() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchFieldException, ScannerNotClaimedException, ScannerUnavailableException {
        Constructor<TriggerStateChangeEvent> constructor = TriggerStateChangeEvent.class.getDeclaredConstructor(
                Object.class, boolean.class);
        constructor.setAccessible(true);
        TriggerStateChangeEvent event = constructor.newInstance(this,true);

        Field field=ClientBarcodeActivity.class.getDeclaredField("barcodeReader");
        field.setAccessible(true);
        field.set(clientBarcodeActivity,barcodeReader);
        Constructor<ScannerNotClaimedException> constructor1 = ScannerNotClaimedException.class.getDeclaredConstructor(String.class);
        constructor1.setAccessible(true);
        ScannerNotClaimedException scannerNotClaimedException = constructor1.newInstance("Error Occurs");
        PowerMockito.doThrow(scannerNotClaimedException).when(barcodeReader).decode(event.getState());
        PowerMockito.when(Toast.makeText(any(), eq("Scanner is not claimed"), eq(Toast.LENGTH_SHORT))).thenReturn(toast);
        clientBarcodeActivity.onTriggerEvent(event);
        verify(toast).show();

    }
    @Test
    public void onTriggerEvent_catch1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchFieldException, ScannerNotClaimedException, ScannerUnavailableException {
        Constructor<TriggerStateChangeEvent> constructor = TriggerStateChangeEvent.class.getDeclaredConstructor(
                Object.class, boolean.class);
        constructor.setAccessible(true);
        TriggerStateChangeEvent event = constructor.newInstance(this,true);

        Field field=ClientBarcodeActivity.class.getDeclaredField("barcodeReader");
        field.setAccessible(true);
        field.set(clientBarcodeActivity,barcodeReader);
        Constructor<ScannerUnavailableException> constructor1 = ScannerUnavailableException.class.getDeclaredConstructor(String.class);
        constructor1.setAccessible(true);
        ScannerUnavailableException scannerUnavailableException = constructor1.newInstance("Error Occurs");
        PowerMockito.doThrow(scannerUnavailableException).when(barcodeReader).decode(event.getState());
        PowerMockito.when(Toast.makeText(any(), eq("Scanner unavailable"), eq(Toast.LENGTH_SHORT))).thenReturn(toast);
        clientBarcodeActivity.onTriggerEvent(event);
        verify(toast).show();

    }
}