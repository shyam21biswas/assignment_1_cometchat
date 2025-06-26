package com.example.dsaadmin.CometChatUIKit

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home



import android.content.Intent

import android.widget.Toast

import androidx.compose.foundation.layout.*

import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import com.cometchat.chat.constants.CometChatConstants

import com.cometchat.chat.core.CometChat
import com.cometchat.chat.exceptions.CometChatException
import com.cometchat.chat.models.Group
import com.cometchat.chat.models.User
import com.example.dsaadmin.HomeScreen
import com.example.dsaadmin.MessageActivity
import com.example.dsaadmin.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay


@Composable
fun AppScaffold(authKey: String) {
    val navController = rememberNavController()
    val items = listOf("home", "chat")
    var selectedItem by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val uName by UserPreferences.getUserName(context).collectAsState("")
    val userid by UserPreferences.getUseruide(context).collectAsState("")

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(70.dp),


            ) {

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item)

                        },
                        icon = {
                            if (item == "home") Icon(Icons.Default.Home, contentDescription = "Home")
                            else Icon(Icons.Default.Email, contentDescription = "Chat")
                        },
                        label = { Text(item.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController, FirebaseAuth.getInstance().currentUser) }
            composable("chat") {
                GroupChatLauncher(
                    navController,

                    uid = userid!!,
                    name = uName!!,
                    authKey = authKey,
                    groupId = "dsa_01",
                    groupType = CometChatConstants.GROUP_TYPE_PUBLIC
                )
            }
        }
    }
}
/*
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Welcome to DSA App", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Use bottom navigation to join the group chat.")
    }
}
*/


@Composable
fun GroupChatLauncher(
    navHostController: NavHostController,
    uid: String,
    name: String,
    authKey: String,
    groupId: String,
    groupType: String
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        loginOrCreateUserAndJoinGroup(context, uid, name, authKey, groupId, groupType)
    }

    Text("Joining group chat...", modifier = Modifier.padding(16.dp))

    LaunchedEffect(Unit) {
        delay(4000)
        navHostController.popBackStack()

    }

}


fun loginOrCreateUserAndJoinGroup(
    context: Context,
    uid: String,
    name: String,
    authKey: String,
    groupId: String,
    groupType: String
) {
    CometChat.login(uid, authKey, object : CometChat.CallbackListener<User>() {
        override fun onSuccess(user: User?) {
            joinGroup(context, groupId, groupType)
        }

        override fun onError(e: CometChatException?) {
            when (e?.code) {
                "ERR_UID_NOT_FOUND" -> {
                    val newUser = User().apply {
                        this.uid = uid
                        this.name = name
                    }
                    CometChat.createUser(newUser, authKey, object : CometChat.CallbackListener<User>() {
                        override fun onSuccess(user: User?) {
                            loginOrCreateUserAndJoinGroup(context, uid, name, authKey, groupId, groupType)
                        }

                        override fun onError(e: CometChatException?) {
                            Toast.makeText(context, "Create user failed: ${e?.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }

                "ERR_ALREADY_LOGGED_IN" -> {
                    joinGroup(context, groupId, groupType)
                }

                else -> {
                    Toast.makeText(context, "Login error: ${e?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    })
}

fun joinGroup(context: Context, groupId: String, groupType: String) {
    CometChat.joinGroup(groupId, groupType, "", object : CometChat.CallbackListener<Group>() {
        override fun onSuccess(group: Group?) {
            val intent = Intent(context, MessageActivity::class.java)
            intent.putExtra("guid", groupId)
            context.startActivity(intent)
        }

        override fun onError(e: CometChatException?) {
            if (e?.code == "ERR_ALREADY_JOINED") {
                val intent = Intent(context, MessageActivity::class.java)
                intent.putExtra("guid", groupId)
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Group join failed: ${e?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    })
}
