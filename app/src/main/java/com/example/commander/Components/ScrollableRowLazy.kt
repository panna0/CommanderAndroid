package com.example.commander.Components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.commander.Models.Match
import com.example.commander.Network.ApiContext
import com.example.commander.Network.ApiService
import com.example.commander.Network.RetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun ScrollableRowsLazy(onShowBottomSheet: (String) -> Unit) {
    val context = LocalContext.current

    var adminMatches by remember { mutableStateOf(listOf<Match>()) }
    var founderMatches by remember { mutableStateOf(listOf<Match>()) }

    val apiContext = remember { ApiContext(context) }
    val scope = rememberCoroutineScope()



    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = apiContext.getMatches()
                if (response.isSuccessful) {
                    response.body()?.let { matchesResponse ->
                        adminMatches = matchesResponse.admin_games
                        founderMatches = matchesResponse.founder_games
                    }
                } else {
                    // log o gestisci errore HTTP
                    println("Errore API: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(founderMatches) { match ->
            MyRow(match) {
                onShowBottomSheet(match.id)
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}
