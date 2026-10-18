package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Lumina AI", appName)
  }

  @Test
  fun `verify presets catalog contains presets`() {
    val presets = com.example.model.PresetsCatalog.allPresets
    org.junit.Assert.assertTrue(presets.isNotEmpty())
    val restorationPresets = presets.filter { it.category == com.example.model.PresetCategory.RESTAURACION }
    org.junit.Assert.assertTrue(restorationPresets.isNotEmpty())
  }
}
