@file:Suppress("unused")

package com.example.project1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.project1.data.local.AppDatabase
import com.example.project1.data.local.UserRepository

/**
 * A small-scale, minimal-class Spoonacular "what's in my fridge" search screen.
 *
 * Features:
 * - Add multiple ingredients
 * - Calls Spoonacular /recipes/findByIngredients
 * - ranking=1 prioritizes "maximize used ingredients first" (most similar recipes first)
 * - Shows scrollable list with:
 *   - image + title
 *   - used ingredients (you have)
 *   - missed ingredients (you need)
 *   - unused ingredients (you have but not needed)
 *
 * NOTE: For a production app you'd likely split this into api/repo/viewmodel files.
 * Here we keep it compact for a class project.
 */

/* ----------------------------- API + DTOs ----------------------------- */

private interface SpoonacularApi {
    /**
     * Spoonacular "Search Recipes by Ingredients"
     * Docs: /recipes/findByIngredients
     *
     * - ingredients: comma-separated ingredient list (e.g. "eggs,milk,flour")
     * - number: max results (1..100)
     * - ranking:
     *      1 = maximize used ingredients first (good for "use what I already have")
     *      2 = minimize missing ingredients first
     * - ignorePantry: optional; true tends to ignore common pantry items
     */
    @GET("recipes/findByIngredients")
    suspend fun findByIngredients(
        @Query("ingredients") ingredientsCommaSeparated: String,
        @Query("number") number: Int = 20,
        @Query("ranking") ranking: Int = 1,
        @Query("ignorePantry") ignorePantry: Boolean = true,
        @Query("apiKey") apiKey: String
    ): List<RecipeByIngredientsDto>
}

@JsonClass(generateAdapter = true)
private data class RecipeByIngredientsDto(
    val id: Int,
    val title: String,
    val image: String?,
    val usedIngredientCount: Int,
    val missedIngredientCount: Int,
    val usedIngredients: List<IngredientDto>,
    val missedIngredients: List<IngredientDto>,
    val unusedIngredients: List<IngredientDto>
)

@JsonClass(generateAdapter = true)
private data class IngredientDto(
    val name: String,
    val amount: Double?,
    val unit: String?,
    val original: String? // Often easiest to show (e.g., "1 tbsp olive oil")
)

/**
 * Tiny Retrofit client helper.
 * Kept here to reduce file/class count.
 */
private object SpoonacularClient {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.spoonacular.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: SpoonacularApi = retrofit.create(SpoonacularApi::class.java)
}

/* ----------------------------- UI State ----------------------------- */

private data class UiState(
    val ingredientText: String = "",
    val ingredients: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val results: List<RecipeByIngredientsDto> = emptyList(),
    val selectedRecipe: RecipeByIngredientsDto? = null // simple “detail view” without navigation
)

/* ----------------------------- Composables ----------------------------- */

