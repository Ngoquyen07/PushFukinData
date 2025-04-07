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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PushFukinDataTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    //ManualFoodEntryScreen()
                    ManualExerciseEntryScreen()
                }
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

    // Các field cơ bản
    var name by remember { mutableStateOf("") }
    var caloPerHour by remember { mutableStateOf("") }
    var urlImage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Nhập tên bài tập
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Tên bài tập") },
            modifier = Modifier.fillMaxWidth()
        )

        // Nhập calo mỗi giờ
        OutlinedTextField(
            value = caloPerHour,
            onValueChange = { caloPerHour = it },
            label = { Text("Calo mỗi giờ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Nhập URL ảnh
        OutlinedTextField(
            value = urlImage,
            onValueChange = { urlImage = it },
            label = { Text("URL ảnh") },
            modifier = Modifier.fillMaxWidth()
        )

        // Nút lưu dữ liệu vào Firestore
        Button(
            onClick = {
                if (name.isBlank() || caloPerHour.isBlank()) {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val exerciseData = hashMapOf(
                    "name" to name,
                    "calo_per_hour" to (caloPerHour.toIntOrNull() ?: 0),
                    "url_image" to urlImage
                )

                // Tạo ID tự động và lưu dữ liệu
                val newDocRef = db.collection("default_exercise").document()

                newDocRef.set(exerciseData)
                    .addOnSuccessListener {
                        // Hiển thị thông báo thành công
                        Toast.makeText(context, "Lưu thành công", Toast.LENGTH_SHORT).show()

                        // Reset các trường sau khi lưu thành công
                        name = ""
                        caloPerHour = ""
                        urlImage = ""
                    }
                    .addOnFailureListener {
                        // Hiển thị thông báo lỗi nếu có lỗi trong quá trình lưu dữ liệu
                        Toast.makeText(context, "Lỗi khi lưu dữ liệu", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lưu vào Firestore")
        }
    }
}


