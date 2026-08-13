package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FolderEntity
import com.example.data.RecentFileEntity
import com.example.registry.FileRegistry
import com.example.ui.MainViewModel
import com.example.ui.components.AppIcon
import com.example.ui.components.GlassCard
import com.example.ui.components.PhosphorIcon
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBlue
import com.example.ui.theme.GeoBlueContainer
import com.example.ui.theme.GeoBlueLight
import com.example.ui.theme.GeoBluePrimary
import com.example.ui.theme.GeoBorderGlass
import com.example.ui.theme.GeoCardGlassBg
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.GeoTextMuted
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary

@Composable
fun WorkspaceScreen(
    viewModel: MainViewModel,
    onOpenDrawer: () -> Unit,
    onOpenFile: (Uri) -> Unit,
    onNavigateToFolders: () -> Unit
) {
    val context = LocalContext.current
    val recentFiles by viewModel.recentFiles.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedCategoryFilter.collectAsState()

    var fileToAssignFolder by remember { mutableStateOf<RecentFileEntity?>(null) }

    // SAF Document Picker launcher
    val safPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.openFile(context, it)
            onOpenFile(it)
        }
    }

    val defaultCategories = listOf("All Files", "Notebooks", "Scripts", "Data Files", "Documents")
    val categories = remember(folders) {
        val customFolders = folders.filter { it.isVisible && it.name !in defaultCategories }.map { it.name }
        defaultCategories + customFolders
    }

    // Filter recent files list
    val filteredFiles = recentFiles.filter { file ->
        val matchesSearch = file.fileName.contains(searchQuery, ignoreCase = true)
        val matchesCategory = when (selectedFilter) {
            "All Files" -> true
            "Notebooks" -> file.extension.lowercase() == "ipynb"
            "Scripts" -> file.extension.lowercase() in listOf("py", "sh", "go", "rs", "kt", "java", "c", "cpp", "js", "ts")
            "Data Files" -> file.extension.lowercase() in listOf("json", "xml", "csv", "yaml", "yml")
            "Documents" -> file.extension.lowercase() in listOf("md", "html", "css", "txt")
            else -> file.folderCategory.equals(selectedFilter, ignoreCase = true)
        }
        matchesSearch && matchesCategory
    }

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x1AFFFFFF))
                ) {
                    AppIcon(
                        imageVector = PhosphorIcon.Menu,
                        contentDescription = "Open Drawer Menu",
                        tint = GeoTextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "CodeReader",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GeoTextPrimary
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Search Bar
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search workspace...", color = GeoTextMuted) },
                        leadingIcon = {
                            AppIcon(
                                imageVector = PhosphorIcon.Search,
                                contentDescription = "Search",
                                tint = GeoTextMuted
                            )
                        },
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = GeoCardGlassBg,
                            unfocusedContainerColor = GeoCardGlassBg,
                            focusedBorderColor = GeoBlueLight,
                            unfocusedBorderColor = GeoBorderGlass,
                            focusedTextColor = GeoTextPrimary,
                            unfocusedTextColor = GeoTextPrimary
                        ),
                        singleLine = true
                    )
                }

                // Filter Chips
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedFilter == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setCategoryFilter(category) },
                                label = {
                                    Text(
                                        text = category,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else GeoTextSecondary
                                    )
                                },
                                shape = CircleShape,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GeoBluePrimary,
                                    containerColor = GeoCardGlassBg
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = GeoBorderGlass
                                )
                            )
                        }
                    }
                }

                // Folder Quick View Section (Geometric Balance Colorful Cards)
                item {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "WORKSPACE FOLDERS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoTextMuted,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Customize",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GeoBlueLight,
                                modifier = Modifier.clickable { onNavigateToFolders() }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val visibleFolderList = folders.filter { it.isVisible }
                        if (visibleFolderList.isEmpty()) {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No Workspace Folders Visible",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GeoTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Tap Customize above to enable or reorder your workspace folders.",
                                        fontSize = 12.sp,
                                        color = GeoTextMuted
                                    )
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                visibleFolderList.chunked(2).forEach { rowFolders ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        rowFolders.forEach { folder ->
                                            val fileCount = recentFiles.count { file ->
                                                file.folderCategory.equals(folder.name, ignoreCase = true) ||
                                                        when (folder.name) {
                                                            "Notebooks" -> file.extension.lowercase() == "ipynb"
                                                            "Scripts" -> file.extension.lowercase() in listOf("py", "sh", "go", "rs", "kt", "java", "c", "cpp", "js", "ts")
                                                            "Data Files" -> file.extension.lowercase() in listOf("json", "xml", "csv", "yaml", "yml")
                                                            "Documents" -> file.extension.lowercase() in listOf("md", "html", "css", "txt")
                                                            else -> false
                                                        }
                                            }
                                            val isSelected = selectedFilter.equals(folder.name, ignoreCase = true)

                                            val parsedColor = try {
                                                Color(android.graphics.Color.parseColor(folder.colorHex))
                                            } catch (e: Exception) {
                                                GeoBluePrimary
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(105.dp)
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .background(parsedColor)
                                                    .border(
                                                        width = if (isSelected) 2.dp else 1.dp,
                                                        color = if (isSelected) Color.White else GeoBorderGlass,
                                                        shape = RoundedCornerShape(20.dp)
                                                    )
                                                    .clickable {
                                                        if (isSelected) {
                                                            viewModel.setCategoryFilter("All Files")
                                                        } else {
                                                            viewModel.setCategoryFilter(folder.name)
                                                        }
                                                    }
                                                    .padding(14.dp)
                                            ) {
                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    verticalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        AppIcon(
                                                            imageVector = PhosphorIcon.Folder,
                                                            contentDescription = "Folder Icon",
                                                            tint = Color.White.copy(alpha = 0.9f),
                                                            modifier = Modifier.size(20.dp)
                                                        )

                                                        if (isSelected) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .clip(CircleShape)
                                                                    .background(Color.White.copy(alpha = 0.3f))
                                                                    .padding(4.dp)
                                                            ) {
                                                                AppIcon(
                                                                    imageVector = PhosphorIcon.Check,
                                                                    contentDescription = "Active Folder",
                                                                    tint = Color.White,
                                                                    modifier = Modifier.size(12.dp)
                                                                )
                                                            }
                                                        }
                                                    }

                                                    Column {
                                                        Text(
                                                            text = folder.name,
                                                            fontSize = 15.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White,
                                                            maxLines = 1
                                                        )
                                                        Text(
                                                            text = "$fileCount ${if (fileCount == 1) "file" else "files"}",
                                                            fontSize = 12.sp,
                                                            color = Color.White.copy(alpha = 0.85f)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        if (rowFolders.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Recent Files Section Header
                item {
                    Text(
                        text = "RECENT FILES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoTextMuted,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Recent Files Bento List
                if (filteredFiles.isEmpty()) {
                    item {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AppIcon(
                                    imageVector = PhosphorIcon.File,
                                    contentDescription = null,
                                    tint = GeoTextMuted,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No code files found",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = GeoTextSecondary
                                )
                                Text(
                                    text = "Tap + below to open any file from storage",
                                    fontSize = 12.sp,
                                    color = GeoTextMuted
                                )
                            }
                        }
                    }
                } else {
                    items(filteredFiles, key = { it.uriString }) { file ->
                        val fileInfo = FileRegistry.getInfoForExtension(file.extension)

                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                val uri = Uri.parse(file.uriString)
                                viewModel.openFile(context, uri, file.fileName)
                                onOpenFile(uri)
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Accent extension badge
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(fileInfo.accentColor.copy(alpha = 0.2f))
                                        .border(1.dp, fileInfo.accentColor.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = file.extension.uppercase(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = fileInfo.accentColor
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = file.fileName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GeoTextPrimary
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Text(
                                            text = fileInfo.name,
                                            fontSize = 11.sp,
                                            color = fileInfo.accentColor
                                        )
                                        Text(
                                            text = " • ",
                                            fontSize = 11.sp,
                                            color = GeoTextMuted
                                        )
                                        Text(
                                            text = file.lineCount,
                                            fontSize = 11.sp,
                                            color = GeoTextSecondary
                                        )
                                        Text(
                                            text = " • ",
                                            fontSize = 11.sp,
                                            color = GeoTextMuted
                                        )
                                        Text(
                                            text = file.fileSize,
                                            fontSize = 11.sp,
                                            color = GeoTextMuted
                                        )
                                    }
                                }

                                // Folder Badge & Add to Folder Action
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (file.folderCategory.isNotEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .padding(end = 4.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(GeoSurfaceVariant)
                                                .border(1.dp, GeoBorderGlass, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = file.folderCategory,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = GeoBlueLight
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { fileToAssignFolder = file }
                                    ) {
                                        AppIcon(
                                            imageVector = PhosphorIcon.FolderPlus,
                                            contentDescription = "Add to Folder",
                                            tint = GeoBlueLight
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(88.dp)) }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                safPickerLauncher.launch(arrayOf("*/*"))
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(56.dp)
                .testTag("open_file_fab"),
            containerColor = GeoBluePrimary,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            AppIcon(
                imageVector = PhosphorIcon.Add,
                contentDescription = "Open file from storage",
                modifier = Modifier.size(28.dp)
            )
        }

        // Add to Folder Selection Dialog
        fileToAssignFolder?.let { file ->
            AddToFolderDialog(
                file = file,
                availableFolders = folders,
                onDismiss = { fileToAssignFolder = null },
                onSelectFolder = { folderName ->
                    viewModel.assignFileToFolder(file, folderName)
                    fileToAssignFolder = null
                },
                onCreateFolder = { folderName, colorHex ->
                    viewModel.createFolderAndAssignFile(file, folderName, colorHex)
                    fileToAssignFolder = null
                }
            )
        }
    }
}

@Composable
fun AddToFolderDialog(
    file: RecentFileEntity,
    availableFolders: List<FolderEntity>,
    onDismiss: () -> Unit,
    onSelectFolder: (String) -> Unit,
    onCreateFolder: (String, String) -> Unit
) {
    var showCreateSection by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    var selectedColorHex by remember { mutableStateOf("#2563EB") }

    val presetColors = listOf("#2563EB", "#10B981", "#F59E0B", "#EC4899", "#8B5CF6", "#64748B")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GeoSurface,
        title = {
            Column {
                Text(
                    text = "Add to Folder",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoTextPrimary
                )
                Text(
                    text = file.fileName,
                    fontSize = 13.sp,
                    color = GeoTextMuted,
                    maxLines = 1
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select a folder for this file:",
                    fontSize = 13.sp,
                    color = GeoTextSecondary
                )

                // List of existing folders
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableFolders) { folder ->
                        val isAssigned = file.folderCategory.equals(folder.name, ignoreCase = true)
                        val color = try {
                            Color(android.graphics.Color.parseColor(folder.colorHex))
                        } catch (e: Exception) {
                            GeoBlueLight
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isAssigned) GeoBlueContainer else GeoSurfaceVariant)
                                .border(
                                    width = if (isAssigned) 1.5.dp else 1.dp,
                                    color = if (isAssigned) GeoBlueLight else GeoBorderGlass,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectFolder(folder.name) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = folder.name,
                                        fontSize = 14.sp,
                                        fontWeight = if (isAssigned) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isAssigned) GeoBlueLight else GeoTextPrimary
                                    )
                                }

                                if (isAssigned) {
                                    AppIcon(
                                        imageVector = PhosphorIcon.Check,
                                        contentDescription = "Currently Selected",
                                        tint = GeoBlueLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Toggle Create New Folder
                if (!showCreateSection) {
                    TextButton(
                        onClick = { showCreateSection = true },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        AppIcon(
                            imageVector = PhosphorIcon.FolderPlus,
                            contentDescription = null,
                            tint = GeoBlueLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ Create New Folder", color = GeoBlueLight, fontSize = 13.sp)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GeoSurfaceVariant)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "NEW FOLDER NAME",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextMuted
                        )

                        OutlinedTextField(
                            value = newFolderName,
                            onValueChange = { newFolderName = it },
                            placeholder = { Text("e.g. Algorithms", fontSize = 13.sp, color = GeoTextMuted) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = GeoTextPrimary,
                                unfocusedTextColor = GeoTextPrimary,
                                focusedBorderColor = GeoBlueLight,
                                unfocusedBorderColor = GeoBorderGlass
                            )
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            presetColors.forEach { hex ->
                                val color = Color(android.graphics.Color.parseColor(hex))
                                val isSelected = selectedColorHex == hex
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (isSelected) 2.dp else 0.dp,
                                            color = if (isSelected) Color.White else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { selectedColorHex = hex }
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (newFolderName.isNotBlank()) {
                                    onCreateFolder(newFolderName.trim(), selectedColorHex)
                                }
                            },
                            enabled = newFolderName.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = GeoBluePrimary)
                        ) {
                            Text("Create & Assign", color = Color.White)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = GeoBlueLight)
            }
        }
    )
}
