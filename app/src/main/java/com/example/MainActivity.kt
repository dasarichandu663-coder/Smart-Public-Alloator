package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CivicBottomNav
import com.example.ui.components.CivicTopAppBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = viewModel()
                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                val language by viewModel.language.collectAsStateWithLifecycle()
                val isAdminMode by viewModel.isAdminMode.collectAsStateWithLifecycle()
                val services by viewModel.services.collectAsStateWithLifecycle()
                val tasks by viewModel.tasks.collectAsStateWithLifecycle()
                val staff by viewModel.staff.collectAsStateWithLifecycle()
                val allocationResult by viewModel.allocationResult.collectAsStateWithLifecycle()
                val baselineResult by viewModel.baselineResult.collectAsStateWithLifecycle()
                val appointments by viewModel.appointments.collectAsStateWithLifecycle()
                val readyDocuments by viewModel.readyDocuments.collectAsStateWithLifecycle()
                val reportedIssues by viewModel.reportedIssues.collectAsStateWithLifecycle()
                val benchmarkMetrics by viewModel.benchmarkMetrics.collectAsStateWithLifecycle()
                val selectedServiceId by viewModel.selectedServiceId.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val selectedState by viewModel.selectedState.collectAsStateWithLifecycle()
                val aiSearchState by viewModel.aiSearchState.collectAsStateWithLifecycle()
                val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()

                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(statusMessage) {
                    statusMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearStatusMessage()
                    }
                }

                // Handle system back navigation to return to Home screen
                BackHandler(enabled = currentScreen != Screen.Home) {
                    viewModel.navigateTo(Screen.Home)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        CivicTopAppBar(
                            currentScreen = currentScreen,
                            language = language,
                            isAdminMode = isAdminMode,
                            onToggleLanguage = { viewModel.toggleLanguage() },
                            onToggleAdminMode = { viewModel.setAdminMode(!isAdminMode) },
                            onNavigateToTestBench = { viewModel.navigateTo(Screen.TestBench) }
                        )
                    },
                    bottomBar = {
                        CivicBottomNav(
                            currentScreen = currentScreen,
                            isAdminMode = isAdminMode,
                            language = language,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            Screen.Home -> HomeScreen(
                                language = language,
                                services = services,
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
                                onSearchSubmit = { viewModel.executeAiSearch(it) },
                                onSelectService = { viewModel.selectService(it) },
                                onNavigate = { viewModel.navigateTo(it) }
                            )

                            Screen.ServiceFinder -> ServiceFinderScreen(
                                language = language,
                                services = services,
                                searchQuery = searchQuery,
                                selectedState = selectedState,
                                aiSearchState = aiSearchState,
                                onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
                                onStateChange = { viewModel.onStateFilterChanged(it) },
                                onExecuteSearch = { viewModel.executeAiSearch(it) },
                                onSelectService = { viewModel.selectService(it) },
                                onNavigate = { viewModel.navigateTo(it) }
                            )

                            Screen.DocumentChecklist -> DocumentChecklistScreen(
                                language = language,
                                services = services,
                                selectedServiceId = selectedServiceId,
                                readyDocuments = readyDocuments,
                                onSelectService = { viewModel.selectService(it) },
                                onToggleDocumentReady = { sId, dId -> viewModel.toggleDocumentReady(sId, dId) },
                                onNavigate = { viewModel.navigateTo(it) }
                            )

                            Screen.ApplicationGuidance -> ApplicationGuidanceScreen(
                                language = language,
                                services = services,
                                selectedServiceId = selectedServiceId,
                                onBookDemoAppointment = { name, phone, sName, center, date, slot ->
                                    viewModel.bookDemoAppointment(name, phone, sName, center, date, slot)
                                },
                                onNavigate = { viewModel.navigateTo(it) }
                            )

                            Screen.MyRequests -> MyRequestsScreen(
                                language = language,
                                appointments = appointments,
                                services = services,
                                readyDocuments = readyDocuments,
                                reportedIssues = reportedIssues,
                                onReportIssue = { sName, type, desc, email ->
                                    viewModel.reportIssue(sName, type, desc, email)
                                },
                                onSelectService = { viewModel.selectService(it) },
                                onNavigate = { viewModel.navigateTo(it) }
                            )

                            Screen.AdminDashboard -> AdminDashboardScreen(
                                language = language,
                                tasks = tasks,
                                staff = staff,
                                services = services,
                                allocationResult = allocationResult,
                                baselineResult = baselineResult,
                                onRunClassicalAllocation = { viewModel.runClassicalAllocation() },
                                onRunBaselineAllocation = { viewModel.runBaselineAllocation() },
                                onApproveAllocation = { viewModel.approveAllocation() },
                                onToggleServiceVerification = { viewModel.toggleServiceVerification(it) }
                            )

                            Screen.TestBench -> TestBenchScreen(
                                language = language,
                                benchmarkMetrics = benchmarkMetrics,
                                onRunScenario = { viewModel.runBenchmarkScenario(it) },
                                onUpdateMetric = { num, value, passed, notes ->
                                    viewModel.runBenchmarkScenario(num)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
