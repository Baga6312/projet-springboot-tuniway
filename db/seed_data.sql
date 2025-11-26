-- Complete database seed file for TuniWay
-- Run this after dropping all tables and recreating them

-- ============================================
-- USERS DATA
-- ============================================
INSERT INTO user (DTYPE, email, password, profilePicture, role, username) VALUES
('Client', 'alice.client@test.com', 'password123', 'https://i.pravatar.cc/150?img=6', 'CLIENT', 'aliceclient'),
('Client', 'bob.client@test.com', 'password123', 'https://i.pravatar.cc/150?img=7', 'CLIENT', 'bobclient'),
('Client', 'charlie.client@test.com', 'password123', 'https://i.pravatar.cc/150?img=8', 'CLIENT', 'charlieclient'),
('Client', 'diana.client@test.com', 'password123', 'https://i.pravatar.cc/150?img=9', 'CLIENT', 'dianaclient'),
('Client', 'frank.client@test.com', 'password123', 'https://i.pravatar.cc/150?img=10', 'CLIENT', 'frankclient'),
('Client', 'grace.client@test.com', 'password123', 'https://i.pravatar.cc/150?img=11', 'CLIENT', 'graceclient'),
('Client', 'henry.client@test.com', 'password123', 'https://i.pravatar.cc/150?img=12', 'CLIENT', 'henryclient'),
('Client', 'isabel.client@test.com', 'password123', 'https://i.pravatar.cc/150?img=13', 'CLIENT', 'isabelclient'),
('Guide', 'leo.guide@test.com', 'password123', 'https://i.pravatar.cc/150?img=16', 'GUIDE', 'leoguide'),
('Guide', 'maria.guide@test.com', 'password123', 'https://i.pravatar.cc/150?img=17', 'GUIDE', 'mariaguide'),
('Guide', 'noah.guide@test.com', 'password123', 'https://i.pravatar.cc/150?img=18', 'GUIDE', 'noahguide'),
('Guide', 'olivia.guide@test.com', 'password123', 'https://i.pravatar.cc/150?img=19', 'GUIDE', 'oliviaguide'),
('Guide', 'paul.guide@test.com', 'password123', 'https://i.pravatar.cc/150?img=20', 'GUIDE', 'paulguide'),
('Admin', 'admin@tuniway.com', 'admin123', 'https://i.pravatar.cc/150?img=1', 'ADMIN', 'admin'),
('Admin', 'tina.admin@test.com', 'password123', 'https://i.pravatar.cc/150?img=24', 'ADMIN', 'tinaadmin');

