package com.example.mathtutor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MathQuestGame()
                }
            }
        }
    }
}

private enum class Operation(val symbol: String, val title: String) {
    ADD("+", "Addition"),
    SUBTRACT("−", "Subtraction"),
    MULTIPLY("×", "Multiplication"),
    DIVIDE("÷", "Division")
}

private data class Puzzle(
    val left: Int,
    val right: Int,
    val operation: Operation,
    val answer: Int,
    val options: List<Int>
)

@Composable
private fun MathQuestGame() {
    var selectedOperation by remember { mutableStateOf(Operation.ADD) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var puzzle by remember { mutableStateOf(generatePuzzle(selectedOperation)) }
    var feedback by remember { mutableStateOf("Pick the right answer to earn stars ⭐") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF2E335A), Color(0xFF1C1B33))
                )
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "Math Quest Kids",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Solve mini-puzzles and level up your hero!",
                color = Color(0xFFC4C7FF)
            )

            StatsRow(score = score, streak = streak)
            OperationPicker(
                selectedOperation = selectedOperation,
                onSelected = {
                    selectedOperation = it
                    puzzle = generatePuzzle(it)
                    feedback = "Great! New ${it.title.lowercase()} challenge unlocked."
                }
            )

            PuzzleCard(
                puzzle = puzzle,
                onAnswerClick = { option ->
                    if (option == puzzle.answer) {
                        score += 10
                        streak += 1
                        feedback = "Awesome! +10 points"
                    } else {
                        streak = 0
                        feedback = "Nice try! Correct answer: ${puzzle.answer}"
                    }
                    puzzle = generatePuzzle(selectedOperation)
                }
            )

            Text(
                text = feedback,
                color = Color(0xFFFFE082),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StatsRow(score: Int, streak: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatPill(label = "Score", value = score.toString(), color = Color(0xFF00E5FF), modifier = Modifier.weight(1f))
        StatPill(label = "Streak", value = "🔥 $streak", color = Color(0xFFFFB74D), modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatPill(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF312F59)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, color = Color(0xFFB4BAFF), fontSize = 13.sp)
            Text(text = value, color = color, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
    }
}

@Composable
private fun OperationPicker(selectedOperation: Operation, onSelected: (Operation) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(Operation.entries) { operation ->
            val active = operation == selectedOperation
            Button(
                onClick = { onSelected(operation) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (active) Color(0xFF6C63FF) else Color(0xFF444175)
                )
            ) {
                Text(text = operation.title)
            }
        }
    }
}

@Composable
private fun PuzzleCard(puzzle: Puzzle, onAnswerClick: (Int) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2951)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Puzzle", color = Color(0xFFA8B1FF), fontWeight = FontWeight.SemiBold)
            Text(
                text = "${puzzle.left} ${puzzle.operation.symbol} ${puzzle.right} = ?",
                fontSize = 36.sp,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                puzzle.options.forEach { option ->
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(width = 260.dp, height = 56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A54D4)),
                        onClick = { onAnswerClick(option) }
                    ) {
                        Text(text = option.toString(), fontSize = 22.sp)
                    }
                }
            }
        }
    }
}

private fun generatePuzzle(operation: Operation): Puzzle {
    val left = Random.nextInt(1, 13)
    val right = Random.nextInt(1, 13)

    val normalized = when (operation) {
        Operation.ADD -> left to right
        Operation.SUBTRACT -> maxOf(left, right) to minOf(left, right)
        Operation.MULTIPLY -> left to right
        Operation.DIVIDE -> {
            val divisor = Random.nextInt(1, 11)
            val quotient = Random.nextInt(1, 11)
            (divisor * quotient) to divisor
        }
    }

    val answer = when (operation) {
        Operation.ADD -> normalized.first + normalized.second
        Operation.SUBTRACT -> normalized.first - normalized.second
        Operation.MULTIPLY -> normalized.first * normalized.second
        Operation.DIVIDE -> normalized.first / normalized.second
    }

    val wrongAnswers = buildSet {
        while (size < 3) {
            val candidate = (answer + Random.nextInt(-10, 11)).coerceAtLeast(0)
            if (candidate != answer) add(candidate)
        }
    }

    val options = (wrongAnswers + answer).shuffled()

    return Puzzle(
        left = normalized.first,
        right = normalized.second,
        operation = operation,
        answer = answer,
        options = options
    )
}
