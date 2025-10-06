package com.example.datossinmvvm

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenUser(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val db = remember { crearDatabase(context) }
    val dao = db.userDao()
    val coroutineScope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dataUser by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios") },
                actions = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            if (firstName.isNotBlank() || lastName.isNotBlank()) {
                                AgregarUsuario(User(0, firstName, lastName), dao)
                                firstName = ""
                                lastName = ""
                                dataUser = getUsers(dao)
                                snackbarHostState.showSnackbar("Usuario agregado")
                            } else {
                                snackbarHostState.showSnackbar("Ingrese nombre o apellido")
                            }
                        }
                    }) { Text("Agregar") }

                    TextButton(onClick = {
                        coroutineScope.launch {
                            dataUser = getUsers(dao)
                        }
                    }) { Text("Listar") }

                    TextButton(onClick = {
                        coroutineScope.launch {
                            val deleted = dao.deleteLast()
                            snackbarHostState.showSnackbar(
                                if (deleted > 0) "Último usuario eliminado" else "No hay registros"
                            )
                            dataUser = getUsers(dao)
                        }
                    }) { Text("Eliminar") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name:") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name:") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Text("Usuarios registrados:", fontSize = 20.sp)
            Spacer(Modifier.height(8.dp))
            Text(text = dataUser, fontSize = 18.sp)
        }
    }
}

fun crearDatabase(context: Context): UserDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        UserDatabase::class.java,
        "user_db"
    ).build()
}

suspend fun getUsers(dao: UserDao): String {
    var rpta = ""
    val users = dao.getAll()
    users.forEach { user ->
        rpta += "${user.firstName} - ${user.lastName}\n"
    }
    return rpta
}

suspend fun AgregarUsuario(user: User, dao: UserDao) {
    try {
        dao.insert(user)
    } catch (e: Exception) {
        Log.e("User", "Error insert: ${e.message}")
    }
}
