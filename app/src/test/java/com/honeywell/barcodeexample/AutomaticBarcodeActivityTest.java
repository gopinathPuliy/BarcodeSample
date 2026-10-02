package com.honeywell.barcodeexample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ToggleButton;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.TriggerStateChangeEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class AutomaticBarcodeActivityTest {

    private ActivityController<TestableAutomaticBarcodeActivity> controller;
    private TestableAutomaticBarcodeActivity automaticBarcodeActivity;

    public static class TestableAutomaticBarcodeActivity extends AutomaticBarcodeActivity {
        private final FrameLayout preSuperContentRoot = new FrameLayout(RuntimeEnvironment.getApplication());

        @Override
        @SuppressWarnings("unchecked")
        public <T extends View> T findViewById(int id) {
            if (id == android.R.id.content) {
                return (T) preSuperContentRoot;
            }
            return super.findViewById(id);
        }

        View getPreSuperContentRoot() {
            return preSuperContentRoot;
        }
    }

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(TestableAutomaticBarcodeActivity.class)
                .create().start().resume().visible();
        automaticBarcodeActivity = controller.get();
    }

    private Object getFieldValue(String fieldName) {
        try {
            Field field = AutomaticBarcodeActivity.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(automaticBarcodeActivity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    private BarcodeReadEvent buildReadEvent() throws Exception {
        Constructor<BarcodeReadEvent> constructor = BarcodeReadEvent.class.getDeclaredConstructor(
                Object.class, String.class, String.class, String.class, String.class, String.class, String.class);
        constructor.setAccessible(true);
        return constructor.newInstance(this, "12345", "UTF-8", "Code128", "AIM_ID", "2025-02-07T11:35:00", "LABEL_TYPE");
    }

    @Test
    public void android15InsetsBlockAppliesExpectedPadding() {
        View root = automaticBarcodeActivity.getPreSuperContentRoot();
        assertNotNull(root);

        WindowInsetsCompat insets = new WindowInsetsCompat.Builder()
                .setInsets(
                        WindowInsetsCompat.Type.statusBars() | WindowInsetsCompat.Type.navigationBars(),
                        Insets.of(0, 10, 0, 20))
                .build();

        ViewCompat.dispatchApplyWindowInsets(root, insets);

        assertEquals(40, root.getPaddingTop());
        assertEquals(20, root.getPaddingBottom());
    }

    @Test
    public void orientationMatchesDeviceModel() {
        if (Build.MODEL.startsWith("VM1A")) {
            assertEquals(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, automaticBarcodeActivity.getRequestedOrientation());
        } else {
            assertEquals(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, automaticBarcodeActivity.getRequestedOrientation());
        }
    }

    @Test
    public void barcodeReaderIsNullByDefault() {
        assertNull(getFieldValue("barcodeReader"));
    }

    @Test
    public void barcodeEventUpdatesListView() throws Exception {
        automaticBarcodeActivity.onBarcodeEvent(buildReadEvent());

        ListView barcodeList = automaticBarcodeActivity.findViewById(R.id.listViewBarcodeData);
        assertNotNull(barcodeList);
        assertNotNull(barcodeList.getAdapter());
        assertEquals(5, barcodeList.getAdapter().getCount());
    }

    @Test
    public void toggleContinuousDoesNotCrashWhenReaderIsNull() {
        ToggleButton toggle = automaticBarcodeActivity.findViewById(R.id.toggleContinuous);
        assertNotNull(toggle);

        toggle.performClick();
        toggle.performClick();

        // Reader remains null and UI stays usable.
        assertNull(getFieldValue("barcodeReader"));
    }

    @Test
    public void triggerAndFailureEventsDoNotCrash() throws Exception {
        Constructor<TriggerStateChangeEvent> triggerConstructor =
                TriggerStateChangeEvent.class.getDeclaredConstructor(Object.class, boolean.class);
        triggerConstructor.setAccessible(true);
        TriggerStateChangeEvent triggerEvent = triggerConstructor.newInstance(this, true);

        Constructor<BarcodeFailureEvent> failureConstructor =
                BarcodeFailureEvent.class.getDeclaredConstructor(Object.class, String.class);
        failureConstructor.setAccessible(true);
        BarcodeFailureEvent failureEvent = failureConstructor.newInstance(this, "Failure");

        automaticBarcodeActivity.onTriggerEvent(triggerEvent);
        automaticBarcodeActivity.onFailureEvent(failureEvent);
    }

    @Test
    public void lifecycleMethodsHandleNullReader() {
        automaticBarcodeActivity.onResume();
        automaticBarcodeActivity.onPause();
        automaticBarcodeActivity.onDestroy();
        assertNull(getFieldValue("barcodeReader"));
    }

    @After
    public void tearDown() {
        if (controller != null) {
            controller.pause().stop().destroy();
        }
    }
}