/**
 * Call this from your NavHost route, or directly from a screen.
 *
 * Example usage:
 *   GenerateRecipesPage(apiKey = BuildConfig.SPOONACULAR_API_KEY)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateRecipesPage(
    apiKey: String,
    userRepository: UserRepository,
    userId: Int,
    modifier: Modifier = Modifier,
    numberOfResults: Int = 20,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf(UiState()) }

    // If a recipe is selected, show a simple detail view.
    // This avoids creating separate screens/classes for a smaller project.
    val selected = state.selectedRecipe
    if (selected != null) {
        RecipeDetailView(
            recipe = selected,
            userRepository = userRepository,
            userId = userId,
            onBack = { state = state.copy(selectedRecipe = null) },
            modifier = modifier
        )
        return
    }

    // Scaffold gives you a proper "top bar" area for Back + Title.
    Scaffold(
        topBar = {
            TopAppBar(
                // statusBarsPadding helps when you're using enableEdgeToEdge()
                // so the title isn't jammed into the very top.
                modifier = Modifier.statusBarsPadding(),
                title = { Text("Search recipes by ingredients") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        // Your existing UI content (input, chips, button, results)
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding) // pushes content below TopAppBar
                .padding(16.dp)
        ) {
            Spacer(Modifier.height(8.dp)) // optional: adds a little breathing room

            // Ingredient input row
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = state.ingredientText,
                    onValueChange = { state = state.copy(ingredientText = it) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Ingredient") },
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    val next = state.ingredientText.trim()
                    if (next.isNotBlank()) {
                        // Keep distinct (case-insensitive) to avoid duplicates like "Eggs" and "eggs"
                        val updated = (state.ingredients + next)
                            .distinctBy { it.lowercase() }
                        state = state.copy(ingredients = updated, ingredientText = "")
                    }
                }) {
                    Text("Add")
                }
            }

            Spacer(Modifier.height(10.dp))

            // Chips (simple AssistChip list) for added ingredients
            IngredientChipsRow(
                ingredients = state.ingredients,
                onRemove = { toRemove ->
                    state = state.copy(ingredients = state.ingredients - toRemove)
                }
            )

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    enabled = state.ingredients.isNotEmpty() && !state.isLoading,
                    onClick = {
                        // Start loading
                        state = state.copy(isLoading = true, error = null, results = emptyList())

                        scope.launch {
                            runCatching {
                                val query = state.ingredients.joinToString(",") { it.trim() }

                                // Spoonacular call:
                                // ranking=1 -> maximize used ingredients first
                                val list = SpoonacularClient.api.findByIngredients(
                                    ingredientsCommaSeparated = query,
                                    number = numberOfResults,
                                    ranking = 1,
                                    ignorePantry = true,
                                    apiKey = apiKey
                                )

                                // Extra safety sort:
                                // Most similar = most used ingredients, then fewest missing ingredients.
                                list.sortedWith(
                                    compareByDescending<RecipeByIngredientsDto> { it.usedIngredientCount }
                                        .thenBy { it.missedIngredientCount }
                                )
                            }.onSuccess { sorted ->
                                state = state.copy(isLoading = false, results = sorted)
                            }.onFailure { e ->
                                state = state.copy(
                                    isLoading = false,
                                    error = e.message ?: "Search failed (unknown error)"
                                )
                            }
                        }
                    }
                ) {
                    Text(if (state.isLoading) "Searching..." else "Search")
                }

                Spacer(Modifier.width(12.dp))

                if (state.error != null) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(Modifier.height(14.dp))

            // Scrollable results list
            if (!state.isLoading && state.results.isEmpty()) {
                Text("Add ingredients and press Search to see recipes.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.results, key = { it.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onOpen = { state = state.copy(selectedRecipe = recipe) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IngredientChipsRow(
    ingredients: List<String>,
    onRemove: (String) -> Unit
) {
    if (ingredients.isEmpty()) return

    // Simple wrap layout without extra dependencies
    FlowRowCompat(horizontalSpacing = 8.dp, verticalSpacing = 8.dp) {
        ingredients.forEach { ing ->
            AssistChip(
                onClick = { onRemove(ing) },
                label = { Text(ing) },
                trailingIcon = { Text("✕") }
            )
        }
    }
}

/**
 * A single recipe card in the results list.
 * Click "Open" to go to the detail view (still within the same file/screen).
 */
@Composable
private fun RecipeCard(
    recipe: RecipeByIngredientsDto,
    onOpen: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Row {
                AsyncImage(
                    model = recipe.image,
                    contentDescription = recipe.title,
                    modifier = Modifier.size(92.dp)
                )

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(4.dp))

                    Text("Uses: ${recipe.usedIngredientCount} • Missing: ${recipe.missedIngredientCount}")

                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { expanded = !expanded }) {
                            Text(if (expanded) "Hide" else "Show ingredients")
                        }
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(onClick = onOpen) { Text("Open") }
                    }
                }
            }

            if (expanded) {
                Spacer(Modifier.height(10.dp))
                IngredientSection("Uses (you have)", recipe.usedIngredients)
                Spacer(Modifier.height(8.dp))
                IngredientSection("Missing (you need)", recipe.missedIngredients)
                Spacer(Modifier.height(8.dp))
                IngredientSection("Unused (you have but not needed)", recipe.unusedIngredients)
            }
        }
    }
}

/**
 * Simple detail view. For a bigger app you could navigate to another route
 * and call /recipes/{id}/information, but this keeps it minimal.
 */
