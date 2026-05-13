package com.antharjala.watch.viewmodel

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.antharjala.watch.data.local.AppDatabase
import com.antharjala.watch.data.model.AlertItem
import com.antharjala.watch.data.model.BorewellEntry
import com.antharjala.watch.data.model.ZoneData
import com.antharjala.watch.data.repository.BorewellRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

private val Application.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")
private val USER_ID_KEY = stringPreferencesKey("user_id")
private val NICKNAME_KEY = stringPreferencesKey("nickname")

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = BorewellRepository(db.borewellDao(), db.alertDao())

    // ── User ID (anonymous UUID) ──────────────────────────────────────────────
    private val _userId = MutableStateFlow("")
    val userId: StateFlow<String> = _userId.asStateFlow()

    private val _nickname = MutableStateFlow("Farmer")
    val nickname: StateFlow<String> = _nickname.asStateFlow()

    // ── Data Streams ──────────────────────────────────────────────────────────
    val allEntries: StateFlow<List<BorewellEntry>> = repository.allEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val zoneData: StateFlow<List<ZoneData>> = repository.zoneData
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alerts: StateFlow<List<AlertItem>> = repository.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadAlertCount: StateFlow<Int> = repository.unreadAlertCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // ── Log Form State ────────────────────────────────────────────────────────
    private val _logSuccess = MutableStateFlow(false)
    val logSuccess: StateFlow<Boolean> = _logSuccess.asStateFlow()

    private val _contributionCount = MutableStateFlow(0)
    val contributionCount: StateFlow<Int> = _contributionCount.asStateFlow()

    // ── User Entries ──────────────────────────────────────────────────────────
    val userEntries: StateFlow<List<BorewellEntry>> = _userId
        .flatMapLatest { uid ->
            if (uid.isBlank()) flowOf(emptyList())
            else repository.entriesForUser(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            // Load or create anonymous user ID
            application.dataStore.data.first().let { prefs ->
                val savedId = prefs[USER_ID_KEY]
                val id = if (savedId.isNullOrBlank()) {
                    val newId = UUID.randomUUID().toString()
                    application.dataStore.edit { it[USER_ID_KEY] = newId }
                    newId
                } else savedId
                _userId.value = id

                val savedNick = prefs[NICKNAME_KEY]
                if (!savedNick.isNullOrBlank()) _nickname.value = savedNick
            }

            // Seed demo data
            repository.seedIfEmpty()

            // Load contribution count
            refreshContributionCount()
        }
    }

    fun logBorewell(
        lat: Double,
        lng: Double,
        depthFt: Float,
        yearDrilled: Int,
        yieldInchesPerHour: Float
    ) {
        viewModelScope.launch {
            repository.logBorewell(
                rawLat = lat,
                rawLng = lng,
                depthFt = depthFt,
                yearDrilled = yearDrilled,
                yieldInchesPerHour = yieldInchesPerHour,
                userId = _userId.value
            )
            _logSuccess.value = true
            refreshContributionCount()
        }
    }

    fun resetLogSuccess() { _logSuccess.value = false }

    fun markAlertRead(id: Long) {
        viewModelScope.launch { repository.markAlertRead(id) }
    }

    fun updateNickname(name: String) {
        viewModelScope.launch {
            _nickname.value = name
            getApplication<Application>().dataStore.edit { it[NICKNAME_KEY] = name }
        }
    }

    private suspend fun refreshContributionCount() {
        _contributionCount.value = repository.countForUser(_userId.value)
    }
}
