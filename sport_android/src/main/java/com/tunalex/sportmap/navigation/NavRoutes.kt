package com.tunalex.sportmap.navigation

object NavRoutes {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val DASHBOARD = "dashboard"
    const val MAP = "map"
    const val PLACE_DETAIL = "place_detail"
    const val PLACE_DETAIL_ARG = "placeId"
    const val PLACE_DETAIL_ROUTE = "$PLACE_DETAIL/{$PLACE_DETAIL_ARG}"
    fun placeDetail(id: Long) = "$PLACE_DETAIL/$id"

    const val STORE = "store"
    const val PRODUCT_DETAIL = "product_detail"
    const val PRODUCT_DETAIL_ARG = "productId"
    const val PRODUCT_DETAIL_ROUTE = "$PRODUCT_DETAIL/{$PRODUCT_DETAIL_ARG}"
    fun productDetail(id: Long) = "$PRODUCT_DETAIL/$id"

    const val CART = "cart"
    const val SETTINGS = "settings"
    const val MEDALS = "medals"
    const val PREMIUM = "premium"
    const val EDIT_PROFILE = "edit_profile"
}
