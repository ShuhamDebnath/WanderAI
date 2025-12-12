package com.shuham.wanderai.presentation.trip_details

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.shuham.wanderai.data.model.Activity
import com.shuham.wanderai.data.model.ActivityOption
import com.shuham.wanderai.data.model.TripResponse
import com.shuham.wanderai.theme.OceanTeal
import com.shuham.wanderai.theme.SageGreen
import com.shuham.wanderai.theme.SunsetCoral
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TripDetailsRoute(
    viewModel: TripDetailsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToMap: (String, Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    println("tripId ${ state.trip?.id }")

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect {
            when (it) {
                is TripDetailsEvent.NavigateToMap -> {
                    onNavigateToMap(it.tripId, it.dayNumber)
                }
            }
        }
    }

    TripDetailsScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
        onNavigateToMap = { viewModel.onAction(TripDetailsAction.OnNavigateToMap) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsScreen(
    state: TripDetailsState,
    onAction: (TripDetailsAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToMap: () -> Unit
) {
    val tripData = state.trip
    val currentDayPlan = tripData?.days?.getOrNull(state.selectedDay)
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        onAction(TripDetailsAction.OnAddActivityClicked)
                        scope.launch {
                            snackbarHostState.showSnackbar("Feature Coming Soon: Add your own places!")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Activity")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                if (tripData != null) {
                    HeroHeader(tripData, onNavigateBack, onNavigateToMap)
                }

                if (tripData != null && tripData.days.isNotEmpty()) {
                    SecondaryScrollableTabRow(
                        selectedTabIndex = state.selectedDay,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        edgePadding = 16.dp,
                        divider = {} 
                    ) {
                        tripData.days.forEachIndexed { index, dayPlan ->
                            Tab(
                                selected = state.selectedDay == index,
                                onClick = { onAction(TripDetailsAction.OnDaySelected(index)) },
                                text = {
                                    Text(
                                        text = "Day ${dayPlan.dayNumber}",
                                        fontWeight = if (state.selectedDay == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (state.selectedDay == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                        }
                    }
                }

                if (currentDayPlan != null) {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(40.dp)
                                        .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(2.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = currentDayPlan.narrative,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }

                        currentDayPlan.sections.forEach { section ->
                            item {
                                Text(
                                    text = section.timeOfDay,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                )
                            }

                            items(section.activities) { activity ->
                                TimelineItem(
                                    activity = activity,
                                    onActivityClick = { onAction(TripDetailsAction.OnActivityClicked(it)) },
                                    onOptionClick = { onAction(TripDetailsAction.OnOptionClicked(it)) }
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(72.dp)) }
                    }
                } else if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Loading Itinerary...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error or No Data: ${state.errorMessage}", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }


        if (state.selectedActivity != null || state.selectedOption != null) {
            ModalBottomSheet(
                onDismissRequest = { onAction(TripDetailsAction.OnDismissBottomSheet) },
                sheetState = bottomSheetState,
                containerColor = Color.White
            ) {
                if (state.selectedActivity != null) {
                    ActivityDetailsContent(
                        activity = state.selectedActivity,
                        imageUrl = state.selectedImage, // Pass the image from state
                        onNavigate = { onAction(TripDetailsAction.OnNavigateToMap) }
                    )
                } else if (state.selectedOption != null) {
                    OptionDetailsContent(
                        option = state.selectedOption,
                        imageUrl = state.selectedImage, // Pass the image from state
                        onNavigate = { onAction(TripDetailsAction.OnNavigateToMap) }
                    )
                }
            }
        }
    }
}

@Composable
fun HeroHeader(tripData: TripResponse, onBackClick: () -> Unit, onMapClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        val destination = tripData.destinations.firstOrNull()?.replace(" ", ",") ?: "travel"
        AsyncImage(
            model = "https://loremflickr.com/800/400/$destination,landmark",
            contentDescription = "Destination Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            IconButton(
                onClick = onMapClick,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(Icons.Default.Map, contentDescription = "Show on Map", tint = Color.White)
            }
        }
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Text(
                text = tripData.tripName,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${tripData.days.size} Days • ${tripData.destinations.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
            }
        }
    }
}

@Composable
fun TimelineItem(
    activity: Activity,
    onActivityClick: (Activity) -> Unit,
    onOptionClick: (ActivityOption) -> Unit
) {
    var isChecked by remember { mutableStateOf(false) }
    val timelineColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                val lineX = 12.dp.toPx()
                drawLine(
                    color = timelineColor,
                    start = Offset(lineX, 0f),
                    end = Offset(lineX, size.height),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        Box(
            modifier = Modifier
                .width(24.dp)
                .padding(top = 2.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.background, CircleShape)
            )
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                ),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (!activity.options.isNullOrEmpty()) {
                 SimpleChoiceCarousel(
                        activity = activity,
                        onOptionClick = onOptionClick
                 )
            } else {
                 SimpleActivityCard(
                        activity = activity,
                        isChecked = isChecked,
                        onClick = { onActivityClick(activity) }
                 )
            }
        }
    }
}

@Composable
fun SimpleActivityCard(
    activity: Activity,
    isChecked: Boolean,
    onClick: () -> Unit
) {
    val cardAlpha by animateFloatAsState(targetValue = if (isChecked) 0.6f else 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .alpha(cardAlpha)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = activity.time ?: "",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = activity.placeName ?: activity.title ?: "Unknown",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (isChecked) TextDecoration.LineThrough else null
                ),
                color = if (isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )

            if (activity.description != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = activity.description ?: "No description available.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SimpleChoiceCarousel(
    activity: Activity,
    onOptionClick: (ActivityOption) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = activity.title ?: "Options",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(activity.options ?: emptyList()) { option ->
                SmallChoiceCard(option = option, onClick = { onOptionClick(option) })
            }
        }
    }
}

@Composable
fun SmallChoiceCard(option: ActivityOption, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(180.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(100.dp)) {
                AsyncImage(
                    model = "https://loremflickr.com/400/300/${option.name.replace(" ", ",")},food",
                    contentDescription = option.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                if (option.isRecommended) {
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "Best",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = option.tag,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = option.name,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ActivityDetailsContent(
    activity: Activity,
    imageUrl: String?,
    onNavigate: () -> Unit
) {
    val searchQuery = activity.placeName ?: activity.title ?: ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Image Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = searchQuery,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(OceanTeal.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (searchQuery.isNotEmpty()) {
                        CircularProgressIndicator(color = OceanTeal)
                    } else {
                        Icon(Icons.Default.Place, null, tint = OceanTeal.copy(0.5f), modifier = Modifier.size(48.dp))
                    }
                }
            }

            // Gradient Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 100f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            // Title overlaid on image
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                // Category Chip
                Text(
                    text = activity.type.replace("_", " "),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = activity.placeName ?: activity.title ?: "Details",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            // Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoChip(icon = Icons.Default.AccessTime, text = activity.time ?: "--:--")
                if (activity.estimatedDuration != null) {
                    InfoChip(icon = Icons.Default.Timer, text = activity.estimatedDuration)
                }
                if (activity.priceLevel != null) {
                    InfoChip(icon = Icons.Default.AttachMoney, text = activity.priceLevel)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // INSIDER TIP
            if (!activity.insiderTip.isNullOrBlank()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SageGreen.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = "Tip",
                            tint = OceanTeal,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Insider Tip",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = OceanTeal
                            )
                            Text(
                                text = activity.insiderTip,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Description
            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = activity.description ?: "No description available.",
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Navigate Button
            Button(
                onClick = onNavigate,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OceanTeal),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Navigate on Map", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun OptionDetailsContent(
    option: ActivityOption,
    imageUrl: String?,
    onNavigate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(250.dp)
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = option.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().background(OceanTeal.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OceanTeal)
                }
            }

            // Gradient
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 100f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
            )

            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
            ) {
                if (option.isRecommended) {
                    Text(
                        text = "Recommended",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier
                            .background(SunsetCoral, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    text = option.name,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoChip(icon = Icons.Default.AttachMoney, text = option.priceLevel)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = option.tag,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = SageGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = option.description,
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onNavigate,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OceanTeal),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Navigate on Map", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Helper for Info Chips (Time, Price, etc)
@Composable
fun InfoChip(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, color = Color.Black)
    }
}
