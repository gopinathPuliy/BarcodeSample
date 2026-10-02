package com.honeywell.barcodeexample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class MainActivityInsetsTest {

    private ActivityController<TestableMainActivity> controller;
    private TestableMainActivity mainActivity;

    public static class TestableMainActivity extends MainActivity {
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
        if (Build.VERSION.SDK_INT < 35) {
            throw new AssertionError("This test must run with SDK 35 or higher.");
        }

        controller = Robolectric.buildActivity(TestableMainActivity.class)
                .create().start().resume().visible();
        mainActivity = controller.get();
    }

    @Test
    public void android15InsetsBlockAppliesExpectedPadding() {
        View root = mainActivity.getPreSuperContentRoot();
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

    @After
    public void tearDown() {
        if (controller != null) {
            controller.pause().stop().destroy();
        }
    }
}

