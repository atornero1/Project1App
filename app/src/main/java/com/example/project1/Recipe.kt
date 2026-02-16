package com.example.project1

data class Recipe(
    val id: Int,
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val instructions: List<String>
)

object RecipeRepo {
    val recipes = listOf(
        Recipe(
            id = 1,
            title = "Spaghetti Aglio e Olio",
            description = "Quick garlic & olive oil pasta.",
            ingredients = listOf(
                "200g spaghetti",
                "3 cloves garlic (sliced)",
                "3 tbsp olive oil",
                "Chili flakes",
                "Salt",
                "Parsley"
            ),
            instructions = listOf(
                "Boil pasta in salted water until al dente.",
                "Warm olive oil and sauté garlic until lightly golden.",
                "Add chili flakes.",
                "Toss pasta with oil (add a splash of pasta water if needed).",
                "Top with parsley and serve."
            )
        ),
        Recipe(
            id = 2,
            title = "Chicken Tacos",
            description = "Simple seasoned chicken tacos.",
            ingredients = listOf(
                "2 chicken breasts (diced)",
                "Taco seasoning",
                "Tortillas",
                "Lettuce",
                "Tomato",
                "Cheese",
                "Salsa"
            ),
            instructions = listOf(
                "Season chicken and cook until done.",
                "Warm tortillas.",
                "Fill with chicken and toppings.",
                "Add salsa and serve."
            )
        )
    )

    fun getById(id: Int): Recipe? = recipes.find { it.id == id }
}