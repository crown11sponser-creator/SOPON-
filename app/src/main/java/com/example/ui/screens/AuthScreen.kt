package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DemoWarningBanner
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String, String, String?) -> Unit,
    onQuickDemoUser: () -> Unit,
    onQuickAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isRegisterMode by remember { mutableStateOf(false) }

    // Login fields
    var loginIdentifier by remember { mutableStateOf("demo@example.com") }
    var loginPassword by remember { mutableStateOf("password123") }
    var rememberMe by remember { mutableStateOf(true) }

    // Register fields
    var regFullName by remember { mutableStateOf("") }
    var regMobile by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var regReferralCode by remember { mutableStateOf("USER48291") }
    var termsAccepted by remember { mutableStateOf(true) }

    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("auth_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // App Logo Icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(EmeraldDark, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Token,
                contentDescription = "Demo Rewards Logo",
                tint = GoldAmber,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Demo Rewards",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                color = Navy900
            )
        )

        Text(
            text = "Simulated Rewards & Product Plans Platform",
            style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mandatory Demo Warning Banner
        DemoWarningBanner()

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Mode Toggle Tabs
                TabRow(
                    selectedTabIndex = if (isRegisterMode) 1 else 0,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = EmeraldDark
                ) {
                    Tab(
                        selected = !isRegisterMode,
                        onClick = { isRegisterMode = false; errorMessage = null },
                        text = { Text("Login", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = isRegisterMode,
                        onClick = { isRegisterMode = true; errorMessage = null },
                        text = { Text("Register", fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isRegisterMode) {
                    // Login Mode
                    OutlinedTextField(
                        value = loginIdentifier,
                        onValueChange = { loginIdentifier = it; errorMessage = null },
                        label = { Text("Email or Mobile") },
                        modifier = Modifier.fillMaxWidth().testTag("login_identifier_input"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = { loginPassword = it; errorMessage = null },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth().testTag("login_password_input"),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it }
                            )
                            Text("Remember me", style = MaterialTheme.typography.bodySmall)
                        }

                        TextButton(onClick = { showForgotPasswordDialog = true }) {
                            Text("Forgot password?", style = MaterialTheme.typography.bodySmall.copy(color = EmeraldDark))
                        }
                    }

                    errorMessage?.let { err ->
                        Text(text = err, color = RedAccent, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (loginIdentifier.isBlank() || loginPassword.isBlank()) {
                                errorMessage = "Please enter both credentials"
                                return@Button
                            }
                            onLogin(loginIdentifier, loginPassword)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("login_submit_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Sign In (Demo)", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick switch helper buttons for easy evaluator testing
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onQuickDemoUser,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Alex Mercer (User)", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = onQuickAdmin,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Demo Admin", fontSize = 11.sp, color = GoldDark)
                        }
                    }
                } else {
                    // Registration Mode
                    OutlinedTextField(
                        value = regFullName,
                        onValueChange = { regFullName = it; errorMessage = null },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("reg_fullname_input"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = regMobile,
                        onValueChange = { regMobile = it; errorMessage = null },
                        label = { Text("Mobile Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("reg_mobile_input"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it; errorMessage = null },
                        label = { Text("Email Address") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth().testTag("reg_email_input"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = regPassword,
                        onValueChange = { regPassword = it; errorMessage = null },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth().testTag("reg_password_input"),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = regConfirmPassword,
                        onValueChange = { regConfirmPassword = it; errorMessage = null },
                        label = { Text("Confirm Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.LockClock, contentDescription = null) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = regReferralCode,
                        onValueChange = { regReferralCode = it },
                        label = { Text("Referral / Invitation Code (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.GroupAdd, contentDescription = null) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it }
                        )
                        Text(
                            text = "I acknowledge that this is purely a DEMO / simulation platform with virtual credits and no real money.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp)
                        )
                    }

                    errorMessage?.let { err ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = err, color = RedAccent, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (regFullName.isBlank() || regMobile.isBlank() || regEmail.isBlank() || regPassword.isBlank()) {
                                errorMessage = "Please fill in all mandatory fields"
                                return@Button
                            }
                            if (regPassword != regConfirmPassword) {
                                errorMessage = "Passwords do not match"
                                return@Button
                            }
                            if (!termsAccepted) {
                                errorMessage = "Please accept the demo terms checkbox"
                                return@Button
                            }
                            onRegister(
                                regFullName.trim(),
                                regMobile.trim(),
                                regEmail.trim(),
                                regPassword,
                                regReferralCode.ifBlank { null }
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("register_submit_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Create Demo Account (+₹1,500 DEMO)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Demo Password Reset", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This is a demonstration app. You can use the seeded demo accounts: demo@example.com (pass: password123) or admin@demorewards.example (pass: admin123)."
                )
            },
            confirmButton = {
                Button(
                    onClick = { showForgotPasswordDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Text("OK")
                }
            }
        )
    }
}
