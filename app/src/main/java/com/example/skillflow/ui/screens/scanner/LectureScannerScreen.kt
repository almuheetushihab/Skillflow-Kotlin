package com.example.skillflow.ui.screens.scanner

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.R
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.common.components.FormattedMarkdownText
import com.example.skillflow.ui.screens.scanner.components.CourseSelectionCard
import com.example.skillflow.ui.screens.scanner.components.GeminiApiKeyCard
import com.example.skillflow.ui.screens.scanner.components.ScanActionCard
import com.example.skillflow.ui.theme.SkillflowTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LectureScannerScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LectureScannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is LectureScannerEvent.NavigateToDetail -> onNavigateToDetail(event.nuggetId)
                is LectureScannerEvent.ShowSnackbar -> {}
            }
        }
    }

    LaunchedEffect(state.error, state.isScanning) {
        if (state.error != null || state.isScanning) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.onBitmapSelected(bitmap)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (e: Exception) {
                null
            }
            if (bitmap != null) {
                viewModel.onBitmapSelected(bitmap)
            }
        }
    }

    LectureScannerContent(
        state = state,
        scrollState = scrollState,
        onNavigateBack = onNavigateBack,
        onResetState = viewModel::resetState,
        onCourseSelected = viewModel::onCourseSelected,
        onLaunchCamera = { cameraLauncher.launch(null) },
        onLaunchGallery = { galleryLauncher.launch("image/*") },
        onAnalyzeSheet = viewModel::analyzeSheet,
        onSaveNuggetAndNavigate = viewModel::saveNuggetAndNavigate,
        onApiKeyInputChange = viewModel::onApiKeyInputChange,
        onSaveApiKey = viewModel::saveApiKey,
        onGetFreeKey = { uriHandler.openUri("https://aistudio.google.com/app/apikey") },
        onUseOfflineExtractor = viewModel::useOfflineExtractor,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LectureScannerContent(
    state: LectureScannerState,
    scrollState: androidx.compose.foundation.ScrollState,
    onNavigateBack: () -> Unit,
    onResetState: () -> Unit,
    onCourseSelected: (String) -> Unit,
    onLaunchCamera: () -> Unit,
    onLaunchGallery: () -> Unit,
    onAnalyzeSheet: () -> Unit,
    onSaveNuggetAndNavigate: () -> Unit,
    onApiKeyInputChange: (String) -> Unit,
    onSaveApiKey: () -> Unit,
    onGetFreeKey: () -> Unit,
    onUseOfflineExtractor: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SkillflowTopAppBar(
                title = stringResource(R.string.scan_lecture_sheet_title),
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
                actions = {
                    if (state.selectedBitmap != null && !state.isScanning) {
                        IconButton(onClick = onResetState) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.cd_reset)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Target Course Selection
            CourseSelectionCard(
                availableCourses = state.availableCourses,
                selectedCourseId = state.selectedCourseId,
                onCourseSelected = onCourseSelected
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Image Source Action Buttons
            if (state.selectedBitmap == null && !state.isScanning) {
                ScanActionCard(
                    onCameraClick = onLaunchCamera,
                    onGalleryClick = onLaunchGallery
                )
            }

            // Image Preview & Scanning Status
            state.selectedBitmap?.let { bitmap ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = stringResource(R.string.cd_lecture_preview),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (state.isScanning) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = stringResource(R.string.analyzing_lecture_sheet),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else if (state.scannedNugget == null) {
                            Button(
                                onClick = onAnalyzeSheet,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = stringResource(R.string.cd_sparkles)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.analyze_with_gemini))
                            }
                        }
                    }
                }
            }

            // Display Scanned Result
            state.scannedNugget?.let { nugget ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = stringResource(R.string.cd_check_circle),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.extracted_lecture_nugget),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = nugget.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = nugget.shortDescription,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        FormattedMarkdownText(
                            markdownText = nugget.content,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (nugget.quizzes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            SuggestionChip(
                                onClick = {},
                                label = {
                                    Text(stringResource(R.string.ai_quiz_generated, nugget.quizzes.size))
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onSaveNuggetAndNavigate,
                            enabled = !state.isSaving,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.saving))
                            } else {
                                Text(stringResource(R.string.save_to_nuggets))
                            }
                        }
                    }
                }
            }

            state.error?.let { err ->
                Spacer(modifier = Modifier.height(16.dp))
                GeminiApiKeyCard(
                    errorMessage = err,
                    apiKeyInput = state.apiKeyInput,
                    onApiKeyChange = onApiKeyInputChange,
                    onSaveApiKey = onSaveApiKey,
                    onGetFreeKey = onGetFreeKey,
                    onUseOfflineExtractor = onUseOfflineExtractor,
                    onRetry = onAnalyzeSheet
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LectureScannerContentPreview() {
    SkillflowTheme {
        LectureScannerContent(
            state = LectureScannerState(),
            scrollState = rememberScrollState(),
            onNavigateBack = {},
            onResetState = {},
            onCourseSelected = {},
            onLaunchCamera = {},
            onLaunchGallery = {},
            onAnalyzeSheet = {},
            onSaveNuggetAndNavigate = {},
            onApiKeyInputChange = {},
            onSaveApiKey = {},
            onGetFreeKey = {},
            onUseOfflineExtractor = {}
        )
    }
}
