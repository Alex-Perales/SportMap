package com.tunalex.sportmap.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tunalex.sportmap.ui.theme.BlueVibrant
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentBottomSheet(
    amount: Double,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onPay: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf(0) }
    var cardNumber by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var yapePhone by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Medio de pago", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                }
            }
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PaymentMethodCard(
                    icon = {
                        Icon(
                            Icons.Filled.CreditCard, null,
                            modifier = Modifier.size(28.dp),
                            tint = if (selectedMethod == 0) BlueVibrant
                                   else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    label = "Tarjeta",
                    selected = selectedMethod == 0,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedMethod = 0 }
                )
                PaymentMethodCard(
                    icon = {
                        Text(
                            "Y",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = if (selectedMethod == 1) Color(0xFF6B21A8)
                                    else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    label = "Yape",
                    selected = selectedMethod == 1,
                    selectedColor = Color(0xFF6B21A8),
                    modifier = Modifier.weight(1f),
                    onClick = { selectedMethod = 1 }
                )
                PaymentMethodCard(
                    icon = {
                        Text(
                            "KR",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = if (selectedMethod == 2) Color(0xFFD97706)
                                    else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    label = "Kasnet",
                    selected = selectedMethod == 2,
                    selectedColor = Color(0xFFD97706),
                    modifier = Modifier.weight(1f),
                    onClick = { selectedMethod = 2 }
                )
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(Modifier.height(20.dp))

            when (selectedMethod) {
                0 -> {
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { cardNumber = it.filter { c -> c.isDigit() }.take(16) },
                        label = { Text("Número de tarjeta") },
                        placeholder = { Text("1234 5678 9012 3456") },
                        leadingIcon = { Icon(Icons.Filled.CreditCard, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = cardHolder,
                        onValueChange = { cardHolder = it.take(40) },
                        label = { Text("Nombre del titular") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = expiry,
                            onValueChange = { if (it.length <= 5) expiry = it },
                            label = { Text("MM/AA") },
                            placeholder = { Text("01/27") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = cvv,
                            onValueChange = { cvv = it.filter { c -> c.isDigit() }.take(3) },
                            label = { Text("CVV") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
                1 -> {
                    Text(
                        "Ingresa tu número de Yape",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = yapePhone,
                        onValueChange = { yapePhone = it.filter { c -> c.isDigit() }.take(9) },
                        label = { Text("Número de celular") },
                        leadingIcon = { Icon(Icons.Filled.Phone, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF6B21A8).copy(alpha = 0.07f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .background(Color.White, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "QR\nSimulado",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6B21A8)
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Escanea el código con tu app Yape",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    Text(
                        "Paga en cualquier agente Kasnet",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFD97706).copy(alpha = 0.07f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "KR",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFD97706)
                            )
                            Text(
                                "Código de pago",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "KSN-${(100000..999999).random()}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Presenta este código en el agente Kasnet más cercano",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total a pagar", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(
                    "S/. ${"%.2f".format(amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = BlueVibrant
                )
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { onPay() }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueVibrant)
            ) {
                Text("Pagar S/. ${"%.2f".format(amount)}", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    icon: @Composable () -> Unit,
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    selectedColor: Color = BlueVibrant,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) selectedColor else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) selectedColor.copy(alpha = 0.08f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Spacer(Modifier.height(4.dp))
            Text(
                label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}
