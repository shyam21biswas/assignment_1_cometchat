package com.example.dsaadmin




import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

import kotlinx.coroutines.delay




@Composable
fun SplashScreen(navController: NavController, onFinished: () -> Unit) {
    var showLogo by remember { mutableStateOf(false) }
    var bye by remember { mutableStateOf(false) }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.perfect))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = 1,
        speed = 1.25f,
        isPlaying = true,
        restartOnPlay = false
    )

    LaunchedEffect(progress) {
        if (progress == 1f && !showLogo) {
            showLogo = true
            delay(2000)
            onFinished()
        }
    }

    Surface(color = Color(0xFF0D1A24)) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content (animation or logo)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (!showLogo) {
                    LottieAnimation(
                        composition,
                        progress,
                        modifier = Modifier.size(500.dp)
                    )
                } else {
                    bye = true
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.perfect),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(200.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "TrackDSA",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Footer text
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 50.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "Made with ❤️ by Moonstone",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}





@Composable
fun WelcomeScreen(navController: NavController) {
    val context = LocalContext.current
    val userName by UserPreferences.getUserName(context).collectAsState("")
    var navigateToHome by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome, $userName!!",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF145ACB)
            )
            navigateToHome = true

        }
    }
    if (navigateToHome) {
        LaunchedEffect(Unit) {
            delay(2000)
            navController.navigate("home")
            {popUpTo("signin") { inclusive = true }}

            navigateToHome = false
        }
    }

}


@Composable
fun LottieAlertDialog(showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            speed = 1f
        )

        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Awesome!",/*modifier = Modifier.clickable { solvechecker = false }*/)
                }
            },
            title = {
                Box()
                {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.height(150.dp).width(350.dp)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    Text(
                        text = "🎯 You’ve solved your daily question!",
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Keep this streak going! Consistency is the key to mastery 💪🔥",
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }
            , shape = RoundedCornerShape(24.dp)
        )
    }
}



@Composable
fun Lottiequestion(
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.load)) // your lottie file
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever,
            speed = 0.7f
        )

        // Auto-dismiss after 3 seconds
        LaunchedEffect(Unit) {
            delay(2000)
            onDismiss()
            load = false
        }

        AlertDialog(
            modifier = Modifier.height(200.dp).width(200.dp),
            onDismissRequest = onDismiss,
            buttons = {},

            title = {
                Box()
                {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.height(150.dp).width(350.dp)
                    )
                }
            },
            text = {}
            , shape = RoundedCornerShape(24.dp)

        )
    }
}

