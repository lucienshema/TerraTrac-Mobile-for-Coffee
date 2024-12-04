package org.technoserve.cafetrac.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


/**
 * This function is used to create a skeleton for the site card layout.
 * It includes a shimmering card with placeholder content.
 * The placeholder content includes a checkbox, a site name, and a brief description.
 * The checkbox is a shimmering version of a checkbox, and the site name and description are placeholders.
 * The card's background color is determined by the system theme.
 *
 * Note: This function assumes that the necessary UI components, such as Checkbox, Text, and Image, are already defined and imported.
 * You may need to update the function to use the actual components provided by your project.
 *
 * @param isDarkTheme Whether the system theme is dark or light
 * @param backgroundColor The background color for the card
 * @param placeholderColor The color for the placeholder content (checkbox, site name, and description)
 * @return A skeleton for the site card layout with shimmering card and placeholder content
 */


@Composable
fun FarmCardSkeleton(
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White

    ElevatedCard(
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .background(backgroundColor)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skeleton checkbox
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(4.dp)
                        )
                )

                // Skeleton farm info (Left Side)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    // Skeleton for farm name
                    Box(
                        modifier = Modifier
                            .height(20.dp)
                            .fillMaxWidth(0.5f)
                            .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                            .padding(bottom = 4.dp)
                    )

                    // Skeleton for size
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth(0.4f)
                            .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Skeleton for village
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth(0.6f)
                            .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Skeleton for district
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth(0.4f)
                            .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                    )
                }

                // Skeleton for action icons (Right Side)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    // Skeleton edit icon
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.LightGray, shape = CircleShape)
                            .padding(end = 2.dp)
                    )

                    // Skeleton delete icon
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.LightGray, shape = CircleShape)
                            .padding(end = 2.dp)
                    )
                }
            }

            // Skeleton for needs update label
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.3f)
                    .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                    .padding(top = 4.dp)
            )
        }
    }
}