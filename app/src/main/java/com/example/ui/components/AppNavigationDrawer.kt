package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBlueContainer
import com.example.ui.theme.GeoBlueLight
import com.example.ui.theme.GeoBorderGlass
import com.example.ui.theme.GeoCardGlassBg
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.GeoTextMuted
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary

enum class NavDestination {
    WORKSPACE,
    FOLDERS,
    SETTINGS
}

@Composable
fun AppNavigationDrawer(
    drawerState: DrawerState,
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .background(GeoBackground)
                    .padding(24.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Branding
                    Text(
                        text = "CodeReader",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Normal,
                        color = GeoTextPrimary,
                        modifier = Modifier.padding(bottom = 32.dp, top = 12.dp)
                    )

                    // Navigation Items
                    NavigationDrawerItem(
                        title = "Workspace",
                        icon = PhosphorIcon.Description,
                        isSelected = currentDestination == NavDestination.WORKSPACE,
                        onClick = { onNavigate(NavDestination.WORKSPACE) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NavigationDrawerItem(
                        title = "Folders",
                        icon = PhosphorIcon.Folder,
                        isSelected = currentDestination == NavDestination.FOLDERS,
                        onClick = { onNavigate(NavDestination.FOLDERS) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NavigationDrawerItem(
                        title = "Settings",
                        icon = PhosphorIcon.Settings,
                        isSelected = currentDestination == NavDestination.SETTINGS,
                        onClick = { onNavigate(NavDestination.SETTINGS) }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Developer Bento Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(GeoSurfaceVariant)
                            .border(1.dp, GeoBorderGlass, RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "DEVELOPER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoTextMuted,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "mohit :)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GeoTextPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GeoBlueContainer)
                                    .border(1.dp, GeoBlueLight.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://github.com/mohitpandeycs/")
                                        )
                                        try {
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AppIcon(
                                            imageVector = PhosphorIcon.Code,
                                            contentDescription = "GitHub",
                                            tint = GeoBlueLight,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "View GitHub",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = GeoBlueLight
                                        )
                                    }
                                    AppIcon(
                                        imageVector = PhosphorIcon.OpenInNew,
                                        contentDescription = null,
                                        tint = GeoBlueLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        content = content
    )
}

@Composable
private fun NavigationDrawerItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (isSelected) GeoBlueContainer else GeoCardGlassBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppIcon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) GeoBlueLight else GeoTextSecondary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isSelected) GeoBlueLight else GeoTextPrimary
            )
        }
    }
}
