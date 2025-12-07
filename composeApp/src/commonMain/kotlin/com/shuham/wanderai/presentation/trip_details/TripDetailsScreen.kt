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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

// Custom Colors from Design System
val OceanTeal = Color(0xFF006D77)
val SunsetCoral = Color(0xFFE29578)
val AliceBlue = Color(0xFFEDF6F9)
val SageGreen = Color(0xFF83C5BE)

@Composable
fun TripDetailsRoute(
    viewModel: TripDetailsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    TripDetailsScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsScreen(
    state: TripDetailsState,
    onAction: (TripDetailsAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    val itinerary = state.trip
    // Note: using index vs dayNumber requires careful mapping.
    // Assuming selectedDay in state corresponds to the list index for tabs logic
    // Ideally we map selectedDay (Int) to the actual day number or index.
    // The reference code used index.
    val currentDayPlan = itinerary?.days?.getOrNull(state.selectedDay) // Assuming selectedDay is 0-based index for this logic, if 1-based subtract 1
    
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
                    containerColor = SunsetCoral,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Activity")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AliceBlue)
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                // 1. Hero Header
                if (itinerary != null) {
                    HeroHeader(itinerary, onNavigateBack)
                }

                // 2. Day Tabs
                if (itinerary != null && itinerary.days.isNotEmpty()) {
                    SecondaryScrollableTabRow(
                        selectedTabIndex = state.selectedDay,
                        containerColor = Color.White,
                        contentColor = OceanTeal,
                        edgePadding = 16.dp,
                        divider = {} 
                    ) {
                        itinerary.days.forEachIndexed { index, dayPlan ->
                            Tab(
                                selected = state.selectedDay == index,
                                onClick = { onAction(TripDetailsAction.OnDaySelected(index)) },
                                text = {
                                    Text(
                                        text = "Day ${dayPlan.dayNumber}",
                                        fontWeight = if (state.selectedDay == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (state.selectedDay == index) OceanTeal else Color.Gray
                                    )
                                }
                            )
                        }
                    }
                }

                // 3. Timeline View
                if (currentDayPlan != null && itinerary != null) {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Day Narrative
                        item {
                            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(40.dp)
                                        .background(SunsetCoral, RoundedCornerShape(2.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = currentDayPlan.narrative,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.Gray
                                    )
                                )
                            }
                        }

                        currentDayPlan.sections.forEach { section ->
                            item {
                                Text(
                                    text = section.timeOfDay,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OceanTeal
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
                        Text("Loading Itinerary...", color = Color.Gray)
                    }
                } else {
                     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error or No Data: ${state.errorMessage}", color = Color.Gray)
                    }
                }
            }
        }

        // 4. Details Bottom Sheet
        if (state.selectedActivity != null || state.selectedOption != null) {
            ModalBottomSheet(
                onDismissRequest = { onAction(TripDetailsAction.OnDismissBottomSheet) },
                sheetState = bottomSheetState,
                containerColor = Color.White
            ) {
                if (state.selectedActivity != null) {
                    ActivityDetailsContent(
                        activity = state.selectedActivity,
                        onNavigate = { onAction(TripDetailsAction.OnNavigateToMap) }
                    )
                } else if (state.selectedOption != null) {
                    OptionDetailsContent(
                        option = state.selectedOption,
                        onNavigate = { onAction(TripDetailsAction.OnNavigateToMap) }
                    )
                }
            }
        }
    }
}

@Composable
fun HeroHeader(itinerary: TripResponse, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        // Use LoremFlickr for consistent travel images
        val destination = itinerary.destinations.firstOrNull()?.replace(" ", ",") ?: "travel"
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
        
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Text(
                text = itinerary.tripName,
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
                    text = "${itinerary.days.size} Days • ${itinerary.destinations.joinToString(", ")}",
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                val lineX = 12.dp.toPx()
                drawLine(
                    color = OceanTeal.copy(alpha = 0.3f),
                    start = Offset(lineX, 0f),
                    end = Offset(lineX, size.height),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        // Timeline Checkbox
        Box(
            modifier = Modifier
                .width(24.dp)
                .padding(top = 2.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(AliceBlue, CircleShape)
            )
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = OceanTeal,
                    uncheckedColor = OceanTeal.copy(alpha = 0.6f)
                ),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            // Distinguish between options and single activities
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "09:00 AM", // e.g. "09:00 AM"
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = OceanTeal
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = activity.placeName ?: activity.title ?: "Unknown",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (isChecked) TextDecoration.LineThrough else null
                ),
                color = if (isChecked) Color.Gray else Color.Black
            )

            if (activity.description != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
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
                color = Color.Gray
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                            .background(SunsetCoral, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "Best",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                            color = Color.White
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = option.tag,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = SageGreen,
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
fun ActivityDetailsContent(activity: Activity, onNavigate: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Text(
            text = activity.placeName ?: activity.title ?: "Details",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = OceanTeal
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "09:00 AM",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = activity.description ?: "No description available.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNavigate,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = OceanTeal)
        ) {
            Icon(Icons.Default.Navigation, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Navigate")
        }
    }
}

@Composable
fun OptionDetailsContent(option: ActivityOption, onNavigate: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        AsyncImage(
            model = "https://loremflickr.com/800/400/${option.name.replace(" ", ",")},food",
            contentDescription = option.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = option.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = OceanTeal,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = option.priceLevel,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
        }
        Text(
            text = option.tag,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = SageGreen
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = option.description,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNavigate,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = OceanTeal)
        ) {
            Icon(Icons.Default.Navigation, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Navigate")
        }
    }
}
