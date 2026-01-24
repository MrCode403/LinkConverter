package xyz.illuminate.dlinks.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Output
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.illuminate.dlinks.viewmodel.LinkConverterViewModel
import xyz.illuminate.dlinks.viewmodel.LinkType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkConverterScreen(
    viewModel: LinkConverterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Link Converter",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Join Telegram") },
                            onClick = {
                                showMenu = false
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://t.me/team_illuminate")
                                    )
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Send, null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Rate App") },
                            onClick = {
                                showMenu = false
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=xyz.illuminate.dlinks")
                                    )
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Star, null)
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 8.dp
            ) {
                LinkType.values().forEach { type ->
                    NavigationBarItem(
                        icon = {
                            Box(
                                modifier = Modifier.size(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Background circle for selected state
                                if (uiState.selectedType == type) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(
                                                        type.color.copy(alpha = 0.2f),
                                                        type.color.copy(alpha = 0.1f)
                                                    )
                                                ),
                                                shape = CircleShape
                                            )
                                    )
                                }

                                // Icon with better sizing
                                Image(
                                    painter = painterResource(type.iconRes),
                                    contentDescription = type.title,
                                    modifier = Modifier.size(28.dp),
                                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                        if (uiState.selectedType == type)
                                            type.color
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                )
                            }
                        },
                        label = {
                            Text(
                                type.title.split(" ").firstOrNull() ?: type.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (uiState.selectedType == type)
                                    androidx.compose.ui.text.font.FontWeight.Bold
                                else
                                    androidx.compose.ui.text.font.FontWeight.Normal
                            )
                        },
                        selected = uiState.selectedType == type,
                        onClick = { viewModel.selectLinkType(type) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = type.color,
                            selectedTextColor = type.color,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Animated gradient background
            AnimatedGradientBackground()

            AnimatedContent(
                targetState = uiState.selectedType,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) +
                            slideInHorizontally(
                                initialOffsetX = { if (targetState.ordinal > initialState.ordinal) 300 else -300 },
                                animationSpec = tween(400)
                            ) togetherWith
                            fadeOut(animationSpec = tween(400)) +
                            slideOutHorizontally(
                                targetOffsetX = { if (targetState.ordinal > initialState.ordinal) -300 else 300 },
                                animationSpec = tween(400)
                            )
                },
                label = "content_transition"
            ) { linkType ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Service Icon with Animation
                    ServiceIconSection(linkType)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Title and Subtitle with Animation
                    TitleSection(linkType)

                    Spacer(modifier = Modifier.height(32.dp))

                    // Input Field
                    InputSection(
                        value = uiState.inputLink,
                        onValueChange = { viewModel.updateInputLink(it) },
                        error = uiState.error,
                        onErrorDismiss = { viewModel.clearError() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Arrow Animation
                    AnimatedArrow()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Output Field
                    OutputSection(
                        value = uiState.outputLink,
                        showSuccess = uiState.showSuccessAnimation
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action Buttons
                    ActionButtons(
                        onConvert = { viewModel.convertLink() },
                        onCopy = {
                            if (uiState.outputLink.isNotEmpty()) {
                                val clipboard =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(
                                    ClipData.newPlainText(
                                        "text",
                                        uiState.outputLink
                                    )
                                )
                                android.widget.Toast.makeText(
                                    context,
                                    "Link copied!",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        isLoading = uiState.isLoading,
                        hasOutput = uiState.outputLink.isNotEmpty()
                    )
                }
            }
        }

        // Loading Dialog
        if (uiState.isLoading) {
            LoadingDialog()
        }
    }
}

@Composable
fun AnimatedGradientBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val animatedAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient angle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.02f),
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.03f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.02f)
                    ),
                    start = androidx.compose.ui.geometry.Offset(
                        x = 500f * kotlin.math.cos(Math.toRadians(animatedAngle.toDouble()))
                            .toFloat(),
                        y = 500f * kotlin.math.sin(Math.toRadians(animatedAngle.toDouble()))
                            .toFloat()
                    ),
                    end = androidx.compose.ui.geometry.Offset(
                        x = 500f * kotlin.math.cos(Math.toRadians(animatedAngle.toDouble() + 180))
                            .toFloat(),
                        y = 500f * kotlin.math.sin(Math.toRadians(animatedAngle.toDouble() + 180))
                            .toFloat()
                    )
                )
            )
    )
}

@Composable
fun ServiceIconSection(linkType: LinkType) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "icon scale"
    )

    Box(
        modifier = Modifier
            .size(140.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow effect
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = linkType.color.copy(alpha = 0.08f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {}

        // Main icon card
        Card(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                linkType.color.copy(alpha = 0.15f),
                                linkType.color.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(linkType.iconRes),
                    contentDescription = linkType.title,
                    modifier = Modifier.size(70.dp)
                )
            }
        }
    }
}

@Composable
fun TitleSection(linkType: LinkType) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = linkType.title,
            style = MaterialTheme.typography.headlineMedium,
            color = linkType.color,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = linkType.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun InputSection(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    onErrorDismiss: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Enter link to convert") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = {
                Icon(Icons.Default.Link, "Link")
            },
            isError = error != null,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        AnimatedVisibility(
            visible = error != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun AnimatedArrow() {
    val infiniteTransition = rememberInfiniteTransition(label = "arrow")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arrow bounce"
    )

    Icon(
        imageVector = Icons.Default.ArrowDownward,
        contentDescription = "Convert",
        modifier = Modifier
            .size(32.dp)
            .offset(y = offsetY.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun OutputSection(value: String, showSuccess: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (showSuccess) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "output scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (showSuccess)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (showSuccess)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (showSuccess) Icons.Default.CheckCircle else Icons.Default.Output,
                contentDescription = null,
                tint = if (showSuccess)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = value.ifEmpty { "Output will appear here" },
                style = MaterialTheme.typography.bodyMedium,
                color = if (value.isEmpty())
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                else
                    MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ActionButtons(
    onConvert: () -> Unit,
    onCopy: () -> Unit,
    isLoading: Boolean,
    hasOutput: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onConvert,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Default.Autorenew, "Convert", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Convert", style = MaterialTheme.typography.labelLarge)
            }
        }

        Button(
            onClick = onCopy,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            enabled = hasOutput && !isLoading,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Icon(Icons.Default.ContentCopy, "Copy", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Copy Link", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MaterialLoadingIndicator() {
    LoadingIndicator()
}

@Composable
fun LoadingDialog() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .wrapContentSize(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    MaterialLoadingIndicator()
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Converting Link",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Please wait...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
