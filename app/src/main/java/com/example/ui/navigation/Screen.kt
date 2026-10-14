package com.example.ui.navigation

sealed class Screen(val route: String, val labelEn: String, val labelTe: String) {
    object Home : Screen("home", "Home", "హోమ్")
    object ServiceFinder : Screen("find_service", "Find a Service", "సేవను వెతకండి")
    object DocumentChecklist : Screen("document_checklist", "Document Checklist", "పత్రాల జాబితా")
    object ApplicationGuidance : Screen("application_guidance", "Guidance & Demo", "దరఖాస్తు మార్గదర్శి")
    object MyRequests : Screen("my_requests", "My Requests", "నా అభ్యర్థనలు")
    object AdminDashboard : Screen("admin_dashboard", "Admin Dashboard", "అడ్మిన్ డాష్‌బోర్డ్")
    object TestBench : Screen("test_bench", "Testing & Metrics", "పరీక్షలు & ప్రమాణాలు")
}
