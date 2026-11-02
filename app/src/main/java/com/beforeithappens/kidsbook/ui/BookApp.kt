package com.beforeithappens.kidsbook.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.beforeithappens.kidsbook.BookViewModel
import com.beforeithappens.kidsbook.data.UserData
import com.beforeithappens.kidsbook.ui.screens.ActivitiesScreen
import com.beforeithappens.kidsbook.ui.screens.CalmPlanScreen
import com.beforeithappens.kidsbook.ui.screens.CrewScreen
import com.beforeithappens.kidsbook.ui.screens.GoodOrBossyScreen
import com.beforeithappens.kidsbook.ui.screens.GrownUpsScreen
import com.beforeithappens.kidsbook.ui.screens.HomeScreen
import com.beforeithappens.kidsbook.ui.screens.HowToUseScreen
import com.beforeithappens.kidsbook.ui.screens.KnowByHeartScreen
import com.beforeithappens.kidsbook.ui.screens.MagicWordsScreen
import com.beforeithappens.kidsbook.ui.screens.OnboardingScreen
import com.beforeithappens.kidsbook.ui.screens.PartScreen
import com.beforeithappens.kidsbook.ui.screens.PersonalBestsScreen
import com.beforeithappens.kidsbook.ui.screens.SafetyHandScreen
import com.beforeithappens.kidsbook.ui.screens.StoryScreen
import com.beforeithappens.kidsbook.ui.screens.KeepOrTellScreen
import com.beforeithappens.kidsbook.ui.screens.ExitLinesScreen
import com.beforeithappens.kidsbook.ui.screens.UsefulThingScreen
import com.beforeithappens.kidsbook.ui.screens.TechRulesScreen
import com.beforeithappens.kidsbook.ui.screens.WhatIfScreen
import com.beforeithappens.kidsbook.ui.screens.WhoCanHelpScreen

object Routes {
    const val HOME = "home"
    const val ONBOARDING = "onboarding"
    const val PART = "part/{id}"
    const val STORY = "story/{id}"
    const val MAGIC = "magic"
    const val ACTIVITIES = "activities"
    const val WHAT_IF = "whatif"
    const val KEEP_TELL = "keeptell"
    const val EXIT_LINES = "exitlines"
    const val USEFUL = "useful"
    const val GOOD_BOSSY = "goodbossy"
    const val HAND = "hand"
    const val HEART = "heart"
    const val BESTS = "bests"
    const val CALM = "calm"
    const val TECH = "tech"
    const val HELP = "help"
    const val GROWN_UPS = "grownups"
    const val CREW = "crew"
    const val HOW_TO = "howto"
}

/** Navigation helpers handed to every screen. */
class Nav(private val controller: NavHostController) {
    fun go(route: String) = controller.navigate(route) { launchSingleTop = true }
    fun back() { if (!controller.popBackStack()) controller.navigate(Routes.HOME) }
    fun home() { controller.popBackStack(Routes.HOME, inclusive = false) }
    fun help() = go(Routes.HELP)
    fun story(id: Int) = go("story/$id")
    fun part(id: Int) = go("part/$id")
    fun finishOnboarding() = controller.navigate(Routes.HOME) {
        popUpTo(Routes.ONBOARDING) { inclusive = true }
    }
    fun restart() = controller.navigate(Routes.ONBOARDING) {
        popUpTo(0) { inclusive = true }
    }
}

@Composable
fun BookApp(vm: BookViewModel) {
    val user by vm.user.collectAsStateWithLifecycle()
    val current = user
    if (current == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val controller = rememberNavController()
    val nav = remember(controller) { Nav(controller) }
    val start = remember { if (current.onboarded) Routes.HOME else Routes.ONBOARDING }

    BookNavHost(controller, nav, vm, current, start)
}

@Composable
private fun BookNavHost(
    controller: NavHostController,
    nav: Nav,
    vm: BookViewModel,
    user: UserData,
    start: String,
) {
    val content = vm.content
    NavHost(navController = controller, startDestination = start) {
        composable(Routes.ONBOARDING) { OnboardingScreen(content, user, vm::update, nav) }
        composable(Routes.HOME) { HomeScreen(content, user, nav) }
        composable(Routes.PART, arguments = listOf(navArgument("id") { type = NavType.IntType })) {
            PartScreen(content, user, it.arguments?.getInt("id") ?: 1, nav)
        }
        composable(Routes.STORY, arguments = listOf(navArgument("id") { type = NavType.IntType })) {
            StoryScreen(content, user, it.arguments?.getInt("id") ?: 1, vm::update, nav)
        }
        composable(Routes.MAGIC) { MagicWordsScreen(content, user, nav) }
        composable(Routes.ACTIVITIES) { ActivitiesScreen(nav) }
        composable(Routes.WHAT_IF) { WhatIfScreen(content, nav) }
        composable(Routes.KEEP_TELL) { KeepOrTellScreen(content, nav) }
        composable(Routes.EXIT_LINES) { ExitLinesScreen(content, user, vm::update, nav) }
        composable(Routes.USEFUL) { UsefulThingScreen(content, nav) }
        composable(Routes.GOOD_BOSSY) { GoodOrBossyScreen(content, nav) }
        composable(Routes.HAND) { SafetyHandScreen(content, user, vm::update, nav) }
        composable(Routes.HEART) { KnowByHeartScreen(content, user, vm, nav) }
        composable(Routes.BESTS) { PersonalBestsScreen(content, user, vm::update, nav) }
        composable(Routes.CALM) { CalmPlanScreen(content, user, vm::update, nav) }
        composable(Routes.TECH) { TechRulesScreen(content, user, vm::update, nav) }
        composable(Routes.HELP) { WhoCanHelpScreen(content, user, nav) }
        composable(Routes.GROWN_UPS) { GrownUpsScreen(content, user, vm, nav) }
        composable(Routes.CREW) { CrewScreen(content, nav) }
        composable(Routes.HOW_TO) { HowToUseScreen(content, nav) }
    }
}
