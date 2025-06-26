package com.example.dsaadmin.CometChatUIKit

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cometchat.chat.core.CometChat
import com.cometchat.chat.exceptions.CometChatException
import com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit
import com.cometchat.chatuikit.shared.cometchatuikit.UIKitSettings
import com.example.dsaadmin.MyApp
import com.example.dsaadmin.SplashScreen

class MainActivity : ComponentActivity() {

    private val appId = "277709b44bcaf7b9"
    private val region = "in"
    private val authKey = "1cb758b88b0b45d0cbfa7dde80e292aee1a694cc"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = UIKitSettings.UIKitSettingsBuilder()
            .setRegion(region)
            .setAppId(appId)
            .setAuthKey(authKey)
            .subscribePresenceForAllUsers()
            .build()

        CometChatUIKit.init(this, settings, object : CometChat.CallbackListener<String>() {
            override fun onSuccess(success: String?) {
                Log.d("CometChatInit", "Success")
                setContent {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") { SplashScreen(navController , onFinished = { navController.navigate("myapp"){popUpTo("splash") { inclusive = true }} }) }
                        composable("myapp") { MyApp(authKey) } }
                    //AppScaffold(authKey)
                }
            }


            override fun onError(e: CometChatException?) {
                Log.e("CometChatInit", "Error: ${e?.message}")
                Toast.makeText(this@MainActivity, "Init failed: ${e?.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
