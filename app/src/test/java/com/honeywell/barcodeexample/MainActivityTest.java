package com.honeywell.barcodeexample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.widget.Button;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class MainActivityTest {

    private ActivityController<MainActivity> controller;
    private MainActivity mainActivity;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(MainActivity.class)
                .create().start().resume().visible();
        mainActivity = controller.get();
    }


    @Test
    public void buttonsArePresent() {
        assertNotNull(mainActivity.findViewById(R.id.buttonAutomaticBarcode));
        assertNotNull(mainActivity.findViewById(R.id.buttonClientBarcode));
        assertNotNull(mainActivity.findViewById(R.id.buttonScannerSelectBarcode));
    }


    @Test
    public void automaticBarcodeButtonStartsCorrectIntent() {
        ((Button) mainActivity.findViewById(R.id.buttonAutomaticBarcode)).performClick();

        ShadowActivity shadow = Shadows.shadowOf(mainActivity);
        Intent started = shadow.getNextStartedActivity();

        assertNotNull(started);
        assertEquals("android.intent.action.AUTOMATICBARCODEACTIVITY", started.getAction());
    }

    @Test
    public void clientBarcodeButtonStartsCorrectIntent() {
        ((Button) mainActivity.findViewById(R.id.buttonClientBarcode)).performClick();

        ShadowActivity shadow = Shadows.shadowOf(mainActivity);
        Intent started = shadow.getNextStartedActivity();

        assertNotNull(started);
        assertEquals("android.intent.action.CLIENTBARCODEACTIVITY", started.getAction());
    }

    @Test
    public void scannerSelectButtonStartsCorrectIntent() {
        ((Button) mainActivity.findViewById(R.id.buttonScannerSelectBarcode)).performClick();

        ShadowActivity shadow = Shadows.shadowOf(mainActivity);
        Intent started = shadow.getNextStartedActivity();

        assertNotNull(started);
        assertEquals("android.intent.action.SCANNERSELECTBARCODEACTIVITY", started.getAction());
    }


    @Test
    public void orientationMatchesDeviceModel() {
        if (Build.MODEL.startsWith("VM1A")) {
            assertEquals(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
                    mainActivity.getRequestedOrientation());
        } else {
            assertEquals(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
                    mainActivity.getRequestedOrientation());
        }
    }


    @After
    public void tearDown() {
        if (controller != null) {
            controller.pause().stop().destroy();
        }
    }
}