@Composable
private fun RecipeDetailView(
    recipe: RecipeByIngredientsDto,
    userRepository: UserRepository,
    userId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    val scope = rememberCoroutineScope() // Needed for the database "launch"
    val context = androidx.compose.ui.platform.LocalContext.current // Needed for the Toast message

    Column(modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onBack) { Text("Back") }

            Spacer(Modifier.weight(1f)) // This pushes the next button to the far right

            // --- NEW SAVE BUTTON ---
            Button(
                onClick = {
                    scope.launch {
                        // Create the database object using the API data
                        val toSave = com.example.project1.data.local.SavedRecipes(
                            id = recipe.id,
                            userId = userId,
                            title = recipe.title,
                            imageUrl = recipe.image,
                            summary = "Uses ${recipe.usedIngredientCount} ingredients"
                        )

                        // Perform the save
                        userRepository.saveRecipe(toSave)

                        // Show a quick success message
                        android.widget.Toast.makeText(context, "Recipe Saved!", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Save")
            }
        }
        Spacer(Modifier.height(12.dp))

        AsyncImage(
            model = recipe.image,
            contentDescription = recipe.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )

        Spacer(Modifier.height(10.dp))

        Text(recipe.title, style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(12.dp))

        // Scrollable ingredient info
        Column(Modifier.verticalScroll(rememberScrollState())) {
            IngredientSection("Uses (you have)", recipe.usedIngredients)
            Spacer(Modifier.height(10.dp))
            IngredientSection("Missing (you need)", recipe.missedIngredients)
            Spacer(Modifier.height(10.dp))
            IngredientSection("Unused (you have but not needed)", recipe.unusedIngredients)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun IngredientSection(title: String, items: List<IngredientDto>) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(6.dp))

    if (items.isEmpty()) {
        Text("None")
        return
    }

    items.forEach { ing ->
        // Prefer "original" if available (already nicely formatted by the API)
        val line = ing.original ?: buildString {
            append(ing.name)
            if (ing.amount != null) append(" • ${ing.amount}")
            if (!ing.unit.isNullOrBlank()) append(" ${ing.unit}")
        }
        Text("• $line")
    }
}

/* ----------------------------- Small FlowRow helper ----------------------------- */
/**
 * Tiny flow layout so your chips wrap lines without importing extra libraries.
 */
@Composable
private fun FlowRowCompat(
    horizontalSpacing: Dp,
    verticalSpacing: Dp,
    content: @Composable () -> Unit
) {
    Layout(content = content) { measurables, constraints ->
        val hSpace = horizontalSpacing.roundToPx()
        val vSpace = verticalSpacing.roundToPx()

        val placeables = measurables.map { it.measure(constraints) }

        var x = 0
        var y = 0
        var rowHeight = 0

        val positions = ArrayList<Pair<Int, Int>>(placeables.size)

        placeables.forEach { p ->
            if (x + p.width > constraints.maxWidth) {
                // move to next row
                x = 0
                y += rowHeight + vSpace
                rowHeight = 0
            }
            positions += x to y
            x += p.width + hSpace
            rowHeight = maxOf(rowHeight, p.height)
        }

        layout(constraints.maxWidth, y + rowHeight) {
            placeables.forEachIndexed { i, p ->
                val (px, py) = positions[i]
                p.place(px, py)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SpoonacularRecipeSearchScreen_Preview() {
    // IMPORTANT: Preview should not make real API calls.
    // So we preview a small “fake UI” with sample data instead.
    val sample = RecipeByIngredientsDto(
        id = 1,
        title = "Preview Recipe",
        image = null,
        usedIngredientCount = 2,
        missedIngredientCount = 1,
        usedIngredients = listOf(
            IngredientDto(name = "egg", amount = 2.0, unit = "", original = "2 eggs"),
            IngredientDto(name = "milk", amount = 1.0, unit = "cup", original = "1 cup milk")
        ),
        missedIngredients = listOf(
            IngredientDto(name = "flour", amount = 2.0, unit = "cups", original = "2 cups flour")
        ),
        unusedIngredients = listOf(
            IngredientDto(name = "butter", amount = 1.0, unit = "tbsp", original = "1 tbsp butter")
        )
    )

    // Preview only the card/detail UI (safe, no internet)
    MaterialTheme {
        Column(Modifier.padding(16.dp)) {
            RecipeCard(recipe = sample, onOpen = {})
            Spacer(Modifier.height(16.dp))
            RecipeDetailView(recipe = sample, onBack = {},
                userRepository = UserRepository(AppDatabase.getDatabase(LocalContext.current).userDao()),
                userId = 1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GenerateRecipesPage_Preview() {
    // IMPORTANT: Preview should not make real API calls.
    // So we preview a small “fake UI” with sample data instead.
    val sample = RecipeByIngredientsDto(
        id = 1,
        title = "Preview Recipe",
        image = null,
        usedIngredientCount = 2,
        missedIngredientCount = 1,
        usedIngredients = listOf(
            IngredientDto(name = "egg", amount = 2.0, unit = "", original = "2 eggs"),
            IngredientDto(name = "milk", amount = 1.0, unit = "cup", original = "1 cup milk")
        ),
        missedIngredients = listOf(
            IngredientDto(name = "flour", amount = 2.0, unit = "cups", original = "2 cups flour")
        ),
        unusedIngredients = listOf(
            IngredientDto(name = "butter", amount = 1.0, unit = "tbsp", original = "1 tbsp butter")
        )
    )

    // Preview only the card/detail UI (safe, no internet)
    MaterialTheme {
        Column(Modifier.padding(16.dp)) {
            RecipeCard(recipe = sample, onOpen = {})
            Spacer(Modifier.height(16.dp))
            RecipeDetailView(recipe = sample, onBack = {},
                // Add these two lines so the preview stops complaining:
                userRepository = UserRepository(AppDatabase.getDatabase(LocalContext.current).userDao()),
                userId = 1
            )
        }
    }
}

