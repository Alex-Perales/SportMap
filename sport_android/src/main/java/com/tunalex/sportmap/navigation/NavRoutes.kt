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
    const val HELP_FAQ = "help_faq"
    const val ABOUT = "about"
    const val RESERVATION_HISTORY = "reservation_history"

    const val ROUTE = "route"
    const val ROUTE_PLACE_ARG = "placeId"
    const val ROUTE_ROUTE = "$ROUTE/{$ROUTE_PLACE_ARG}"
    fun route(placeId: Long) = "$ROUTE/$placeId"
}
