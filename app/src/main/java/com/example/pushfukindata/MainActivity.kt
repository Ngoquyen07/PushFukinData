package com.example.pushfukindata

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.pushfukindata.ui.theme.PushFukinDataTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PushFukinDataTheme {

                Surface(modifier = Modifier.fillMaxSize()) {
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(16.dp)
                    ) {
                        //ManualFoodEntryScreen()
                        ManualExerciseEntryScreen()
                        //AddCustomFoodScreen()
                    }
                }

//                Surface(modifier = Modifier.fillMaxSize()) {
//                    //ManualFoodEntryScreen()
//                    ManualExerciseEntryScreen()
//                    //AddCustomFoodScreen()
//                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualFoodEntryScreen() {
    val db = Firebase.firestore
    val context = LocalContext.current

    // Các field cơ bản
    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var calo by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var carb by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var urlImage by remember { mutableStateOf("") }

    // --- TYPE (1-4) ---
    val typeOptions = listOf(1, 2, 3, 4)
    var expandedType by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(typeOptions[0]) }

    // --- QUANTITY TYPE ("gram", "ml") ---
    val qtyOptions = listOf("gram", "ml")
    var expandedQtyType by remember { mutableStateOf(false) }
    var selectedQtyType by remember { mutableStateOf(qtyOptions[0]) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(value = id, onValueChange = { id = it }, label = { Text("ID") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tên thực phẩm") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = calo, onValueChange = { calo = it }, label = { Text("Calo") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = fat, onValueChange = { fat = it }, label = { Text("Chất béo") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = carb, onValueChange = { carb = it }, label = { Text("Tinh bột") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = protein, onValueChange = { protein = it }, label = { Text("Đạm") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Số lượng mặc định") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

        // Type (1-4)
        ExposedDropdownMenuBox(expanded = expandedType, onExpandedChange = { expandedType = !expandedType }) {
            OutlinedTextField(
                value = selectedType.toString(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Loại (1-4)") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                typeOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.toString()) },
                        onClick = {
                            selectedType = option
                            expandedType = false
                        }
                    )
                }
            }
        }

        // Quantity Type ("gram"/"ml")
        ExposedDropdownMenuBox(expanded = expandedQtyType, onExpandedChange = { expandedQtyType = !expandedQtyType }) {
            OutlinedTextField(
                value = selectedQtyType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Đơn vị") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedQtyType, onDismissRequest = { expandedQtyType = false }) {
                qtyOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedQtyType = option
                            expandedQtyType = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(value = urlImage, onValueChange = { urlImage = it }, label = { Text("URL ảnh") }, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = {
                if (id.isBlank()) return@Button
                val foodData = hashMapOf(
                    "id" to id,
                    "name" to name,
                    "calo" to (calo.toFloatOrNull() ?: 0f),
                    "fat" to (fat.toFloatOrNull() ?: 0f),
                    "carb" to (carb.toFloatOrNull() ?: 0f),
                    "protein" to (protein.toFloatOrNull() ?: 0f),
                    "type" to selectedType,
                    "Quantity" to (quantity.toIntOrNull() ?: 0),
                    "quantity_type" to selectedQtyType,
                    "urlimage" to urlImage
                )
                db.collection("default_food")
                    .document(id)
                    .set(foodData)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Lưu thành công", Toast.LENGTH_SHORT).show()

                        // Reset các trường sau khi lưu thành công
                        id = ""
                        name = ""
                        calo = ""
                        fat = ""
                        carb = ""
                        protein = ""
                        quantity = ""
                        urlImage = ""
                        selectedType = typeOptions[0]
                        selectedQtyType = qtyOptions[0]
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Lỗi khi lưu dữ liệu", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lưu vào Firestore")
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualExerciseEntryScreen() {
    val db = Firebase.firestore
    val context = LocalContext.current

    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var caloBurn by remember { mutableStateOf("") }
    var unitType by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var urlImage by remember { mutableStateOf("") }

    val unitTypeOptions = listOf("min", "rep")
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Tên bài tập") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = caloBurn,
            onValueChange = { caloBurn = it },
            label = { Text("Calo đốt mỗi đơn vị") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // ComboBox cho unitType
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = unitType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Loại đơn vị") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                unitTypeOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            unitType = selectionOption
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = unit,
            onValueChange = { unit = it },
            label = { Text("Số đơn vị") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = urlImage,
            onValueChange = { urlImage = it },
            label = { Text("URL ảnh") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (id.isBlank() || name.isBlank() || caloBurn.isBlank() || unitType.isBlank() || unit.isBlank()) {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val exerciseData = hashMapOf(
                    "id" to id,
                    "name" to name,
                    "caloBurn" to (caloBurn.toIntOrNull() ?: 0),
                    "unitType" to unitType,
                    "unit" to (unit.toIntOrNull() ?: 1),
                    "urlImage" to urlImage
                )

                db.collection("default_exercise").document(id)
                    .set(exerciseData)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Lưu thành công", Toast.LENGTH_SHORT).show()

                        // Reset các trường
                        id = ""
                        name = ""
                        caloBurn = ""
                        unitType = ""
                        unit = ""
                        urlImage = ""
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Lỗi khi lưu dữ liệu", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lưu vào Firestore")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomFoodScreen() {
    val db = Firebase.firestore
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var calo by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var carb by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var urlImage by remember { mutableStateOf("") }

    val typeOptions = listOf(1, 2, 3, 4)
    var expandedType by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(typeOptions[0]) }

    val qtyOptions = listOf("gram", "ml")
    var expandedQtyType by remember { mutableStateOf(false) }
    var selectedQtyType by remember { mutableStateOf(qtyOptions[0]) }

    // Tính số lượng hiển thị mặc định dựa trên loại đơn vị
    val displayedQuantity = if (selectedQtyType == "gram") "100" else "250"

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Tên thực phẩm") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = calo,
            onValueChange = { calo = it },
            label = { Text("Calo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = fat,
            onValueChange = { fat = it },
            label = { Text("Chất béo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = carb,
            onValueChange = { carb = it },
            label = { Text("Tinh bột") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = protein,
            onValueChange = { protein = it },
            label = { Text("Đạm") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Số lượng mặc định (readonly)
        OutlinedTextField(
            value = displayedQuantity,
            onValueChange = {},
            label = { Text("Số lượng mặc định") },
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        // Type (1-4)
        ExposedDropdownMenuBox(expanded = expandedType, onExpandedChange = { expandedType = !expandedType }) {
            OutlinedTextField(
                value = selectedType.toString(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Loại (1-4)") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                typeOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.toString()) },
                        onClick = {
                            selectedType = option
                            expandedType = false
                        }
                    )
                }
            }
        }

        // Quantity Type ("gram"/"ml")
        ExposedDropdownMenuBox(expanded = expandedQtyType, onExpandedChange = { expandedQtyType = !expandedQtyType }) {
            OutlinedTextField(
                value = selectedQtyType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Đơn vị") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedQtyType, onDismissRequest = { expandedQtyType = false }) {
                qtyOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedQtyType = option
                            expandedQtyType = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = urlImage,
            onValueChange = { urlImage = it },
            label = { Text("URL ảnh") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val foodData = hashMapOf(
                    "name" to name,
                    "calo" to (calo.toFloatOrNull() ?: 0f),
                    "fat" to (fat.toFloatOrNull() ?: 0f),
                    "carb" to (carb.toFloatOrNull() ?: 0f),
                    "protein" to (protein.toFloatOrNull() ?: 0f),
                    "type" to selectedType,
                    "Quantity" to displayedQuantity.toInt(),
                    "quantity_type" to selectedQtyType,
                    "urlimage" to urlImage
                )

                db.collection("default_food")
                    .add(foodData)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Lưu thành công", Toast.LENGTH_SHORT).show()
                        name = ""
                        calo = ""
                        fat = ""
                        carb = ""
                        protein = ""
                        urlImage = ""
                        selectedType = typeOptions[0]
                        selectedQtyType = qtyOptions[0]
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Lỗi khi lưu dữ liệu", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Thêm thực phẩm tùy chỉnh")
        }
    }
}


