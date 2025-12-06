package com.shuham.wanderai.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import com.shuham.wanderai.data.model.TripRequest
import com.shuham.wanderai.presentation.loading.LoadingScreen
import com.shuham.wanderai.theme.WanderAITheme

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToItinerary: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.NavigateToItinerary -> onNavigateToItinerary(event.tripId)
            }
        }
    }
    
    HomeScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            HomeHeader(userName = state.userName)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            DestinationInputSection(destinations = state.destinations, onAction = onAction)

            Spacer(modifier = Modifier.height(24.dp))

            TravelerTypeSection(selectedType = state.selectedTravelerType, onAction = onAction)

            Spacer(modifier = Modifier.height(24.dp))

            DatesAndDurationSection(isFlexible = state.isFlexibleDate, durationDays = state.tripDurationDays, onAction = onAction)

            Spacer(modifier = Modifier.height(24.dp))

            BudgetSection(selectedBudget = state.selectedBudget, onAction = onAction)

            Spacer(modifier = Modifier.height(24.dp))

            PersonalizationSection(selectedDiet = state.selectedDiet, pace = state.pace, selectedInterests = state.selectedInterests, onAction = onAction)

            Spacer(modifier = Modifier.height(32.dp))

            PlanTripButton(onClick = { onAction(HomeAction.OnPlanTripClicked) }, isLoading = state.isLoading)
            
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // --- Overlays ---

        AnimatedVisibility(
            visible = state.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LoadingScreen()
        }

        if (state.errorMessage != null) {
            AlertDialog(
                onDismissRequest = { onAction(HomeAction.OnErrorDismissed) },
                title = { Text(text = "Trip Generation Failed") },
                text = { Text(text = state.errorMessage) },
                confirmButton = {
                    Button(onClick = { onAction(HomeAction.OnErrorDismissed) }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun HomeHeader(userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Where to next,",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = "$userName?",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun DestinationInputSection(
    destinations: List<String>,
    onAction: (HomeAction) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Destinations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            destinations.forEachIndexed { index, destination ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = destination,
                        onValueChange = { onAction(HomeAction.OnDestinationChanged(index, it)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        },
                        label = { Text("City Name") },
                        placeholder = { Text("e.g. New York") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    if (destinations.size > 1) {
                        IconButton(onClick = { onAction(HomeAction.OnRemoveDestination(index)) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                if (index < destinations.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            TextButton(onClick = { onAction(HomeAction.OnAddDestination) }) {
                Text("+ Add Destination", color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TravelerTypeSection(
    selectedType: TravelerType,
    onAction: (HomeAction) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Traveler Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TravelerType.entries.forEach { type ->
                    val isSelected = selectedType == type
                    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(containerColor)
                            .clickable { onAction(HomeAction.OnTravelerTypeSelected(type)) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = type.label,
                            color = contentColor,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DatesAndDurationSection(
    isFlexible: Boolean,
    durationDays: Int,
    onAction: (HomeAction) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Dates & Duration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Toggle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp)
            ) {
                Row {
                    val modifier = Modifier.weight(1f).clip(RoundedCornerShape(50)).padding(vertical = 8.dp)
                    
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = modifier
                            .background(if (!isFlexible) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { onAction(HomeAction.OnDateTypeToggle(false)) }
                    ) {
                        Text(
                            "Specific Dates",
                            color = if (!isFlexible) MaterialTheme.colorScheme.onSurface else Color.Gray,
                            fontWeight = if (!isFlexible) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                    
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = modifier
                            .background(if (isFlexible) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { onAction(HomeAction.OnDateTypeToggle(true)) }
                    ) {
                        Text(
                            "Flexible Days",
                            color = if (isFlexible) MaterialTheme.colorScheme.primary else Color.Gray,
                            fontWeight = if (isFlexible) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            if (isFlexible) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = { onAction(HomeAction.OnDurationChanged(durationDays - 1)) },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    
                    Text(
                        text = "$durationDays Days",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(
                        onClick = { onAction(HomeAction.OnDurationChanged(durationDays + 1)) },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetSection(
    selectedBudget: BudgetTier,
    onAction: (HomeAction) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Budget",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            BudgetTier.entries.forEach { budget ->
                val isSelected = selectedBudget == budget
                val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.Transparent

                Card(
                    onClick = { onAction(HomeAction.OnBudgetSelected(budget)) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    border = BorderStroke(1.dp, borderColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${budget.label} (${budget.priceLevel})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = budget.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PersonalizationSection(
    selectedDiet: List<DietOption>,
    pace: Float,
    selectedInterests: List<Interest>,
    onAction: (HomeAction) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Personalization",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Diet
            Spacer(modifier = Modifier.height(12.dp))
            Text("Diet", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DietOption.entries.forEach { diet ->
                    FilterChip(
                        selected = selectedDiet.contains(diet),
                        onClick = { onAction(HomeAction.OnDietSelected(diet)) },
                        label = { Text(diet.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(50)
                    )
                }
            }

            // Pace
            Spacer(modifier = Modifier.height(16.dp))
            Text("Pace", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Slider(
                value = pace,
                onValueChange = { onAction(HomeAction.OnPaceChanged(it)) },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Relaxed", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text("Packed", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            // Interests
            Spacer(modifier = Modifier.height(16.dp))
            Text("Interests", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Interest.entries.forEach { interest ->
                    val isSelected = selectedInterests.contains(interest)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onAction(HomeAction.OnInterestSelected(interest)) },
                        label = { Text(interest.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(50)
                    )
                }
            }
        }
    }
}

@Composable
fun PlanTripButton(onClick: () -> Unit, isLoading: Boolean) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        enabled = !isLoading
    ) {
        // The isLoading state is now handled by the overlay, so we don't need the text change here
        // It is better to leave it enabled=false to prevent multiple clicks
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Plan My Trip", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    WanderAITheme {
        HomeScreen(
            state = HomeState(
                userName = "Preview User",
                destinations = listOf("Paris", "London"),
                selectedBudget = BudgetTier.High
            ),
            onAction = {}
        )
    }
}
