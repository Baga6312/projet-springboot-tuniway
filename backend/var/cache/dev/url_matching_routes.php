<?php

/**
 * This file has been auto-generated
 * by the Symfony Routing Component.
 */

return [
    false, // $matchHost
    [ // $staticRoutes
        '/_wdt/styles' => [[['_route' => '_wdt_stylesheet', '_controller' => 'web_profiler.controller.profiler::toolbarStylesheetAction'], null, null, null, false, false, null]],
        '/_profiler' => [[['_route' => '_profiler_home', '_controller' => 'web_profiler.controller.profiler::homeAction'], null, null, null, true, false, null]],
        '/_profiler/search' => [[['_route' => '_profiler_search', '_controller' => 'web_profiler.controller.profiler::searchAction'], null, null, null, false, false, null]],
        '/_profiler/search_bar' => [[['_route' => '_profiler_search_bar', '_controller' => 'web_profiler.controller.profiler::searchBarAction'], null, null, null, false, false, null]],
        '/_profiler/phpinfo' => [[['_route' => '_profiler_phpinfo', '_controller' => 'web_profiler.controller.profiler::phpinfoAction'], null, null, null, false, false, null]],
        '/_profiler/xdebug' => [[['_route' => '_profiler_xdebug', '_controller' => 'web_profiler.controller.profiler::xdebugAction'], null, null, null, false, false, null]],
        '/_profiler/open' => [[['_route' => '_profiler_open_file', '_controller' => 'web_profiler.controller.profiler::openAction'], null, null, null, false, false, null]],
        '/api/favorites' => [
            [['_route' => 'api_favorites_list', '_controller' => 'App\\Controller\\Api\\FavoriController::index'], null, ['GET' => 0], null, false, false, null],
            [['_route' => 'api_favorites_create', '_controller' => 'App\\Controller\\Api\\FavoriController::create'], null, ['POST' => 0], null, false, false, null],
        ],
        '/api/places' => [
            [['_route' => 'api_places_list', '_controller' => 'App\\Controller\\Api\\PlaceController::index'], null, ['GET' => 0], null, false, false, null],
            [['_route' => 'api_places_create', '_controller' => 'App\\Controller\\Api\\PlaceController::create'], null, ['POST' => 0], null, false, false, null],
        ],
        '/api/reservations' => [
            [['_route' => 'api_reservations_list', '_controller' => 'App\\Controller\\Api\\ReservationController::index'], null, ['GET' => 0], null, false, false, null],
            [['_route' => 'api_reservations_create', '_controller' => 'App\\Controller\\Api\\ReservationController::create'], null, ['POST' => 0], null, false, false, null],
        ],
        '/api/reviews' => [
            [['_route' => 'api_reviews_list', '_controller' => 'App\\Controller\\Api\\ReviewController::index'], null, ['GET' => 0], null, false, false, null],
            [['_route' => 'api_reviews_create', '_controller' => 'App\\Controller\\Api\\ReviewController::create'], null, ['POST' => 0], null, false, false, null],
        ],
        '/api/tours' => [
            [['_route' => 'api_tours_list', '_controller' => 'App\\Controller\\Api\\TourPersonnaliseController::index'], null, ['GET' => 0], null, false, false, null],
            [['_route' => 'api_tours_create', '_controller' => 'App\\Controller\\Api\\TourPersonnaliseController::create'], null, ['POST' => 0], null, false, false, null],
        ],
        '/api/users' => [
            [['_route' => 'api_users_list', '_controller' => 'App\\Controller\\Api\\UserController::index'], null, ['GET' => 0], null, false, false, null],
            [['_route' => 'api_users_create', '_controller' => 'App\\Controller\\Api\\UserController::create'], null, ['POST' => 0], null, false, false, null],
        ],
        '/favori' => [[['_route' => 'app_favori_index', '_controller' => 'App\\Controller\\FavoriController::index'], null, ['GET' => 0], null, false, false, null]],
        '/favori/new' => [[['_route' => 'app_favori_new', '_controller' => 'App\\Controller\\FavoriController::new'], null, ['POST' => 0], null, false, false, null]],
        '/' => [[['_route' => 'app_home', '_controller' => 'App\\Controller\\HomeController::index'], null, null, null, false, false, null]],
        '/api/login' => [[['_route' => 'app_login', '_controller' => 'App\\Controller\\LoginController::login'], null, ['POST' => 0], null, false, false, null]],
        '/logout' => [[['_route' => 'app_logout', '_controller' => 'App\\Controller\\LoginController::logout'], null, null, null, false, false, null]],
        '/place' => [[['_route' => 'app_place_index', '_controller' => 'App\\Controller\\PlaceController::index'], null, ['GET' => 0], null, false, false, null]],
        '/place/new' => [[['_route' => 'app_place_new', '_controller' => 'App\\Controller\\PlaceController::new'], null, ['POST' => 0], null, false, false, null]],
        '/api/register' => [[['_route' => 'app_register', '_controller' => 'App\\Controller\\RegistrationController::register'], null, ['POST' => 0], null, false, false, null]],
        '/reservation' => [[['_route' => 'app_reservation_index', '_controller' => 'App\\Controller\\ReservationController::index'], null, ['GET' => 0], null, false, false, null]],
        '/reservation/new' => [[['_route' => 'app_reservation_new', '_controller' => 'App\\Controller\\ReservationController::new'], null, ['POST' => 0], null, false, false, null]],
        '/review' => [[['_route' => 'app_review_index', '_controller' => 'App\\Controller\\ReviewController::index'], null, ['GET' => 0], null, false, false, null]],
        '/review/new' => [[['_route' => 'app_review_new', '_controller' => 'App\\Controller\\ReviewController::new'], null, ['POST' => 0], null, false, false, null]],
        '/tour/personnalise' => [[['_route' => 'app_tour_personnalise_index', '_controller' => 'App\\Controller\\TourPersonnaliseController::index'], null, ['GET' => 0], null, false, false, null]],
        '/tour/personnalise/new' => [[['_route' => 'app_tour_personnalise_new', '_controller' => 'App\\Controller\\TourPersonnaliseController::new'], null, ['POST' => 0], null, false, false, null]],
    ],
    [ // $regexpList
        0 => '{^(?'
                .'|/_(?'
                    .'|error/(\\d+)(?:\\.([^/]++))?(*:38)'
                    .'|wdt/([^/]++)(*:57)'
                    .'|profiler/(?'
                        .'|font/([^/\\.]++)\\.woff2(*:98)'
                        .'|([^/]++)(?'
                            .'|/(?'
                                .'|search/results(*:134)'
                                .'|router(*:148)'
                                .'|exception(?'
                                    .'|(*:168)'
                                    .'|\\.css(*:181)'
                                .')'
                            .')'
                            .'|(*:191)'
                        .')'
                    .')'
                .')'
                .'|/api/(?'
                    .'|favorites/(?'
                        .'|([^/]++)(?'
                            .'|(*:234)'
                        .')'
                        .'|user/([^/]++)(*:256)'
                    .')'
                    .'|places/(?'
                        .'|([^/]++)(?'
                            .'|(*:286)'
                        .')'
                        .'|search(*:301)'
                        .'|category/([^/]++)(*:326)'
                        .'|nearby(*:340)'
                        .'|([^/]++)/reviews(*:364)'
                    .')'
                    .'|re(?'
                        .'|servations/(?'
                            .'|([^/]++)(?'
                                .'|(*:403)'
                            .')'
                            .'|user/([^/]++)(*:425)'
                            .'|status/([^/]++)(*:448)'
                        .')'
                        .'|views/(?'
                            .'|([^/]++)(?'
                                .'|(*:477)'
                            .')'
                            .'|place/([^/]++)(*:500)'
                            .'|user/([^/]++)(*:521)'
                        .')'
                    .')'
                    .'|tours/(?'
                        .'|([^/]++)(?'
                            .'|(*:551)'
                        .')'
                        .'|guide/([^/]++)(*:574)'
                        .'|client/([^/]++)(*:597)'
                    .')'
                    .'|users/([^/]++)(?'
                        .'|(*:623)'
                        .'|/re(?'
                            .'|views(*:642)'
                            .'|servations(*:660)'
                        .')'
                    .')'
                .')'
                .'|/favori/([^/]++)(?'
                    .'|(*:690)'
                    .'|/edit(*:703)'
                    .'|(*:711)'
                .')'
                .'|/place/([^/]++)(?'
                    .'|(*:738)'
                    .'|/edit(*:751)'
                    .'|(*:759)'
                .')'
                .'|/re(?'
                    .'|servation/([^/]++)(?'
                        .'|(*:795)'
                        .'|/edit(*:808)'
                        .'|(*:816)'
                    .')'
                    .'|view/([^/]++)(?'
                        .'|(*:841)'
                        .'|/edit(*:854)'
                        .'|(*:862)'
                    .')'
                .')'
                .'|/tour/personnalise/([^/]++)(?'
                    .'|(*:902)'
                    .'|/edit(*:915)'
                    .'|(*:923)'
                .')'
            .')/?$}sDu',
    ],
    [ // $dynamicRoutes
        38 => [[['_route' => '_preview_error', '_controller' => 'error_controller::preview', '_format' => 'html'], ['code', '_format'], null, null, false, true, null]],
        57 => [[['_route' => '_wdt', '_controller' => 'web_profiler.controller.profiler::toolbarAction'], ['token'], null, null, false, true, null]],
        98 => [[['_route' => '_profiler_font', '_controller' => 'web_profiler.controller.profiler::fontAction'], ['fontName'], null, null, false, false, null]],
        134 => [[['_route' => '_profiler_search_results', '_controller' => 'web_profiler.controller.profiler::searchResultsAction'], ['token'], null, null, false, false, null]],
        148 => [[['_route' => '_profiler_router', '_controller' => 'web_profiler.controller.router::panelAction'], ['token'], null, null, false, false, null]],
        168 => [[['_route' => '_profiler_exception', '_controller' => 'web_profiler.controller.exception_panel::body'], ['token'], null, null, false, false, null]],
        181 => [[['_route' => '_profiler_exception_css', '_controller' => 'web_profiler.controller.exception_panel::stylesheet'], ['token'], null, null, false, false, null]],
        191 => [[['_route' => '_profiler', '_controller' => 'web_profiler.controller.profiler::panelAction'], ['token'], null, null, false, true, null]],
        234 => [
            [['_route' => 'api_favorites_show', '_controller' => 'App\\Controller\\Api\\FavoriController::show'], ['id'], ['GET' => 0], null, false, true, null],
            [['_route' => 'api_favorites_delete', '_controller' => 'App\\Controller\\Api\\FavoriController::delete'], ['id'], ['DELETE' => 0], null, false, true, null],
        ],
        256 => [[['_route' => 'api_favorites_by_user', '_controller' => 'App\\Controller\\Api\\FavoriController::getByUser'], ['userId'], ['GET' => 0], null, false, true, null]],
        286 => [
            [['_route' => 'api_places_show', '_controller' => 'App\\Controller\\Api\\PlaceController::show'], ['id'], ['GET' => 0], null, false, true, null],
            [['_route' => 'api_places_update', '_controller' => 'App\\Controller\\Api\\PlaceController::update'], ['id'], ['PUT' => 0], null, false, true, null],
            [['_route' => 'api_places_delete', '_controller' => 'App\\Controller\\Api\\PlaceController::delete'], ['id'], ['DELETE' => 0], null, false, true, null],
        ],
        301 => [[['_route' => 'api_places_search', '_controller' => 'App\\Controller\\Api\\PlaceController::search'], [], ['GET' => 0], null, false, false, null]],
        326 => [[['_route' => 'api_places_category', '_controller' => 'App\\Controller\\Api\\PlaceController::findByCategory'], ['category'], ['GET' => 0], null, false, true, null]],
        340 => [[['_route' => 'api_places_nearby', '_controller' => 'App\\Controller\\Api\\PlaceController::findNearby'], [], ['GET' => 0], null, false, false, null]],
        364 => [[['_route' => 'api_places_reviews', '_controller' => 'App\\Controller\\Api\\PlaceController::getPlaceReviews'], ['id'], ['GET' => 0], null, false, false, null]],
        403 => [
            [['_route' => 'api_reservations_show', '_controller' => 'App\\Controller\\Api\\ReservationController::show'], ['id'], ['GET' => 0], null, false, true, null],
            [['_route' => 'api_reservations_update', '_controller' => 'App\\Controller\\Api\\ReservationController::update'], ['id'], ['PUT' => 0], null, false, true, null],
            [['_route' => 'api_reservations_delete', '_controller' => 'App\\Controller\\Api\\ReservationController::delete'], ['id'], ['DELETE' => 0], null, false, true, null],
        ],
        425 => [[['_route' => 'api_reservations_by_user', '_controller' => 'App\\Controller\\Api\\ReservationController::getByUser'], ['userId'], ['GET' => 0], null, false, true, null]],
        448 => [[['_route' => 'api_reservations_by_status', '_controller' => 'App\\Controller\\Api\\ReservationController::getByStatus'], ['status'], ['GET' => 0], null, false, true, null]],
        477 => [
            [['_route' => 'api_reviews_show', '_controller' => 'App\\Controller\\Api\\ReviewController::show'], ['id'], ['GET' => 0], null, false, true, null],
            [['_route' => 'api_reviews_update', '_controller' => 'App\\Controller\\Api\\ReviewController::update'], ['id'], ['PUT' => 0], null, false, true, null],
            [['_route' => 'api_reviews_delete', '_controller' => 'App\\Controller\\Api\\ReviewController::delete'], ['id'], ['DELETE' => 0], null, false, true, null],
        ],
        500 => [[['_route' => 'api_reviews_by_place', '_controller' => 'App\\Controller\\Api\\ReviewController::getByPlace'], ['placeId'], ['GET' => 0], null, false, true, null]],
        521 => [[['_route' => 'api_reviews_by_user', '_controller' => 'App\\Controller\\Api\\ReviewController::getByUser'], ['userId'], ['GET' => 0], null, false, true, null]],
        551 => [
            [['_route' => 'api_tours_show', '_controller' => 'App\\Controller\\Api\\TourPersonnaliseController::show'], ['id'], ['GET' => 0], null, false, true, null],
            [['_route' => 'api_tours_update', '_controller' => 'App\\Controller\\Api\\TourPersonnaliseController::update'], ['id'], ['PUT' => 0], null, false, true, null],
            [['_route' => 'api_tours_delete', '_controller' => 'App\\Controller\\Api\\TourPersonnaliseController::delete'], ['id'], ['DELETE' => 0], null, false, true, null],
        ],
        574 => [[['_route' => 'api_tours_by_guide', '_controller' => 'App\\Controller\\Api\\TourPersonnaliseController::getByGuide'], ['guideId'], ['GET' => 0], null, false, true, null]],
        597 => [[['_route' => 'api_tours_by_client', '_controller' => 'App\\Controller\\Api\\TourPersonnaliseController::getByClient'], ['clientId'], ['GET' => 0], null, false, true, null]],
        623 => [
            [['_route' => 'api_users_show', '_controller' => 'App\\Controller\\Api\\UserController::show'], ['id'], ['GET' => 0], null, false, true, null],
            [['_route' => 'api_users_update', '_controller' => 'App\\Controller\\Api\\UserController::update'], ['id'], ['PUT' => 0], null, false, true, null],
            [['_route' => 'api_users_delete', '_controller' => 'App\\Controller\\Api\\UserController::delete'], ['id'], ['DELETE' => 0], null, false, true, null],
        ],
        642 => [[['_route' => 'api_users_reviews', '_controller' => 'App\\Controller\\Api\\UserController::getUserReviews'], ['id'], ['GET' => 0], null, false, false, null]],
        660 => [[['_route' => 'api_users_reservations', '_controller' => 'App\\Controller\\Api\\UserController::getUserReservations'], ['id'], ['GET' => 0], null, false, false, null]],
        690 => [[['_route' => 'app_favori_show', '_controller' => 'App\\Controller\\FavoriController::show'], ['id'], ['GET' => 0], null, false, true, null]],
        703 => [[['_route' => 'app_favori_edit', '_controller' => 'App\\Controller\\FavoriController::edit'], ['id'], ['PUT' => 0], null, false, false, null]],
        711 => [[['_route' => 'app_favori_delete', '_controller' => 'App\\Controller\\FavoriController::delete'], ['id'], ['DELETE' => 0], null, false, true, null]],
        738 => [[['_route' => 'app_place_show', '_controller' => 'App\\Controller\\PlaceController::show'], ['id'], ['GET' => 0], null, false, true, null]],
        751 => [[['_route' => 'app_place_edit', '_controller' => 'App\\Controller\\PlaceController::edit'], ['id'], ['PUT' => 0], null, false, false, null]],
        759 => [[['_route' => 'app_place_delete', '_controller' => 'App\\Controller\\PlaceController::delete'], ['id'], ['DELETE' => 0], null, false, true, null]],
        795 => [[['_route' => 'app_reservation_show', '_controller' => 'App\\Controller\\ReservationController::show'], ['id'], ['GET' => 0], null, false, true, null]],
        808 => [[['_route' => 'app_reservation_edit', '_controller' => 'App\\Controller\\ReservationController::edit'], ['id'], ['PUT' => 0], null, false, false, null]],
        816 => [[['_route' => 'app_reservation_delete', '_controller' => 'App\\Controller\\ReservationController::delete'], ['id'], ['DELETE' => 0], null, false, true, null]],
        841 => [[['_route' => 'app_review_show', '_controller' => 'App\\Controller\\ReviewController::show'], ['id'], ['GET' => 0], null, false, true, null]],
        854 => [[['_route' => 'app_review_edit', '_controller' => 'App\\Controller\\ReviewController::edit'], ['id'], ['PUT' => 0], null, false, false, null]],
        862 => [[['_route' => 'app_review_delete', '_controller' => 'App\\Controller\\ReviewController::delete'], ['id'], ['DELETE' => 0], null, false, true, null]],
        902 => [[['_route' => 'app_tour_personnalise_show', '_controller' => 'App\\Controller\\TourPersonnaliseController::show'], ['id'], ['GET' => 0], null, false, true, null]],
        915 => [[['_route' => 'app_tour_personnalise_edit', '_controller' => 'App\\Controller\\TourPersonnaliseController::edit'], ['id'], ['PUT' => 0], null, false, false, null]],
        923 => [
            [['_route' => 'app_tour_personnalise_delete', '_controller' => 'App\\Controller\\TourPersonnaliseController::delete'], ['id'], ['DELETE' => 0], null, false, true, null],
            [null, null, null, null, false, false, 0],
        ],
    ],
    null, // $checkCondition
];
