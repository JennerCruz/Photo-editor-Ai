package com.example

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.PresetsCatalog
import com.example.ui.components.AiRestorationSheet
import com.example.ui.components.BeforeAfterSlider
import com.example.ui.components.ManualAdjustmentsSheet
import com.example.ui.components.PerfectionModesSheet
import com.example.ui.components.PresetSelector
import com.example.ui.components.SamplePhotosSheet
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.LuminaCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.EditorUiState
import com.example.viewmodel.PhotoEditorViewModel
import com.example.viewmodel.ToolTab
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    private val viewModel: PhotoEditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.initializeWithContext(this)

        setContent {
            MyApplicationTheme {
                LuminaPhotoEditorApp(
                    viewModel = viewModel,
                    onShareBitmap = { bitmap -> sharePhoto(bitmap) }
                )
            }
        }
    }

    private fun sharePhoto(bitmap: Bitmap) {
        try {
            val cachePath = File(cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "lumina_restored.jpg")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
            stream.close()

            val contentUri: Uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Compartir Foto Restaurada"))
        } catch (e: Exception) {
            Toast.makeText(this, "Guardando localmente: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuminaPhotoEditorApp(
    viewModel: PhotoEditorViewModel,
    onShareBitmap: (Bitmap) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Android System Photo Picker (Zero Permissions Required)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.loadFromUri(context, uri)
        }
    }

    // Handle Toast / Notifications
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            EditorTopBar(
                uiState = uiState,
                onUndo = { viewModel.undo() },
                onRedo = { viewModel.redo() },
                onReset = { viewModel.resetAdjustments() },
                onHoldCompare = { comparing -> viewModel.setComparingOriginal(comparing) },
                onSave = { viewModel.saveImageToGallery(context) }
            )
        },
        bottomBar = {
            EditorBottomNavigationBar(
                activeTab = uiState.activeTab,
                onSelectTab = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Preview Canvas with Interactive Before/After Split Slider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.05f)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                BeforeAfterSlider(
                    originalBitmap = uiState.originalBitmap,
                    processedBitmap = uiState.processedBitmap,
                    splitPosition = uiState.splitPosition,
                    onSplitPositionChange = { viewModel.updateSplitPosition(it) },
                    isComparingOriginal = uiState.isComparingOriginal
                )

                // Current Active Effect Pill
                CurrentActiveEffectBadge(
                    uiState = uiState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp)
                )
            }

            // Bottom Tool Panel (Tabs: Presets, AI Restoration, Perfection, Adjustments, Photos)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.95f),
                color = DarkSurface,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                AnimatedContent(
                    targetState = uiState.activeTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "ToolPanelTransition"
                ) { tab ->
                    when (tab) {
                        ToolTab.PRESETS -> {
                            PresetSelector(
                                selectedPreset = uiState.selectedPreset,
                                presetIntensity = uiState.presetIntensity,
                                onSelectPreset = { viewModel.selectPreset(it) },
                                onIntensityChange = { viewModel.updatePresetIntensity(it) },
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }

                        ToolTab.AI_RESTORATION -> {
                            AiRestorationSheet(
                                aiDiagnostic = uiState.aiDiagnostic,
                                isAnalyzing = uiState.isAnalyzingAi,
                                isDeepRestoring = uiState.isDeepRestoringAi,
                                onRunDiagnosis = { viewModel.runAiDiagnosis() },
                                onApplyAiAdjustments = { viewModel.applyAiRecommendedAdjustments() },
                                onRunDeepRestoration = { viewModel.runDeepAiRestoration() },
                                onApplyPresetById = { id ->
                                    val preset = PresetsCatalog.allPresets.find { it.id == id }
                                    viewModel.selectPreset(preset)
                                }
                            )
                        }

                        ToolTab.PERFECCION -> {
                            PerfectionModesSheet(
                                activeMode = uiState.activePerfectionMode,
                                onApplyMode = { viewModel.applyPerfectionMode(it) }
                            )
                        }

                        ToolTab.MANUAL -> {
                            ManualAdjustmentsSheet(
                                adjustments = uiState.currentAdjustments,
                                onUpdateAdjustment = { transform ->
                                    viewModel.updateSingleAdjustment(transform)
                                },
                                onResetAll = { viewModel.resetAdjustments() }
                            )
                        }

                        ToolTab.MUESTRAS -> {
                            SamplePhotosSheet(
                                currentTitle = uiState.currentPhotoTitle,
                                onLoadSample = { type -> viewModel.loadSamplePhoto(context, type) },
                                onPickFromGallery = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorTopBar(
    uiState: EditorUiState,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onReset: () -> Unit,
    onHoldCompare: (Boolean) -> Unit,
    onSave: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF38E1FF), Color(0xFFD946EF))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF0D0F17),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Lumina AI",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Restauración & Presets",
                        fontSize = 10.sp,
                        color = Color(0xFF38E1FF)
                    )
                }
            }
        },
        actions = {
            // Undo Button
            IconButton(
                onClick = onUndo,
                enabled = uiState.canUndo,
                modifier = Modifier.testTag("undo_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Deshacer",
                    tint = if (uiState.canUndo) Color.White else Color.White.copy(alpha = 0.25f)
                )
            }

            // Redo Button
            IconButton(
                onClick = onRedo,
                enabled = uiState.canRedo,
                modifier = Modifier.testTag("redo_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Redo,
                    contentDescription = "Rehacer",
                    tint = if (uiState.canRedo) Color.White else Color.White.copy(alpha = 0.25f)
                )
            }

            // Reset Button
            IconButton(
                onClick = onReset,
                modifier = Modifier.testTag("reset_button")
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = "Restablecer",
                    tint = Color(0xFFFFB74D)
                )
            }

            // Hold To Compare Original Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E2438))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                onHoldCompare(true)
                                tryAwaitRelease()
                                onHoldCompare(false)
                            }
                        )
                    }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("hold_compare_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ver",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Save / Export Button
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LuminaCyan,
                    contentColor = Color(0xFF071520)
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .testTag("save_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Guardar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBackground
        )
    )
}

@Composable
private fun CurrentActiveEffectBadge(
    uiState: EditorUiState,
    modifier: Modifier = Modifier
) {
    val activeLabel = when {
        uiState.selectedPreset != null -> "${uiState.selectedPreset.iconEmoji} Preset: ${uiState.selectedPreset.name} (${(uiState.presetIntensity * 100).toInt()}%)"
        uiState.activePerfectionMode != null -> "${uiState.activePerfectionMode.iconEmoji} Perfección: ${uiState.activePerfectionMode.title}"
        !uiState.currentAdjustments.isDefault -> "🎛️ Ajustes Personalizados Activos"
        else -> "⚪ Foto Original"
    }

    Surface(
        color = Color(0xCC111522),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B334E)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = activeLabel,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EditorBottomNavigationBar(
    activeTab: ToolTab,
    onSelectTab: (ToolTab) -> Unit
) {
    NavigationBar(
        containerColor = DarkSurface,
        tonalElevation = 6.dp,
        modifier = Modifier
            .navigationBarsPadding()
            .testTag("bottom_navigation_bar")
    ) {
        ToolTab.values().forEach { tab ->
            val isSelected = tab == activeTab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectTab(tab) },
                icon = {
                    Text(
                        text = tab.iconEmoji,
                        fontSize = 18.sp
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = LuminaCyan,
                    selectedTextColor = LuminaCyan,
                    unselectedIconColor = Color.White.copy(alpha = 0.5f),
                    unselectedTextColor = Color.White.copy(alpha = 0.5f),
                    indicatorColor = Color(0xFF1E2D44)
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}
