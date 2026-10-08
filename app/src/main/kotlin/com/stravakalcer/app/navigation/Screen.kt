package com.stravakalcer.app.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object RouteReview : Screen("route_review")
    object Settings : Screen("settings")
    object Preview : Screen("preview")
    object DeviceSelect : Screen("device_select")
    object Export : Screen("export")
    object Debug : Screen("debug")
}
