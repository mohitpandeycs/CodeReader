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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syntax.SyntaxThemeType
import com.example.syntax.SyntaxThemes
import com.example.ui.MainViewModel
import com.example.ui.components.AppIcon
import com.example.ui.components.GlassCard
import com.example.ui.components.PhosphorIcon
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBlueLight
import com.example.ui.theme.GeoBluePrimary
import com.example.ui.theme.GeoBorderGlass
import com.example.ui.theme.GeoCardGlassBg
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.GeoTextMuted
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary

import com.example.ui.theme.CodeFontOption
import com.example.ui.theme.GeoBlueContainer

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onOpenDrawer: () -> Unit
) {
    val settings by viewModel.settingsState.collectAsState()

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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = "Settings",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Normal,
                            color = GeoTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Customize your reading environment. Adjust typography, visual themes, and layout preferences to create a comfortable workspace.",
                            fontSize = 14.sp,
                            color = GeoTextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }

                // Syntax Highlighting Themes Section
                item {
                    Column {
                        Text(
                            text = "Syntax Highlighting",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GeoTextPrimary
                        )
                        Text(
                            text = "Select a color theme for code blocks.",
                            fontSize = 13.sp,
                            color = GeoTextMuted
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(SyntaxThemeType.values()) { themeType ->
                                val isSelected = settings.syntaxTheme == themeType
                                val themeColors = SyntaxThemes.getColors(themeType)

                                Box(
                                    modifier = Modifier
                                        .width(220.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(GeoCardGlassBg)
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) GeoBlueLight else GeoBorderGlass,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable { viewModel.updateSyntaxTheme(themeType) }
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        // Mini code preview box
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(100.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(themeColors.backgroundColor)
                                                .padding(8.dp)
                                        ) {
                                            Column {
                                                Text(
                                                    text = "function renderCanvas() {",
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = themeColors.keywordColor
                                                )
                                                Text(
                                                    text = "  const ctx = canvas.getContext('2d');",
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = themeColors.stringColor
                                                )
                                                Text(
                                                    text = "  ctx.fillRect(0, 0, w, h);",
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = themeColors.functionColor
                                                )
                                                Text(
                                                    text = "}",
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = themeColors.textColor
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

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
                                                        .background(themeColors.backgroundColor)
                                                        .border(1.dp, GeoBorderGlass, CircleShape)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = themeType.displayName,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = GeoTextPrimary
                                                )
                                            }

                                            if (isSelected) {
                                                AppIcon(
                                                    imageVector = PhosphorIcon.CheckCircle,
                                                    contentDescription = "Selected",
                                                    tint = GeoBlueLight,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Appearance Controls Card (Clean Native Settings UI)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(GeoSurfaceVariant)
                            .border(1.dp, GeoBorderGlass, RoundedCornerShape(20.dp))
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Frosted Glass Effect Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Frosted Glass Effects",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GeoTextPrimary
                                    )
                                    Text(
                                        text = "Enable translucent backgrounds on navigation elements.",
                                        fontSize = 12.sp,
                                        color = GeoTextMuted
                                    )
                                }

                                Switch(
                                    checked = settings.enableGlassEffects,
                                    onCheckedChange = { viewModel.toggleGlassEffects(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = GeoBluePrimary
                                    )
                                )
                            }

                            HorizontalDivider(color = GeoBorderGlass)

                            // Word Wrap Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Word Wrap",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GeoTextPrimary
                                    )
                                    Text(
                                        text = "Wrap lines that exceed the editor width.",
                                        fontSize = 12.sp,
                                        color = GeoTextMuted
                                    )
                                }

                                Switch(
                                    checked = settings.enableWordWrap,
                                    onCheckedChange = { viewModel.toggleWordWrap(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = GeoBluePrimary
                                    )
                                )
                            }

                            HorizontalDivider(color = GeoBorderGlass)

                            // Font Size Slider
                            Column {
                                Text(
                                    text = "Editor Font Size",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GeoTextPrimary
                                )
                                Text(
                                    text = "Adjust the base font size for reading code (${settings.fontSizeSp}sp).",
                                    fontSize = 12.sp,
                                    color = GeoTextMuted
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("A-", fontSize = 12.sp, color = GeoTextMuted)
                                    Slider(
                                        value = settings.fontSizeSp.toFloat(),
                                        onValueChange = { viewModel.updateFontSize(it.toInt()) },
                                        valueRange = 12f..24f,
                                        steps = 11,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 12.dp),
                                        colors = SliderDefaults.colors(
                                            thumbColor = Color.White,
                                            activeTrackColor = GeoBluePrimary,
                                            inactiveTrackColor = GeoBorderGlass
                                        )
                                    )
                                    Text("A+", fontSize = 18.sp, color = GeoTextPrimary)
                                }
                            }
                        }
                    }
                }

                // Code Font Selection Section
                item {
                    Column {
                        Text(
                            text = "Code Font",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GeoTextPrimary
                        )
                        Text(
                            text = "Choose the typeface used in the code viewer.",
                            fontSize = 13.sp,
                            color = GeoTextMuted
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(GeoSurfaceVariant)
                                .border(1.dp, GeoBorderGlass, RoundedCornerShape(20.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CodeFontOption.values().forEach { fontOpt ->
                                val isSelected = settings.codeFontOption == fontOpt
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isSelected) GeoBlueContainer else GeoSurface)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) GeoBlueLight else GeoBorderGlass,
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .clickable { viewModel.updateCodeFontOption(fontOpt) }
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = fontOpt.displayName,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) GeoBlueLight else GeoTextPrimary
                                                )
                                                Text(
                                                    text = fontOpt.description,
                                                    fontSize = 12.sp,
                                                    color = GeoTextMuted
                                                )
                                            }
                                            if (isSelected) {
                                                AppIcon(
                                                    imageVector = PhosphorIcon.CheckCircle,
                                                    contentDescription = "Selected Font",
                                                    tint = GeoBlueLight,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Live Preview Box
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF1E1E1E))
                                                .padding(10.dp)
                                        ) {
                                            Text(
                                                text = fontOpt.sampleSnippet,
                                                fontSize = 12.sp,
                                                fontFamily = fontOpt.fontFamily,
                                                color = Color(0xFFD4D4D4),
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}
