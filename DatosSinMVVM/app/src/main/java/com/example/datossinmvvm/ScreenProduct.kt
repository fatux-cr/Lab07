package com.example.datossinmvvm

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenProduct(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val db = remember { UserDatabase.getDatabase(context) }
    val dao = db.productDao()
    val coroutineScope = rememberCoroutineScope()

    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var productsList by remember { mutableStateOf<List<Product>>(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("CRUD de Productos") },
                actions = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            try {
                                val product = Product(
                                    id = if (id.isNotBlank()) id.toInt() else 0,
                                    name = name,
                                    price = price.toDoubleOrNull() ?: 0.0,
                                    description = description
                                )
                                dao.insert(product)
                                snackbarHostState.showSnackbar("Producto agregado")
                                productsList = dao.getAll()
                                name = ""
                                price = ""
                                description = ""
                                id = ""
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error al agregar")
                                Log.e("DB", e.message ?: "")
                            }
                        }
                    }) { Text("Agregar") }

                    TextButton(onClick = {
                        coroutineScope.launch {
                            productsList = dao.getAll()
                        }
                    }) { Text("Listar") }

                    TextButton(onClick = {
                        coroutineScope.launch {
                            try {
                                val product = Product(
                                    id = id.toInt(),
                                    name = name,
                                    price = price.toDoubleOrNull() ?: 0.0,
                                    description = description
                                )
                                dao.update(product)
                                snackbarHostState.showSnackbar("Producto actualizado")
                                productsList = dao.getAll()
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error al actualizar")
                            }
                        }
                    }) { Text("Actualizar") }

                    TextButton(onClick = {
                        coroutineScope.launch {
                            try {
                                val deleted = dao.deleteById(id.toInt())
                                snackbarHostState.showSnackbar(
                                    if (deleted > 0) "Producto eliminado"
                                    else "ID no encontrado"
                                )
                                productsList = dao.getAll()
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error al eliminar")
                            }
                        }
                    }) { Text("Eliminar") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = id,
                onValueChange = { id = it },
                label = { Text("ID (para editar o eliminar)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del producto") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Precio") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Text("Lista de productos:", fontSize = 20.sp)
            Spacer(Modifier.height(8.dp))
            productsList.forEach { product ->
                Text("${product.id}. ${product.name} - S/${product.price} (${product.description})")
            }
        }
    }
}
