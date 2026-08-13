package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.automirrored.outlined.WrapText
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object PhosphorIcon {
    val Menu: ImageVector get() = Icons.Default.Menu
    val Folder: ImageVector get() = Icons.Default.Folder
    val FolderOutlined: ImageVector get() = Icons.Outlined.Folder
    val FolderPlus: ImageVector get() = Icons.Outlined.CreateNewFolder
    val Code: ImageVector get() = Icons.Outlined.Code
    val OpenInNew: ImageVector get() = Icons.AutoMirrored.Filled.OpenInNew
    val Settings: ImageVector get() = Icons.Default.Settings
    val SettingsOutlined: ImageVector get() = Icons.Outlined.Settings
    val Search: ImageVector get() = Icons.Default.Search
    val Add: ImageVector get() = Icons.Default.Add
    val StarFilled: ImageVector get() = Icons.Default.Star
    val StarOutlined: ImageVector get() = Icons.Outlined.StarOutline
    val Delete: ImageVector get() = Icons.Default.Delete
    val Drag: ImageVector get() = Icons.Default.DragHandle
    val Edit: ImageVector get() = Icons.Default.Edit
    val ArrowBack: ImageVector get() = Icons.AutoMirrored.Filled.ArrowBack
    val Check: ImageVector get() = Icons.Default.Check
    val CheckCircle: ImageVector get() = Icons.Default.CheckCircle
    val Description: ImageVector get() = Icons.Outlined.Description
    val Terminal: ImageVector get() = Icons.Outlined.Terminal
    val File: ImageVector get() = Icons.AutoMirrored.Outlined.InsertDriveFile
    val WrapText: ImageVector get() = Icons.AutoMirrored.Outlined.WrapText
    val FormatSize: ImageVector get() = Icons.Outlined.FormatSize
    val Reset: ImageVector get() = Icons.Default.Refresh
    val More: ImageVector get() = Icons.Default.MoreVert
}

@Composable
fun AppIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )
}
