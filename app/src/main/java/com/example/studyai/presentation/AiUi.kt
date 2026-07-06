package com.example.studyai.presentation

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.core.splashscreen.SplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studyai.R
import com.example.studyai.ui.theme.PrimaryPurple
import com.example.studyai.ui.theme.SurfaceDark
import com.example.studyai.ui.theme.TextPrimary
import com.example.studyai.ui.theme.TextSecondary
import kotlin.math.absoluteValue

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AiUi(vm : StudyViewModel = hiltViewModel()) {

    val state by vm.uiState.collectAsStateWithLifecycle()
    val currentSummary = remember(state.summary){ state.summary.replace("**", "").replace("*", "").replace("#" , "") }
    val context = LocalContext.current

    val pdfPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {uri->
        uri?.let {
            vm.onAction(StudyActions.LoadPdf(context , it))
        }
    }

    val cameraPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicturePreview()) {bitmap ->
        if (bitmap!=null){
           vm.onAction(actions = StudyActions.LoadOcrText(bitmap))
        }
    }


    Column(Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(18.dp)
        .verticalScroll(rememberScrollState())
        ) {

        Column {
            Text(
                text = "StudyAI",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold ,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Summarize notes instantly with AI",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }

        Spacer(Modifier.height(6.dp))

        OutlinedTextField(
            value = state.inputText,
            onValueChange = { vm.onAction(StudyActions.UpdateInput(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(
                    min = 40.dp,
                    max = 100.dp
                ), shape = RoundedCornerShape(28.dp),
            maxLines = 8,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPurple,
                unfocusedBorderColor = TextSecondary,
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            placeholder = {
                Text(
                     text =  if (state.pdfLoaded || state.ocrLoaded) "Add instructions..." else "Paste the text here..."   ,
                    color = TextSecondary
                )
            }
        )

        if (state.pdfLoaded){
            Spacer(Modifier.height(8.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.ai2) ,
                        contentDescription = null ,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "PDF Ready",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Ready for AI summarization",
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    IconButton(onClick = {vm.onAction(StudyActions.ClearPdf)}) {
                        Icon(
                            imageVector = Icons.Default.Delete ,
                            contentDescription = "Delete PDF"
                        )
                    }
                }
            }
        }

        if (state.ocrLoaded){
            Spacer(Modifier.height(5.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.scan) ,
                        contentDescription = null ,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Notes Ready",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Ready for AI summarization",
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    IconButton(onClick = {vm.onAction(StudyActions.ClearOcr)}) {
                        Icon(
                            imageVector = Icons.Default.Delete ,
                            contentDescription = "Delete Scanned File"
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Column(Modifier.fillMaxWidth() , horizontalAlignment = Alignment.CenterHorizontally) {
            Row{
                OutlinedButton(
                    onClick = { vm.onAction(StudyActions.GenerateSummary) },
                    enabled = (state.inputText.isNotBlank() || state.pdfText.isNotBlank() || state.ocrText.isNotBlank()) && !state.isLoading,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Image(
                        painter = painterResource(R.drawable.generative) ,
                        contentDescription = null ,
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Generate")
                }

                Spacer(Modifier.width(6.dp))

                Button(
                    onClick = {vm.onAction(StudyActions.GenerateFlashCard)} ,
                    enabled = (state.inputText.isNotBlank() || state.pdfText.isNotBlank() || state.ocrText.isNotBlank()) && !state.isLoading ,
                    modifier= Modifier.weight(1f)
                    ) {
                    Image(
                        painter = painterResource(R.drawable.flash) ,
                        contentDescription = null ,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Flashcards")
                }
            }

            Row{
                OutlinedButton(
                    onClick = {
                        pdfPicker.launch("application/pdf")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ai2) ,
                        contentDescription = null ,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Upload PDF")
                }

                Spacer(Modifier.width(6.dp))

                OutlinedButton(
                    onClick = {
                        cameraPicker.launch()
                    } ,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(R.drawable.scan) ,
                        contentDescription = null ,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Scan Notes")
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Box(Modifier.fillMaxWidth() , contentAlignment = Alignment.Center){
            if (state.isLoading){
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PrimaryPurple)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "🤖 AI is generating your study notes..." ,
                        color = TextSecondary ,
                        fontSize = 14.sp
                    )
                }
            }
        }

        state.error?.let {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Text(
                    text =  it ,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        if (state.summary.isNotBlank()){
            Text(
                text = "\uD83D\uDCDA Study Summary",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(8.dp))
            ElevatedCard(Modifier
                .fillMaxWidth(), shape = RoundedCornerShape(28.dp) , elevation = CardDefaults.outlinedCardElevation(8.dp)) {
                Column(Modifier
                    .padding(16.dp)){
                    Text(
                        text = currentSummary,
                        fontSize = 15.sp,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        if (state.flashcards.isNotEmpty()){
             Text(
                 text = "🧠 Flashcards" ,
                 fontWeight = FontWeight.Bold ,
                 fontSize = 18.sp
             )

            Spacer(Modifier.height(8.dp))

            Flashcards(vm)
        }
    }
}



@SuppressLint("FrequentlyChangingValue")
@Composable
fun Flashcards(vm : StudyViewModel = hiltViewModel()){
    val state by vm.uiState.collectAsStateWithLifecycle()
    val flashCard = state.flashcards

    val pageState = rememberPagerState(
        pageCount = {flashCard.size}
    )

    HorizontalPager(
        state = pageState,
        pageSpacing = (-30).dp,
        contentPadding = PaddingValues(horizontal = 10.dp),
        modifier = Modifier.fillMaxWidth()
    ) { page ->

        val pageOffset = ((page - pageState.currentPage) + pageState.currentPageOffsetFraction).absoluteValue

        var visible by remember(page) {
            mutableStateOf(false)
        }

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .height(500.dp)
                .clickable { visible = !visible }
                .graphicsLayer {

                    scaleX = lerp(
                        start = 0.85f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                    scaleY = lerp(
                        start = 0.85f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )

                    translationX = pageOffset * 1000f
                },
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Flashcard ${page + 1}/${flashCard.size}",
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.weight(1f))
                Crossfade(
                    targetState = visible,
                    label = ""
                ) { isVisible ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally ,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (!isVisible) flashCard[page].questions else flashCard[page].answer,
                            fontSize = 26.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = if (!isVisible) "Tap to reveal" else "Tap to hide",
                            color = TextSecondary.copy(alpha = 0.8f)
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = "← Swipe for next card",
                    color = TextSecondary.copy(alpha = 0.8f) ,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

