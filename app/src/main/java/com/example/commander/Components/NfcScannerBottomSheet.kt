package com.example.commander.Components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.commander.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NfcScannerBottomSheet(
    nfcTagId: String?,
    onDismiss: () -> Unit,
    bombCode: String?,
    onDefuseSuccess: () -> Unit,
    bombDefuseTimer: Int?
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val initialTime = bombDefuseTimer ?: 10
    var remainingTime by remember { mutableStateOf(initialTime) }
    var isDefusing by remember { mutableStateOf(false) }

    // Effetto principale che gestisce timer + controllo NFC
    LaunchedEffect(nfcTagId) {
        if (nfcTagId == bombCode && nfcTagId != null) {
            // NFC corretto → avvio defusione
            isDefusing = true
            while (isDefusing && remainingTime > 0) {
                // countdown ogni secondo
                kotlinx.coroutines.delay(1000)
                remainingTime--

                // ogni 2 secondi controlliamo se NFC è ancora valido
                if (remainingTime % 2 == 0) {
                    if (nfcTagId != bombCode || nfcTagId == null) {
                        // NFC perso → reset
                        isDefusing = false
                        remainingTime = initialTime
                    }
                }
            }

            // Defusione completata
            if (remainingTime == 0 && isDefusing) {
                onDefuseSuccess()
                onDismiss()
            }
        } else {
            // NFC non corretto → reset
            isDefusing = false
            remainingTime = initialTime
        }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Bomb Defuser", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))

            if (isDefusing) {
                Text("Move your phone over the NFC to defuse the bomb!")
                Spacer(Modifier.height(8.dp))
                Text("Timer: $remainingTime s", style = MaterialTheme.typography.bodyLarge)
            } else {
                Text("Bring your phone closer to the bomb...", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(Modifier.height(24.dp))

            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.nfc_scan_animation)
            )
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(300.dp)
            )
        }
    }
}
