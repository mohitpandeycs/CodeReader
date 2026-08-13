package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FolderEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AppIcon
import com.example.ui.components.GlassCard
import com.example.ui.components.PhosphorIcon
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBlueLight
import com.example.ui.theme.GeoBluePrimary
import com.example.ui.theme.GeoBorderGlass
import com.example.ui.theme.GeoTextMuted
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary

@Composable
fun FolderCustomizationScreen(
    viewModel: MainViewModel,
    onOpenDrawer: () -> Unit
) {
    val folders by viewModel.folders.collectAsState()

    var folderToEditName by remember { mutableStateOf<FolderEntity?>(null) }
    var newNameInput by remember { mutableStateOf("") }

    var folderToEditColor by remember { mutableStateOf<FolderEntity?>(null) }

    val presetColors = listOf(
        "#6366F1", // Indigo
        "#EC4899", // Pink
        "#F59E0B", // Amber
        "#10B981", // Emerald
        "#3B82F6", // Blue
        "#3E3E3E"  // Graphite
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GeoBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
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
                        contentDescription = "Menu",
                        tint = GeoTextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Workspace",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GeoTextPrimary
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            text = "Folder Customization",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Normal,
                            color = GeoTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Organize your workspace. Reorder, toggle visibility, edit names and background colors to keep your projects visually distinct.",
                            fontSize = 14.sp,
                            color = GeoTextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }

                itemsIndexed(folders) { index, folder ->
                    val folderColor = try {
                        Color(android.graphics.Color.parseColor(folder.colorHex))
                    } catch (e: Exception) {
                        GeoBluePrimary
                    }

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Reorder Controls (Move Up / Move Down)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                IconButton(
                                    onClick = {
                                        if (index > 0) {
                                            val mutable = folders.toMutableList()
                                            val item = mutable.removeAt(index)
                                            mutable.add(index - 1, item)
                                            viewModel.reorderFolders(mutable)
                                        }
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Text("▲", fontSize = 10.sp, color = if (index > 0) GeoTextSecondary else GeoTextMuted)
                                }
                                IconButton(
                                    onClick = {
                                        if (index < folders.size - 1) {
                                            val mutable = folders.toMutableList()
                                            val item = mutable.removeAt(index)
                                            mutable.add(index + 1, item)
                                            viewModel.reorderFolders(mutable)
                                        }
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Text("▼", fontSize = 10.sp, color = if (index < folders.size - 1) GeoTextSecondary else GeoTextMuted)
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Color Circle Indicator (Clickable to change color)
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(folderColor)
                                    .border(2.dp, folderColor.copy(alpha = 0.5f), CircleShape)
                                    .clickable { folderToEditColor = folder }
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Folder Name + Edit Button
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = folder.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (folder.isVisible) GeoTextPrimary else GeoTextMuted
                                )

                                IconButton(
                                    onClick = {
                                        folderToEditName = folder
                                        newNameInput = folder.name
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    AppIcon(
                                        imageVector = PhosphorIcon.Edit,
                                        contentDescription = "Edit Name",
                                        tint = GeoTextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            // Visibility Toggle Switch
                            Switch(
                                checked = folder.isVisible,
                                onCheckedChange = { viewModel.toggleFolderVisibility(folder) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = GeoBluePrimary,
                                    uncheckedThumbColor = GeoTextMuted,
                                    uncheckedTrackColor = GeoBorderGlass
                                )
                            )
                        }
                    }
                }

                // Reset Defaults Button
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.resetFoldersToDefault() },
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, GeoTextPrimary)
                        ) {
                            Text(
                                text = "Reset Defaults",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GeoTextPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Rename Dialog
        folderToEditName?.let { targetFolder ->
            AlertDialog(
                onDismissRequest = { folderToEditName = null },
                title = { Text("Rename Folder", color = GeoTextPrimary) },
                text = {
                    OutlinedTextField(
                        value = newNameInput,
                        onValueChange = { newNameInput = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GeoTextPrimary,
                            unfocusedTextColor = GeoTextPrimary
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newNameInput.isNotBlank()) {
                                viewModel.updateFolderName(targetFolder, newNameInput.trim())
                            }
                            folderToEditName = null
                        }
                    ) {
                        Text("Save", color = GeoBlueLight)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { folderToEditName = null }) {
                        Text("Cancel", color = GeoTextMuted)
                    }
                },
                containerColor = GeoBackground
            )
        }

        // Color Picker Dialog
        folderToEditColor?.let { targetFolder ->
            AlertDialog(
                onDismissRequest = { folderToEditColor = null },
                title = { Text("Select Folder Color", color = GeoTextPrimary) },
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        presetColors.forEach { hex ->
                            val color = Color(android.graphics.Color.parseColor(hex))
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable {
                                        viewModel.updateFolderColor(targetFolder, hex)
                                        folderToEditColor = null
                                    }
                            )
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { folderToEditColor = null }) {
                        Text("Close", color = GeoTextMuted)
                    }
                },
                containerColor = GeoBackground
            )
        }
    }
}
