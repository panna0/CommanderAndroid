package com.example.commander.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.commander.Components.Btn
import com.example.commander.Components.CustomInput
import com.example.commander.Components.EditableProfilePhoto
import com.example.commander.Models.CheckEmailRequest
import com.example.commander.Models.User
import com.example.commander.Network.ApiContext
import com.example.commander.UI.AppTypography
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

enum class ValidationError {
    NONE,
    NAME,
    SURNAME,
    BIRTHDATE,
    EMAIL,
    PASSWORD,
    PASSWORD_MATCH,
    USERNAME,
    SERVER_ERROR
}

@Composable
fun RegisterScreen(
    navController: NavHostController,
    users: MutableList<User>,
    onRegister: (User) -> Unit
) {
    var step by remember { mutableStateOf(0) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var surnameError by remember { mutableStateOf<String?>(null) }
    var birthdateError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var password2Error by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var serverError by remember { mutableStateOf<String?>(null) }


    val context = LocalContext.current
    val apiContext = remember { ApiContext(context) }

    val userData = remember {
        mutableStateOf(
            User(
                first_name = "",
                last_name = "",
                date_of_birth = "",
                email = "",
                password2 = "",
                password = "",
                username = "",
                profile_image = null
            )
        )
    }

    val coroutineScope = rememberCoroutineScope()

    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.first().isUpperCase()
    }

    val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ITALY)

    val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun isValidBirthdate(birthdate: String): Boolean {
        return try {
            inputFormatter.parse(birthdate)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun getPasswordError(password: String): String? {
        if (password.length < 8) {
            return "La password deve contenere almeno 8 caratteri."
        }
        if (!password.any { it.isLowerCase() }) {
            return "La password deve contenere almeno una lettera minuscola."
        }
        if (!password.any { it.isUpperCase() }) {
            return "La password deve contenere almeno una lettera maiuscola."
        }
        if (!password.any { it.isDigit() }) {
            return "La password deve contenere almeno un numero."
        }
        if (!password.any { "!@#&()–[{}]:;',?/*~$^+=<>".contains(it) }) {
            return "La password deve contenere almeno un carattere speciale."
        }
        return null
    }

    suspend fun getEmailError(email: String): String? {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Inserisci una email valida."
        }
        return try {
            val response = apiContext.checkEmail(CheckEmailRequest(email))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.email_taken) {
                    "Email già in utilizzo."
                } else {
                    null
                }
            } else {
                "Errore di connessione al server."
            }
        } catch (e: Exception) {
            "Errore di connessione al server."
        }
    }

    LaunchedEffect(userData.value.email) {
        val email = userData.value.email
        if (email.isNotBlank()) {
            delay(500)
            emailError = getEmailError(email)
        } else {
            emailError = null
        }
    }

    LaunchedEffect(userData.value.username) {
        val username = userData.value.username
        if (username.isNotBlank()) {
            delay(500)
            usernameError = apiContext.getUsernameErr(username)
        } else {
            usernameError = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Sign Up", style = AppTypography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Write your credentials to login",
                style = AppTypography.headlineSmall,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
        Spacer(modifier = Modifier.height(64.dp))

        when (step) {
            0 -> {
                CustomInput(
                    value = userData.value.first_name,
                    onValueChange = { newValue ->
                        val formattedValue = if (newValue.isNotEmpty()) {
                            newValue.first().uppercase() + newValue.substring(1).lowercase()
                        } else {
                            newValue
                        }
                        userData.value = userData.value.copy(first_name = formattedValue)
                        nameError = null
                    },
                    label = "Nome",
                    errorMessage = nameError
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomInput(
                    value = userData.value.last_name,
                    onValueChange = { newValue ->
                        val formattedValue = if (newValue.isNotEmpty()) {
                            newValue.first().uppercase() + newValue.substring(1).lowercase()
                        } else {
                            newValue
                        }
                        userData.value = userData.value.copy(last_name = formattedValue)
                        surnameError = null
                    },
                    label = "Cognome",
                    errorMessage = surnameError
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomInput(
                    value = userData.value.date_of_birth,
                    onValueChange = {
                        userData.value = userData.value.copy(date_of_birth = it)
                        birthdateError = null
                    },
                    label = "Data di nascita",
                    isDate = true,
                    errorMessage = birthdateError
                )
            }

            1 -> {
                CustomInput(
                    value = userData.value.email,
                    onValueChange = {
                        userData.value = userData.value.copy(email = it.trim())
                        emailError = null
                    },
                    label = "Email",
                    errorMessage = emailError
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomInput(
                    value = userData.value.password,
                    onValueChange = {
                        userData.value = userData.value.copy(password = it)
                        passwordError = null
                    },
                    label = "Password",
                    isPassword = true,
                    errorMessage = passwordError
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomInput(
                    value = userData.value.password2,
                    onValueChange = {
                        userData.value = userData.value.copy(password2 = it)
                        password2Error = null
                    },
                    label = "Ripeti Password",
                    isPassword = true,
                    errorMessage = password2Error
                )
            }

            2 -> {

                EditableProfilePhoto(
                    initialPhoto = userData.value.profile_image,
                    displayName = userData.value.first_name + " " + userData.value.last_name,
                    onPhotoChanged = { newFile ->
                        userData.value = userData.value.copy(profile_image = newFile)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomInput(
                    value = userData.value.username,
                    onValueChange = {
                        userData.value = userData.value.copy(username = it.trim())
                        usernameError = null
                    },
                    label = "Username",
                    errorMessage = usernameError
                )
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (step > 0) {
                Btn(
                    onClick = { step-- },
                    text = "Back",
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Btn(
                onClick = {
                    coroutineScope.launch {
                        when (step) {
                            0 -> {
                                nameError = if (!isValidName(userData.value.first_name)) "Il nome non può essere vuoto e deve iniziare con la maiuscola." else null
                                surnameError = if (!isValidName(userData.value.last_name)) "Il cognome non può essere vuoto e deve iniziare con la maiuscola." else null
                                birthdateError = if (!isValidBirthdate(userData.value.date_of_birth)) "La data di nascita ha un formato errato. Usa il formato GG/MM/AAAA." else null

                                if (nameError == null && surnameError == null && birthdateError == null) {
                                    step++
                                }
                            }
                            1 -> {
                                passwordError = getPasswordError(userData.value.password)
                                password2Error = if (userData.value.password != userData.value.password2) "Le password non coincidono." else null

                                if (emailError == null && passwordError == null && password2Error == null) {
                                    step++
                                }
                            }
                            2 -> {
                                usernameError = apiContext.getUsernameErr(userData.value.username)

                                if (usernameError == null) {
                                    val formattedUser = userData.value.copy(
                                        date_of_birth = LocalDate.parse(
                                            userData.value.date_of_birth,
                                            inputFormatter
                                        ).format(outputFormatter)
                                    )
                                    try {
                                        val response = apiContext.register(formattedUser)
                                        if (response.isSuccessful) {
                                            onRegister(formattedUser)
                                            users.add(formattedUser)
                                            navController.navigate("login")
                                        } else {
                                            serverError = "Registrazione fallita. Riprova."
                                        }
                                    } catch (e: Exception) {
                                        serverError = "Errore di connessione al server."
                                    } finally {
                                        userData.value.profile_image?.delete()
                                    }
                                }
                            }
                        }
                    }
                },
                text = if (step < 2) "Next" else "Sign Up",
                modifier = Modifier.weight(1f)
            )
        }

        if (serverError != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(serverError!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(100.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                "Already have an account?",
                style = AppTypography.bodyMedium,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            TextButton(onClick = { navController.navigate("login") }) {
                Text("Sign in", style = AppTypography.bodyMedium)
            }
        }
    }
}