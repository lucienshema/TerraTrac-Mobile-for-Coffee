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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer


@Composable
fun SkeletonBoughtItemCardBuyThroughAkrabi() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .shimmer(), // Apply shimmer effect
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Skeleton for akrabiName
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(20.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Skeleton for siteName
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(20.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Skeleton for cherrySold
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(20.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Skeleton for totalPaid
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(20.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
            }

            Row(
                modifier = Modifier.padding(end = 0.dp)
            ) {
                // Skeleton for edit icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))

                // Skeleton for delete icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
            }
        }
    }
}