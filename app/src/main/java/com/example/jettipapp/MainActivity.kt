package com.example.jettipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jettipapp.ui.theme.JetTipAppTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetTipAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyJetTipApp(innerPadding)
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MyJetTipApp(innerPadding: PaddingValues = PaddingValues(0.0.dp)) {
    var amountToPay by remember { mutableFloatStateOf(0f) }
    var billValue by remember { mutableFloatStateOf(0f) }
    var taxPercent by remember { mutableIntStateOf(15) }
    var numberOfPeople by remember { mutableIntStateOf(1) }

    Surface(Modifier.padding(innerPadding)) {
        Column(modifier = Modifier.padding(16.dp)) {
            TotalAmountPerPersonToPayWidget(amountToPay = amountToPay)
            Spacer(modifier = Modifier.height(12.dp))
            BillInfoWidget(
                taxPercent = taxPercent,
                numberOfPeople = numberOfPeople,
                onTaxPercentChanged = {
                    taxPercent = it
                    amountToPay = calculateAmount(billValue, taxPercent, numberOfPeople)
                },
                onBillValueChanged = {
                    billValue = it.toFloatOrNull() ?: 0f
                    amountToPay = calculateAmount(billValue, taxPercent, numberOfPeople)
                },
                onNumberOfPeopleChanged = {
                    numberOfPeople = it
                    amountToPay = calculateAmount(billValue, taxPercent, numberOfPeople)
                },
            )
        }
    }
}

@Composable
fun TotalAmountPerPersonToPayWidget(amountToPay: Float = 0.0f) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "$${String.format(Locale.US, "%.2f", amountToPay)}",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun BillInfoWidget(
    taxPercent: Int,
    numberOfPeople: Int,
    onBillValueChanged: (String) -> Unit = {},
    onNumberOfPeopleChanged: (Int) -> Unit = {},
    onTaxPercentChanged: (Int) -> Unit = {},
) {
    var fieldValue by remember { mutableStateOf(TextFieldValue("")) }
    val isBillValid = fieldValue.text.toFloatOrNull()?.let { it > 0 } ?: false

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            OutlinedTextField(
                value = fieldValue,
                onValueChange = {
                    fieldValue = it
                    onBillValueChanged(it.text)
                },
                label = { Text("Enter Bill Amount") },
                placeholder = { Text("$200") },
                leadingIcon = {
                    Icon(Icons.Default.AttachMoney, contentDescription = "Dollar")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = !isBillValid && fieldValue.text.isNotEmpty(),
                supportingText = {
                    if (!isBillValid && fieldValue.text.isNotEmpty()) {
                        Text("Enter a valid amount", color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            NumberOfPeopleToSplitOnWidget(
                numberOfPeople = numberOfPeople,
                onNumberOfPeopleChanged = onNumberOfPeopleChanged
            )

            Spacer(modifier = Modifier.height(16.dp))
            TaxPercentWidget(
                percent = taxPercent,
                onPercentChanged = onTaxPercentChanged
            )
        }
    }
}

@Composable
fun NumberOfPeopleToSplitOnWidget(
    numberOfPeople: Int,
    onNumberOfPeopleChanged: (Int) -> Unit,
) {
    Column {
        Text("Split Between", fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomRoundedIconButton(Icons.Default.Remove, "Remove", {
                if (numberOfPeople > 1) {
                    onNumberOfPeopleChanged(numberOfPeople - 1)
                }
            }, enabled = numberOfPeople > 1)

            Text(
                text = numberOfPeople.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            CustomRoundedIconButton(Icons.Default.Add, "Add", {
                onNumberOfPeopleChanged(numberOfPeople + 1)
            })
        }
    }
}

@Composable
fun TaxPercentWidget(
    percent: Int,
    onPercentChanged: (Int) -> Unit = {}
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tip", fontWeight = FontWeight.Medium)
            Text("$percent%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = percent.toFloat(),
            onValueChange = {
                onPercentChanged(it.toInt())
            },
            valueRange = 0f..100f,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("0%", color = Color.Gray)
            Text("100%", color = Color.Gray)
        }
    }
}

@Composable
fun CustomRoundedIconButton(
    icon: ImageVector,
    iconDescription: String,
    onClickCallback: () -> Unit,
    enabled: Boolean = true,
) {
    Card(
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .size(40.dp)
            .clickable(enabled = enabled, onClick = onClickCallback)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(imageVector = icon, contentDescription = iconDescription)
        }
    }
}

fun calculateAmount(bill: Float, tax: Int, people: Int): Float {
    return if (people > 0) (bill * (1 + tax / 100f)) / people else 0f
}