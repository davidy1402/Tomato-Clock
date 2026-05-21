package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.FocusRepository
import com.example.ui.pomo.PomodoroScreen
import com.example.ui.pomo.PomodoroViewModel
import com.example.ui.pomo.PomodoroViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Enable edge-to-edge drawing for real immersive color/gradient overlays
    enableEdgeToEdge()

    // Initialize Room Database & repository locally
    val database = AppDatabase.getDatabase(this)
    val dao = database.focusSessionDao()
    val repository = FocusRepository(dao)

    setContent {
      MyApplicationTheme {
        // Instantiate our State Controller using the customized Factory
        val pomodoroViewModel: PomodoroViewModel = viewModel(
          factory = PomodoroViewModelFactory(application, repository)
        )

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          // The main screen takes full screen dimensions
          PomodoroScreen(
            viewModel = pomodoroViewModel,
            modifier = Modifier.fillMaxSize()
          )
        }
      }
    }
  }
}
