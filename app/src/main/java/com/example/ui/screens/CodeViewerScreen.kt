package com.example.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CellOutput
import com.example.model.CellType
import com.example.model.NotebookCell
import com.example.syntax.SyntaxHighlighter
import com.example.syntax.SyntaxThemes
import com.example.ui.MainViewModel
import com.example.ui.components.AppIcon
import com.example.ui.components.GlassCard
import com.example.ui.components.PhosphorIcon
import androidx.compose.ui.text.style.TextAlign
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBlue
import com.example.ui.theme.GeoBlueLight
import com.example.ui.theme.GeoBluePrimary
import com.example.ui.theme.GeoBorderGlass
import com.example.ui.theme.GeoCardGlassBg
import com.example.ui.theme.GeoPink
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.GeoTextMuted
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import kotlinx.coroutines.launch

@Composable
fun CodeViewerScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val fileState by viewModel.openFileState.collectAsState()
    val settings by viewModel.settingsState.collectAsState()
    val themeColors = SyntaxThemes.getColors(settings.syntaxTheme)

    val listState = rememberLazyListState()
    val horizontalScrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    var showSearchField by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GeoBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x1AFFFFFF))
                    ) {
                        AppIcon(
                            imageVector = PhosphorIcon.ArrowBack,
                            contentDescription = "Back",
                            tint = GeoTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = fileState.fileName.ifEmpty { "Code Viewer" },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GeoTextPrimary
                        )
                        if (fileState.isNotebook && fileState.notebookData != null) {
                            Text(
                                text = "Kernel: ${fileState.notebookData?.kernelName}",
                                fontSize = 11.sp,
                                color = GeoBlueLight
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { showSearchField = !showSearchField }
                ) {
                    AppIcon(
                        imageVector = PhosphorIcon.Search,
                        contentDescription = "Search in file",
                        tint = if (showSearchField) GeoBlueLight else GeoTextPrimary
                    )
                }
            }

            // In-file search bar
            AnimatedVisibility(visible = showSearchField) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    OutlinedTextField(
                        value = fileState.searchQuery,
                        onValueChange = { viewModel.setInFileSearchQuery(it) },
                        placeholder = { Text("Find in file...", color = GeoTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GeoTextPrimary,
                            unfocusedTextColor = GeoTextPrimary,
                            focusedBorderColor = GeoBlueLight,
                            unfocusedBorderColor = GeoBorderGlass
                        )
                    )
                }
            }

            if (fileState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GeoBluePrimary)
                }
            } else if (fileState.errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = fileState.errorMessage ?: "Error loading file",
                        color = GeoPink,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            } else if (fileState.isNotebook && fileState.notebookData != null) {
                // Jupyter Notebook Viewer
                val cells = fileState.notebookData?.cells ?: emptyList()

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(cells) { _, cell ->
                        JupyterCellCard(
                            cell = cell,
                            extension = fileState.extension,
                            themeColors = themeColors,
                            fontSizeSp = settings.fontSizeSp,
                            codeFontFamily = settings.codeFontOption.fontFamily,
                            searchQuery = fileState.searchQuery
                        )
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            } else {
                // Standard Code File Viewer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(themeColors.backgroundColor)
                        .border(1.dp, GeoBorderGlass, RoundedCornerShape(16.dp))
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        itemsIndexed(fileState.lines) { index, line ->
                            val lineAnnotated = SyntaxHighlighter.highlightLine(
                                line = line,
                                extension = fileState.extension,
                                colors = themeColors,
                                searchQuery = fileState.searchQuery
                            )

                            val lineNumWidth = if (fileState.lines.size > 9999) 42.dp else if (fileState.lines.size > 999) 32.dp else 24.dp

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 1.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Compact Line Number Column
                                Text(
                                    text = "${index + 1}",
                                    fontSize = (settings.fontSizeSp - 2).coerceAtLeast(10).sp,
                                    lineHeight = (settings.fontSizeSp * 1.5).sp,
                                    fontFamily = settings.codeFontOption.fontFamily,
                                    color = themeColors.lineNumberColor,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(lineNumWidth)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Code Content with Horizontal Scroll when Word Wrap is OFF
                                if (settings.enableWordWrap) {
                                    Text(
                                        text = lineAnnotated,
                                        fontSize = settings.fontSizeSp.sp,
                                        lineHeight = (settings.fontSizeSp * 1.5).sp,
                                        fontFamily = settings.codeFontOption.fontFamily,
                                        softWrap = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .horizontalScroll(horizontalScrollState)
                                    ) {
                                        Text(
                                            text = lineAnnotated,
                                            fontSize = settings.fontSizeSp.sp,
                                            lineHeight = (settings.fontSizeSp * 1.5).sp,
                                            fontFamily = settings.codeFontOption.fontFamily,
                                            softWrap = false
                                        )
                                    }
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }

        // Floating Bottom Control Bar (Scroll Top, Wrap Toggle, Font Scale - Clean Native Container)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .clip(CircleShape)
                .background(GeoSurfaceVariant)
                .border(1.dp, GeoBorderGlass, CircleShape)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Go to Top
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("▲", fontSize = 14.sp, color = GeoTextPrimary)
                }

                // Word Wrap Toggle
                IconButton(
                    onClick = { viewModel.toggleWordWrap(!settings.enableWordWrap) },
                    modifier = Modifier.size(36.dp)
                ) {
                    AppIcon(
                        imageVector = PhosphorIcon.WrapText,
                        contentDescription = "Word Wrap",
                        tint = if (settings.enableWordWrap) GeoBlueLight else GeoTextMuted
                    )
                }

                // Font Size Decrease
                IconButton(
                    onClick = {
                        if (settings.fontSizeSp > 12) viewModel.updateFontSize(settings.fontSizeSp - 1)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("A-", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GeoTextPrimary)
                }

                // Font Size Increase
                IconButton(
                    onClick = {
                        if (settings.fontSizeSp < 24) viewModel.updateFontSize(settings.fontSizeSp + 1)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("A+", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GeoTextPrimary)
                }
            }
        }
    }
}

@Composable
fun JupyterCellCard(
    cell: NotebookCell,
    extension: String,
    themeColors: com.example.syntax.SyntaxColors,
    fontSizeSp: Int,
    codeFontFamily: FontFamily = FontFamily.Monospace,
    searchQuery: String
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Execution Badge for Code Cells
            if (cell.type == CellType.CODE) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "In [${cell.executionCount ?: " "}]",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = GeoBlueLight,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "CODE CELL",
                        fontSize = 10.sp,
                        color = GeoTextMuted,
                        letterSpacing = 1.sp
                    )
                }
            } else {
                Text(
                    text = "MARKDOWN CELL",
                    fontSize = 10.sp,
                    color = GeoTextMuted,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Cell Source Block
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(themeColors.backgroundColor)
                    .border(1.dp, GeoBorderGlass, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    cell.source.lines().forEach { line ->
                        val lineAnnotated = SyntaxHighlighter.highlightLine(
                            line = line,
                            extension = if (cell.type == CellType.MARKDOWN) "md" else "py",
                            colors = themeColors,
                            searchQuery = searchQuery
                        )
                        Text(
                            text = lineAnnotated,
                            fontSize = fontSizeSp.sp,
                            fontFamily = codeFontFamily,
                            softWrap = true
                        )
                    }
                }
            }

            // Cell Outputs
            if (cell.outputs.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                cell.outputs.forEach { output ->
                    when (output) {
                        is CellOutput.TextOutput -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x1A000000))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = output.text,
                                    fontSize = (fontSizeSp - 2).sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = GeoTextSecondary
                                )
                            }
                        }
                        is CellOutput.ErrorOutput -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GeoPink.copy(alpha = 0.15f))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "${output.ename}: ${output.evalue}",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = GeoPink
                                )
                            }
                        }
                        is CellOutput.ImageOutput -> {
                            val bitmap = remember(output.base64Data) {
                                try {
                                    val bytes = Base64.decode(output.base64Data, Base64.DEFAULT)
                                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                } catch (e: Exception) {
                                    null
                                }
                            }

                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Jupyter Output Plot",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
