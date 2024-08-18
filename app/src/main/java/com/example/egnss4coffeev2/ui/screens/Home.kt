
package com.example.egnss4coffeev2.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.egnss4coffeev2.R
import com.example.egnss4coffeev2.utils.Language
import com.example.egnss4coffeev2.utils.LanguageSelector
import com.example.egnss4coffeev2.utils.LanguageViewModel

@Composable
fun Home(
    navController: NavController,
    languageViewModel: LanguageViewModel,
    languages: List<Language>
) {
    Column(
        Modifier
            .padding(top = 20.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        add language selector here and align on right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            LanguageSelector(viewModel = languageViewModel, languages = languages)
        }
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                null,
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .padding(bottom = 10.dp)
            )
            Text(
                text = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.light_blue_900),
                style = TextStyle(fontSize = 24.sp)
            )
        }
        Box(
            modifier = Modifier
                .padding(30.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable {
                    navController.navigate("shopping")
                }
                .padding(16.dp) // Additional padding to make the clickable area similar to a button
        ) {
            Text(
                text = stringResource(id = R.string.get_started),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary // Ensure text color contrasts with background
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.fillMaxHeight(0.2f))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.app_intro),
                style = TextStyle(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                modifier = Modifier.padding(start = 20.dp, end = 5.dp),
                text = stringResource(id = R.string.developed_by)
            )
            Image(
                painter = painterResource(id = R.drawable.tns_labs),
                null,
                modifier = Modifier
                    .width(130.dp)
                    .height(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}

/* Testing Constraint Layout so that the screen will be responsive on landscape devices*/
//@Composable
//fun Home(
//    navController: NavController,
//    languageViewModel: LanguageViewModel,
//    languages: List<Language>
//) {
//    ConstraintLayout(
//        Modifier
//            .padding(top = 20.dp)
//            .fillMaxSize()
//    ) {
//        val (languageSelector, logo, appName, getStartedButton, appIntro, developedByText, tnsLabsImage) = createRefs()
//
//        // Language Selector
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .constrainAs(languageSelector) {
//                    top.linkTo(parent.top)
//                    end.linkTo(parent.end)
//                },
//            horizontalArrangement = Arrangement.End
//        ) {
//            LanguageSelector(viewModel = languageViewModel, languages = languages)
//        }
//
//        // App Icon and Name
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier
//                .constrainAs(logo) {
//                    top.linkTo(languageSelector.bottom, margin = 30.dp)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                }
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.app_icon),
//                contentDescription = null,
//                modifier = Modifier
//                    .width(80.dp)
//                    .height(80.dp)
//                    .padding(bottom = 10.dp)
//            )
//            Text(
//                text = stringResource(id = R.string.app_name),
//                fontWeight = FontWeight.Bold,
//                color = colorResource(id = R.color.light_blue_900),
//                style = TextStyle(fontSize = 24.sp)
//            )
//        }
//
//        // Get Started Button
//        Box(
//            modifier = Modifier
//                .padding(30.dp)
//                .background(
//                    color = MaterialTheme.colorScheme.primary,
//                    shape = RoundedCornerShape(10.dp)
//                )
//                .clickable {
//                    navController.navigate("siteList")
//                }
//                .padding(16.dp)
//                .constrainAs(getStartedButton) {
//                    top.linkTo(logo.bottom, margin = 30.dp)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                }
//        ) {
//            Text(
//                text = stringResource(id = R.string.get_started),
//                style = TextStyle(
//                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.onPrimary
//                ),
//                modifier = Modifier.align(Alignment.Center)
//            )
//        }
//
//        // App Intro Text
//        Box(
//            modifier = Modifier
//                .fillMaxWidth(0.8f)
//                .padding(20.dp)
//                .constrainAs(appIntro) {
//                    top.linkTo(getStartedButton.bottom, margin = 30.dp)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                }
//        ) {
//            Text(
//                text = stringResource(id = R.string.app_intro),
//                style = TextStyle(
//                    fontWeight = FontWeight.Bold
//                ),
//                textAlign = TextAlign.Center,
//                modifier = Modifier.align(Alignment.Center)
//            )
//        }
//
//        // Developed By Text and Image
//        Row(
//            modifier = Modifier
//                .constrainAs(developedByText) {
//                    bottom.linkTo(parent.bottom, margin = 10.dp)
//                    start.linkTo(parent.start)
//                    end.linkTo(tnsLabsImage.start)
//                }
//                .padding(start = 20.dp, end = 5.dp),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.Bottom
//        ) {
//            Text(
//                text = stringResource(id = R.string.developed_by)
//            )
//        }
//
//        Image(
//            painter = painterResource(id = R.drawable.tns_labs),
//            contentDescription = null,
//            modifier = Modifier
//                .width(130.dp)
//                .height(20.dp)
//                .constrainAs(tnsLabsImage) {
//                    bottom.linkTo(parent.bottom, margin = 10.dp)
//                    end.linkTo(parent.end, margin = 20.dp)
//                }
//        )
//    }
//}




