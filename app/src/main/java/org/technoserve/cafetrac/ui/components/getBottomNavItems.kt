package org.technoserve.cafetrac.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.cafetrac.database.models.BottomNavItemWithLabel
import org.technoserve.cafetraorg.technoserve.cafetrac.R

@Composable
fun getBottomNavItems(): List<BottomNavItemWithLabel> {
    return listOf(
        BottomNavItemWithLabel(
            route = BottomNavItem.DirectBuy.route,
            label = stringResource(id = R.string.direct_buy),
            icon = BottomNavItem.DirectBuy.icon
        ),
        BottomNavItemWithLabel(
            route = BottomNavItem.BuyThroughAkrabi.route,
            label = stringResource(id = R.string.buy_through_akrabi),
            icon = BottomNavItem.BuyThroughAkrabi.icon
        )
    )
}
