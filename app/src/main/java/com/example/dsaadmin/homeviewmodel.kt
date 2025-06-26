package com.example.dsaadmin



import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dsaadmin.UserPreferences.saveQuestionsStatus
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.Source

class HomeViewModel(private val user: FirebaseUser?) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _companies = MutableStateFlow<List<Company>>(emptyList())
    val companies: StateFlow<List<Company>> = _companies

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    private val _questionStatusMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val questionStatusMap: StateFlow<Map<String, Boolean>> = _questionStatusMap

    private val questionsCache = mutableMapOf<String, List<Question>>()

    private val _totalSolved = MutableStateFlow(0)
    val totalSolved: StateFlow<Int> = _totalSolved

    private val _totalQuestions = MutableStateFlow(0)
    val totalQuestions: StateFlow<Int> = _totalQuestions

    init {
        loadUserStatus()
    }

    private fun loadUserStatus() {
        viewModelScope.launch {
            user?.let {
                // Load from Firestore
                try {
                    val userDoc = firestore.collection("users").document(user.uid).get().await()
                    val remoteStatus = userDoc.get("questionsStatus") as? Map<String, Boolean>
                    remoteStatus?.let {
                        _questionStatusMap.value = it
                    }
                } catch (e: Exception) {
                    Log.e("Firestore", "Error fetching user status", e)
                }
            }
        }
    }

    fun recalculateCompanyStats() {
        viewModelScope.launch {
            val currentCompanies = _companies.value.toMutableList()
            var solvedSum = 0

            currentCompanies.forEachIndexed { index, company ->
                val questions = questionsCache[company.id] ?: return@forEachIndexed
                val solved = questions.count { _questionStatusMap.value[it.id] == true }
                solvedSum += solved
                currentCompanies[index] = company.copy(solved = solved)
            }

            _companies.value = currentCompanies
           // _totalSolved.value = solvedSum
        }
    }


    fun loadCompanies() {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("companies").get().await()

                val companyList = mutableListOf<Company>()
                var solvedSum = 0
                var totalSum = 0

                for (companyDoc in snapshot.documents) {
                    val questionsSnapshot = firestore.collection("companies")
                        .document(companyDoc.id)
                        .collection("questions")
                        .get()
                        .await()

                    val total = questionsSnapshot.size()
                    val solved = questionsSnapshot.count { _questionStatusMap.value[it.id] == true }

                    companyList.add(
                        Company(
                            id = companyDoc.id,
                            name = companyDoc.getString("name") ?: "",
                            logoUrl = companyDoc.getString("logoUrl") ?: "",
                            solved = solved,
                            total = total
                        )
                    )

                    solvedSum += solved
                    totalSum += total
                }



                _companies.value = companyList
                _totalSolved.value = solvedSum
                _totalQuestions.value = totalSum
            } catch (e: Exception) {
                Log.e("Firestore", "Error loading companies", e)
            }
        }
    }



    //this is the third version   server first than cache
    fun loadQuestionsForCompany(companyId: String) {
        viewModelScope.launch {
            val cached = questionsCache[companyId]
            if (cached != null) {
                _questions.value = cached
                return@launch
            }

            try {
                // Try SERVER first
                val serverSnapshot = firestore.collection("companies")
                    .document(companyId)
                    .collection("questions")
                    .get(Source.SERVER)
                    .await()

                val fetchedQuestions = serverSnapshot.map { doc ->
                    Question(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        link = doc.getString("link"),
                        leetnumber = doc.getString("leetnumber"),
                        tags = doc.get("tags") as? List<String> ?: emptyList(),
                        difficulty = doc.getString("difficulty") ?: "Unknown"
                    )
                }

                questionsCache[companyId] = fetchedQuestions
                _questions.value = fetchedQuestions

            } catch (e: Exception) {
                Log.w("Firestore", "SERVER fetch failed, trying CACHE", e)

                try {
                    val cacheSnapshot = firestore.collection("companies")
                        .document(companyId)
                        .collection("questions")
                        .get(Source.CACHE)
                        .await()

                    val fallbackQuestions = cacheSnapshot.map { doc ->
                        Question(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            link = doc.getString("link"),
                            leetnumber = doc.getString("leetnumber"),
                            tags = doc.get("tags") as? List<String> ?: emptyList(),
                            difficulty = doc.getString("difficulty") ?: "Unknown"
                        )
                    }

                    questionsCache[companyId] = fallbackQuestions
                    _questions.value = fallbackQuestions

                } catch (cacheError: Exception) {
                    Log.e("Firestore", "CACHE fetch also failed", cacheError)
                    _questions.value = emptyList()
                }
            }
        }
    }






    fun toggleQuestionStatus(questionId: String, newStatus: Boolean) {
        viewModelScope.launch {
            _questionStatusMap.value = _questionStatusMap.value.toMutableMap().apply {
                put(questionId, newStatus)
            }

            user?.let {
                firestore.collection("users")
                    .document(it.uid)
                    .update("questionsStatus.$questionId", newStatus)
            }

            // Recalculate stats without refetching
            recalculateCompanyStats()








            if(newStatus) {
                _totalSolved.value = totalSolved.value+1

            }
            else{
                _totalSolved.value = totalSolved.value-1;

        }
    }
    }





}
