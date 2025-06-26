package com.example.dsaadmin

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object UserPreferences {
    private val Context.dataStore by preferencesDataStore(name = "user_prefs")

    val questionsStatusKey = stringPreferencesKey("questions_status_json")
    val username = stringPreferencesKey("name")
    val uidata = stringPreferencesKey("uiddataa")
    val companydata = stringPreferencesKey("companydata")

    suspend fun saveQuestionsStatus(context: Context, statusMap: Map<String, Boolean>) {
        val json = Gson().toJson(statusMap)
        context.dataStore.edit { prefs ->
            prefs[questionsStatusKey] = json
        }
    }
    //gave company data
    suspend fun saveCompanyData(context: Context, comp:List<Company>) {
        val json = Gson().toJson(comp)
        context.dataStore.edit { prefs ->
            prefs[questionsStatusKey] = json
        }

    }
//     fun getCompanyData(context: Context): Flow<List<Company>> {
//        val jsonFlow = context.dataStore.data.map { prefs ->
//            prefs[questionsStatusKey] ?: "[]"
//        }
//        val json = jsonFlow.first()
//        return Gson().fromJson(json, object : TypeToken<List<Company>>() {}.type)
//
//    }


    suspend fun storename(context: Context, name: String) {
        context.dataStore.edit { prefs ->
            prefs[username] = name
        }
    }

    fun getUserName(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[username]
        }
    }

    suspend fun storeuid(context: Context, name: String) {
        context.dataStore.edit { prefs ->
            prefs[uidata] = name
        }
    }

    fun getUseruide(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[uidata]
        }
    }



    suspend fun loadQuestionsStatus(context: Context): Map<String, Boolean> {
        val jsonFlow = context.dataStore.data.map { prefs ->
            prefs[questionsStatusKey] ?: "{}"
        }
        val json = jsonFlow.first()
        return Gson().fromJson(json, object : TypeToken<Map<String, Boolean>>() {}.type)
    }
    suspend fun getCompanyData(context: Context): List<Company> {
        val jsonFlow = context.dataStore.data.map { prefs ->
            prefs[companydata] ?: "[]"
        }
        val json = jsonFlow.first()
        return Gson().fromJson(json, object : TypeToken<List<Company>>() {}.type)

    }
}
