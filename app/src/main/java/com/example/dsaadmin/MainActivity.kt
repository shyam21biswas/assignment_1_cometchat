package com.example.dsaadmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dsaadmin.CometChatUIKit.AppScaffold
import com.example.dsaadmin.ui.theme.DSAAdminTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

/*
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DSAAdminTheme {

                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") { SplashScreen(navController , onFinished = { navController.navigate("myapp"){popUpTo("splash") { inclusive = true }} }) }
                    composable("myapp") { MyApp() }


                }



            }
        }
    }
}
*/





@Composable
fun MyApp(authKey: String) {
    val navController = rememberNavController()
    val user = FirebaseAuth.getInstance().currentUser
    FirebaseFirestore.getInstance().firestoreSettings = FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build()

    NavHost(navController = navController, startDestination = if (user != null) "scaffold" else "signin") {
        composable("signin") { SignInScreenf(navController) }
        composable("home") { HomeScreen(navController, FirebaseAuth.getInstance().currentUser) }
        composable("scaffold") { AppScaffold(authKey) }

    }
}
