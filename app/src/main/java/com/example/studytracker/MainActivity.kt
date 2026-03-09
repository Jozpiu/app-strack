package com.example.studytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studytracker.data.AppDatabase
import com.example.studytracker.data.StudyRepository
import com.example.studytracker.ui.HomeScreen
import com.example.studytracker.ui.HomeViewModel
import com.example.studytracker.ui.theme.StudyTrackerTheme

class MainActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = StudyRepository(
                    AppDatabase.getInstance(applicationContext).studySessionDao()
                )
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyTrackerTheme {
                Surface {
                    HomeScreen(
                        state = viewModel.uiState,
                        onHoursSubmit = viewModel::addStudyHours
                    )
                }
            }
        }
    }
}