-- ============================================
-- PLACES DATA
-- ============================================
INSERT INTO place (name, description, category, city, imageUrl, latitude, longitude) VALUES
('Carthage Ruins', 'Ancient Roman ruins and archaeological site', 'HISTORICAL', 'Tunis', 'https://images.unsplash.com/photo-1585155770728-b8cd1db9c145', 36.8525, 10.3233),
('Bardo Museum', 'National museum of Tunisia with Roman mosaics', 'MUSEUM', 'Tunis', 'https://images.unsplash.com/photo-1566127992631-137a642a90f4', 36.8097, 10.1347),
('Sidi Bou Said', 'Picturesque blue and white village', 'HISTORICAL', 'Sidi Bou Said', 'https://images.unsplash.com/photo-1539037116277-4db20889f2d4', 36.8688, 10.3431),
('La Goulette Beach', 'Popular Mediterranean beach', 'BEACH', 'La Goulette', 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e', 36.8181, 10.3050),
('Hammamet Beach', 'Beautiful sandy beach with clear water', 'BEACH', 'Hammamet', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19', 36.4000, 10.6167),
('Dar El Jeld', 'Traditional Tunisian restaurant', 'RESTAURANT', 'Tunis', 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4', 36.7968, 10.1705),
('Le Baroque', 'Fine dining Mediterranean cuisine', 'RESTAURANT', 'Tunis', 'https://images.unsplash.com/photo-1559339352-11d035aa65de', 36.8065, 10.1815),
('Hotel Africa', 'Luxury hotel in city center', 'HOTEL', 'Tunis', 'https://images.unsplash.com/photo-1566073771259-6a8506099945', 36.8008, 10.1815),
('Movenpick Gammarth', '5-star beachfront resort', 'HOTEL', 'Gammarth', 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb', 36.8833, 10.2833),
('Medina of Tunis', 'UNESCO World Heritage old city', 'HISTORICAL', 'Tunis', 'https://images.unsplash.com/photo-1591604466107-ec97de577aff', 36.7989, 10.1686),
('El Jem Amphitheatre', 'Roman colosseum UNESCO site', 'HISTORICAL', 'El Jem', 'https://images.unsplash.com/photo-1513415756668-eb4b211d33e4', 35.2964, 10.7072),
('Ichkeul National Park', 'Lake and wetlands reserve', 'NATURE', 'Bizerte', 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e', 37.1667, 9.6667),
('Chott el Djerid', 'Large salt lake in Sahara', 'NATURE', 'Tozeur', 'https://images.unsplash.com/photo-1473496169904-658ba7c44d8a', 33.7000, 8.4333),
('Tunisia Mall', 'Modern shopping center', 'SHOPPING', 'Berges du Lac', 'https://images.unsplash.com/photo-1555529902-5261145633bf', 36.8372, 10.2428),
('Souk Medina', 'Traditional marketplace', 'SHOPPING', 'Tunis', 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5', 36.7981, 10.1706),
('Carthageland Park', 'Amusement park', 'ENTERTAINMENT', 'Yasmine Hammamet', 'https://images.unsplash.com/photo-1594818379496-da1e345b0ded', 36.4167, 10.6000),
('Djerba Explore', 'Cultural entertainment park', 'ENTERTAINMENT', 'Djerba', 'https://images.unsplash.com/photo-1503328427499-d92d1ac3d174', 33.8076, 10.8451),
('Dar Zarrouk', 'Seafood restaurant sea view', 'RESTAURANT', 'Sidi Bou Said', 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0', 36.8686, 10.3428),
('Sousse Beach', 'Long sandy tourist beach', 'BEACH', 'Sousse', 'https://images.unsplash.com/photo-1519046904884-53103b34b206', 35.8256, 10.6369),
('Ribat of Sousse', 'Historic fortress watchtower', 'HISTORICAL', 'Sousse', 'https://images.unsplash.com/photo-1591604129853-c6b39db1cc89', 35.8283, 10.6369);

-- ============================================
-- REVIEWS DATA (user_id and place_id reference the IDs from above)
-- ============================================
INSERT INTO review (rating, datePosted, place_id, user_id, comment) VALUES
(5, '2024-11-01 10:30:00', 1, 1, 'Amazing historical site! The ruins are breathtaking.'),
(4, '2024-11-02 14:15:00', 1, 2, 'Great place to visit, very educational.'),
(5, '2024-11-03 09:20:00', 2, 3, 'The mosaics are incredible. A must-see museum!'),
(3, '2024-11-04 16:45:00', 3, 4, 'Beautiful village but very crowded with tourists.'),
(5, '2024-11-05 11:00:00', 4, 5, 'Perfect beach day! Clean water and nice atmosphere.'),
(4, '2024-11-06 13:30:00', 5, 6, 'Lovely beach, great for families.'),
(5, '2024-11-07 19:00:00', 6, 7, 'Authentic Tunisian food, amazing couscous!'),
(4, '2024-11-08 20:15:00', 7, 8, 'Fine dining experience, a bit pricey but worth it.'),
(5, '2024-11-09 08:00:00', 8, 1, 'Excellent hotel, great location in the city center.'),
(5, '2024-11-10 15:30:00', 9, 2, 'Luxury resort with stunning beach views.'),
(4, '2024-11-11 10:45:00', 10, 3, 'The Medina is fascinating, full of history.'),
(5, '2024-11-12 12:00:00', 11, 4, 'Impressive Roman amphitheater, well preserved!'),
(4, '2024-11-13 09:30:00', 12, 5, 'Great nature reserve, saw many birds.'),
(3, '2024-11-14 14:00:00', 13, 6, 'Interesting salt lake, very hot in summer.'),
(4, '2024-11-15 17:20:00', 14, 7, 'Modern mall with good shops and restaurants.'),
(5, '2024-11-16 11:15:00', 15, 8, 'Love the traditional souk, great for souvenirs!'),
(4, '2024-11-17 13:45:00', 16, 1, 'Fun amusement park for kids and adults.'),
(5, '2024-11-18 10:00:00', 17, 2, 'Educational and entertaining cultural park.'),
(5, '2024-11-19 19:30:00', 18, 3, 'Best seafood restaurant with amazing views!'),
(4, '2024-11-20 12:30:00', 19, 4, 'Nice beach for swimming and sunbathing.');

-- ============================================
-- RESERVATIONS DATA (client_id = clients, guide_id = guides)
-- ============================================
INSERT INTO reservation (client_id, dateReservation, guide_id, status, type) VALUES
(1, '2024-12-01 09:00:00', 9, 'CONFIRMED', 'GUIDED_TOUR'),
(2, '2024-12-02 10:00:00', 10, 'PENDING', 'CUSTOM_TOUR'),
(3, '2024-12-03 14:00:00', 11, 'CONFIRMED', 'GUIDED_TOUR'),
(4, '2024-12-04 11:00:00', 12, 'COMPLETED', 'GUIDED_TOUR'),
(5, '2024-12-05 15:00:00', 13, 'CONFIRMED', 'CUSTOM_TOUR'),
(6, '2024-12-06 09:30:00', 9, 'PENDING', 'GUIDED_TOUR'),
(7, '2024-12-07 13:00:00', 10, 'CANCELLED', 'HOTEL_BOOKING'),
(8, '2024-12-08 16:00:00', 11, 'CONFIRMED', 'RESTAURANT_BOOKING'),
(1, '2024-12-09 10:30:00', 12, 'PENDING', 'CUSTOM_TOUR'),
(2, '2024-12-10 12:00:00', 13, 'CONFIRMED', 'GUIDED_TOUR');

-- ============================================
-- TOUR PERSONNALISE DATA (client_id = clients, guide_id = guides)
-- ============================================
INSERT INTO tourpersonnalise (date, prix, client_id, guide_id, description, titre) VALUES
('2024-12-15', 150.00, 1, 9, 'Private tour of Carthage ruins and Sidi Bou Said', 'Historic Tunis Tour'),
('2024-12-16', 200.00, 2, 10, 'Full day beach tour with lunch included', 'Mediterranean Coast Experience'),
('2024-12-17', 180.00, 3, 11, 'Desert adventure to Sahara with camel ride', 'Sahara Desert Explorer'),
('2024-12-18', 120.00, 4, 12, 'Culinary tour of Tunis best restaurants', 'Tunisian Food Journey'),
('2024-12-19', 250.00, 5, 13, 'Two-day tour covering all major sites', 'Complete Tunisia Discovery'),
('2024-12-20', 100.00, 6, 9, 'Shopping tour in Medina and modern malls', 'Shopping Paradise Tour'),
('2024-12-21', 175.00, 7, 10, 'Nature and wildlife tour of national parks', 'Nature & Wildlife Adventure'),
('2024-12-22', 90.00, 8, 11, 'Evening tour with traditional dinner show', 'Cultural Evening Experience'),
('2024-12-23', 220.00, 1, 12, 'Luxury resort day with spa and activities', 'Relaxation & Luxury Day'),
('2024-12-24', 160.00, 2, 13, 'Family-friendly tour with kid activities', 'Family Fun Tour');