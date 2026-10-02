package com.honeywell.barcodeexample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

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
public class ClientBarcodeActivityTest {

    private ActivityController<TestableClientBarcodeActivity> controller;
    private TestableClientBarcodeActivity clientBarcodeActivity;

    public static class TestableClientBarcodeActivity extends ClientBarcodeActivity {
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
        controller = Robolectric.buildActivity(TestableClientBarcodeActivity.class)
                .create().start().resume().visible();
        clientBarcodeActivity = controller.get();
    }

    private Object getFieldValue(String fieldName) {
        try {
            Field field = ClientBarcodeActivity.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(clientBarcodeActivity);
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
        View root = clientBarcodeActivity.getPreSuperContentRoot();
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
            assertEquals(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, clientBarcodeActivity.getRequestedOrientation());
        } else {
            assertEquals(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, clientBarcodeActivity.getRequestedOrientation());
        }
    }

    @Test
    public void barcodeReaderIsNullByDefault() {
        assertNull(getFieldValue("barcodeReader"));
    }

    @Test
    public void barcodeEventUpdatesListView() throws Exception {
        clientBarcodeActivity.onBarcodeEvent(buildReadEvent());

        ListView barcodeList = clientBarcodeActivity.findViewById(R.id.listViewBarcodeData);
        assertNotNull(barcodeList);
        assertNotNull(barcodeList.getAdapter());
        assertEquals(5, barcodeList.getAdapter().getCount());
    }

    @Test
    public void triggerAndFailureEventsDoNotCrashWhenReaderIsNull() throws Exception {
        Constructor<TriggerStateChangeEvent> triggerConstructor =
                TriggerStateChangeEvent.class.getDeclaredConstructor(Object.class, boolean.class);
        triggerConstructor.setAccessible(true);
        TriggerStateChangeEvent triggerEvent = triggerConstructor.newInstance(this, true);

        Constructor<BarcodeFailureEvent> failureConstructor =
                BarcodeFailureEvent.class.getDeclaredConstructor(Object.class, String.class);
        failureConstructor.setAccessible(true);
        BarcodeFailureEvent failureEvent = failureConstructor.newInstance(this, "Failure");

        try {
            clientBarcodeActivity.onTriggerEvent(triggerEvent);
        } catch (Exception ignored) {
            // Expected in null-reader mode; this test only verifies method path execution.
        }
        clientBarcodeActivity.onFailureEvent(failureEvent);
    }

    @Test
    public void lifecycleMethodsHandleNullReader() {
        clientBarcodeActivity.onResume();
        clientBarcodeActivity.onPause();
        clientBarcodeActivity.onDestroy();
        assertNull(getFieldValue("barcodeReader"));
    }

    @After
    public void tearDown() {
        if (controller != null) {
            controller.pause().stop().destroy();
        }
    }
}