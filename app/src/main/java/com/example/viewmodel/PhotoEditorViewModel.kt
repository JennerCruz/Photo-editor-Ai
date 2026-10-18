package com.example.viewmodel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.ai.GeminiRestorationService
import com.example.model.AdjustmentParameters
import com.example.model.AiDiagnostic
import com.example.model.PerfectionMode
import com.example.model.PhotoPreset
import com.example.model.PresetsCatalog
import com.example.processing.ImageProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

enum class ToolTab(val label: String, val iconEmoji: String) {
    PRESETS("Presets", "🎨"),
    AI_RESTORATION("Restaurar IA", "✨"),
    PERFECCION("Perfección", "⚡"),
    MANUAL("Ajustes", "🎛️"),
    MUESTRAS("Fotos", "🖼️")
}

data class EditorUiState(
    val originalBitmap: Bitmap? = null,
    val processedBitmap: Bitmap? = null,
    val currentAdjustments: AdjustmentParameters = AdjustmentParameters(),
    val baseAdjustments: AdjustmentParameters = AdjustmentParameters(),
    val selectedPreset: PhotoPreset? = null,
    val presetIntensity: Float = 1.0f,
    val activePerfectionMode: PerfectionMode? = null,
    val activeTab: ToolTab = ToolTab.PRESETS,
    val splitPosition: Float = 0.5f,
    val isComparingOriginal: Boolean = false,
    val isAnalyzingAi: Boolean = false,
    val isDeepRestoringAi: Boolean = false,
    val aiDiagnostic: AiDiagnostic? = null,
    val toastMessage: String? = null,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val currentPhotoTitle: String = "Muestra Antigua (1940)"
)

class PhotoEditorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private val undoStack = mutableListOf<AdjustmentParameters>()
    private val redoStack = mutableListOf<AdjustmentParameters>()
    private var processingJob: Job? = null

    fun initializeWithContext(context: Context) {
        if (_uiState.value.originalBitmap == null) {
            loadSamplePhoto(context, "vintage")
        }
    }

    fun loadSamplePhoto(context: Context, type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resId = when (type) {
                    "modern" -> R.drawable.sample_modern
                    else -> R.drawable.sample_vintage
                }
                val title = if (type == "modern") "Muestra Urbana (Moderna)" else "Muestra Antigua (1940)"
                val options = BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 }
                val bitmap = BitmapFactory.decodeResource(context.resources, resId, options)
                if (bitmap != null) {
                    withContext(Dispatchers.Main) {
                        setNewOriginalBitmap(bitmap, title)
                    }
                }
            } catch (e: Exception) {
                Log.e("PhotoEditorVM", "Error loading sample photo", e)
            }
        }
    }

    fun loadFromUri(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    withContext(Dispatchers.Main) {
                        setNewOriginalBitmap(bitmap, "Foto del Dispositivo")
                        showToast("Foto cargada con éxito")
                    }
                } else {
                    showToast("No se pudo cargar la imagen seleccionada.")
                }
            } catch (e: Exception) {
                Log.e("PhotoEditorVM", "Error loading uri $uri", e)
                showToast("Error al abrir imagen: ${e.message}")
            }
        }
    }

    private fun setNewOriginalBitmap(bitmap: Bitmap, title: String) {
        undoStack.clear()
        redoStack.clear()
        _uiState.update {
            it.copy(
                originalBitmap = bitmap,
                processedBitmap = bitmap,
                currentAdjustments = AdjustmentParameters(),
                baseAdjustments = AdjustmentParameters(),
                selectedPreset = null,
                presetIntensity = 1.0f,
                activePerfectionMode = null,
                aiDiagnostic = null,
                canUndo = false,
                canRedo = false,
                currentPhotoTitle = title
            )
        }
    }

    fun selectTab(tab: ToolTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun updateSplitPosition(position: Float) {
        _uiState.update { it.copy(splitPosition = position.coerceIn(0.05f, 0.95f)) }
    }

    fun setComparingOriginal(comparing: Boolean) {
        _uiState.update { it.copy(isComparingOriginal = comparing) }
    }

    fun selectPreset(preset: PhotoPreset?) {
        val currentAdj = _uiState.value.currentAdjustments
        saveToUndo(currentAdj)

        if (preset == null) {
            _uiState.update {
                it.copy(
                    selectedPreset = null,
                    currentAdjustments = AdjustmentParameters(),
                    presetIntensity = 1f
                )
            }
            scheduleProcessing(AdjustmentParameters())
            return
        }

        val intensity = _uiState.value.presetIntensity
        val targetAdj = AdjustmentParameters().lerpWith(preset.adjustments, intensity)

        _uiState.update {
            it.copy(
                selectedPreset = preset,
                activePerfectionMode = null,
                currentAdjustments = targetAdj
            )
        }
        scheduleProcessing(targetAdj)
    }

    fun updatePresetIntensity(intensity: Float) {
        val preset = _uiState.value.selectedPreset ?: return
        val clampedIntensity = intensity.coerceIn(0f, 1f)
        val targetAdj = AdjustmentParameters().lerpWith(preset.adjustments, clampedIntensity)

        _uiState.update {
            it.copy(
                presetIntensity = clampedIntensity,
                currentAdjustments = targetAdj
            )
        }
        scheduleProcessing(targetAdj)
    }

    fun applyPerfectionMode(mode: PerfectionMode) {
        val currentAdj = _uiState.value.currentAdjustments
        saveToUndo(currentAdj)

        _uiState.update {
            it.copy(
                activePerfectionMode = mode,
                selectedPreset = null,
                currentAdjustments = mode.adjustments
            )
        }
        scheduleProcessing(mode.adjustments)
        showToast("Perfección '${mode.title}' aplicada")
    }

    fun updateSingleAdjustment(transform: (AdjustmentParameters) -> AdjustmentParameters) {
        val current = _uiState.value.currentAdjustments
        val updated = transform(current)
        _uiState.update {
            it.copy(
                currentAdjustments = updated,
                selectedPreset = null,
                activePerfectionMode = null
            )
        }
        scheduleProcessing(updated)
    }

    fun commitAdjustmentChange() {
        saveToUndo(_uiState.value.currentAdjustments)
    }

    fun resetAdjustments() {
        saveToUndo(_uiState.value.currentAdjustments)
        val defaultAdj = AdjustmentParameters()
        _uiState.update {
            it.copy(
                currentAdjustments = defaultAdj,
                selectedPreset = null,
                presetIntensity = 1f,
                activePerfectionMode = null
            )
        }
        scheduleProcessing(defaultAdj)
        showToast("Ajustes restablecidos al original")
    }

    private fun scheduleProcessing(adjustments: AdjustmentParameters) {
        processingJob?.cancel()
        val orig = _uiState.value.originalBitmap ?: return
        processingJob = viewModelScope.launch(Dispatchers.Default) {
            delay(15) // Debounce rapid slider updates
            val rendered = ImageProcessor.processBitmap(orig, adjustments)
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(processedBitmap = rendered) }
            }
        }
    }

    private fun saveToUndo(adjustments: AdjustmentParameters) {
        undoStack.add(adjustments)
        redoStack.clear()
        _uiState.update { it.copy(canUndo = undoStack.isNotEmpty(), canRedo = false) }
    }

    fun undo() {
        if (undoStack.isEmpty()) return
        val previous = undoStack.removeAt(undoStack.lastIndex)
        redoStack.add(_uiState.value.currentAdjustments)
        _uiState.update {
            it.copy(
                currentAdjustments = previous,
                canUndo = undoStack.isNotEmpty(),
                canRedo = true,
                selectedPreset = null,
                activePerfectionMode = null
            )
        }
        scheduleProcessing(previous)
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val next = redoStack.removeAt(redoStack.lastIndex)
        undoStack.add(_uiState.value.currentAdjustments)
        _uiState.update {
            it.copy(
                currentAdjustments = next,
                canUndo = true,
                canRedo = redoStack.isNotEmpty()
            )
        }
        scheduleProcessing(next)
    }

    fun runAiDiagnosis() {
        val orig = _uiState.value.originalBitmap ?: return
        _uiState.update { it.copy(isAnalyzingAi = true) }
        viewModelScope.launch {
            val result = GeminiRestorationService.analyzePhoto(orig)
            result.onSuccess { diagnostic ->
                _uiState.update {
                    it.copy(
                        isAnalyzingAi = false,
                        aiDiagnostic = diagnostic
                    )
                }
                showToast("¡Diagnóstico de IA completado!")
            }.onFailure { err ->
                _uiState.update { it.copy(isAnalyzingAi = false) }
                showToast("Diagnóstico: ${err.message}")
            }
        }
    }

    fun applyAiRecommendedAdjustments() {
        val diag = _uiState.value.aiDiagnostic ?: return
        val recommended = diag.recommendedAdjustments
        saveToUndo(_uiState.value.currentAdjustments)

        _uiState.update {
            it.copy(
                currentAdjustments = recommended,
                selectedPreset = null,
                activePerfectionMode = null
            )
        }
        scheduleProcessing(recommended)
        showToast("¡Restauración Inteligente aplicada!")
    }

    fun runDeepAiRestoration() {
        val orig = _uiState.value.originalBitmap ?: return
        _uiState.update { it.copy(isDeepRestoringAi = true) }
        viewModelScope.launch {
            val result = GeminiRestorationService.restorePhotoGenerative(orig)
            result.onSuccess { restoredBitmap ->
                _uiState.update {
                    it.copy(
                        isDeepRestoringAi = false,
                        processedBitmap = restoredBitmap
                    )
                }
                showToast("¡Restauración Profunda Generativa completada!")
            }.onFailure { err ->
                _uiState.update { it.copy(isDeepRestoringAi = false) }
                // Fallback to recommended perfection settings when offline or key not supplied
                val fallbackAdj = PresetsCatalog.allPresets.first().adjustments
                applyPerfectionMode(PresetsCatalog.perfectionModes.first())
                showToast("Modo Pro Local: Restauración aplicada (${err.message?.take(40)}...)")
            }
        }
    }

    fun saveImageToGallery(context: Context) {
        val bitmap = _uiState.value.processedBitmap ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val filename = "Lumina_${System.currentTimeMillis()}.jpg"
                var fos: OutputStream? = null
                var imageUri: Uri? = null

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/LuminaAI")
                    }
                    val resolver = context.contentResolver
                    imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    if (imageUri != null) {
                        fos = resolver.openOutputStream(imageUri)
                    }
                } else {
                    val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val luminaDir = File(imagesDir, "LuminaAI")
                    if (!luminaDir.exists()) luminaDir.mkdirs()
                    val imageFile = File(luminaDir, filename)
                    fos = FileOutputStream(imageFile)
                }

                fos?.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it)
                }

                withContext(Dispatchers.Main) {
                    showToast("¡Foto guardada exitosamente en la Galería!")
                }
            } catch (e: Exception) {
                Log.e("PhotoEditorVM", "Error saving photo", e)
                withContext(Dispatchers.Main) {
                    showToast("Error al guardar: ${e.message}")
                }
            }
        }
    }

    fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
