# **Chosen Subject**

## _sujet: TuniWay - Plateforme Touristique Intelligente_

### **problématique:**

les touristes visitant la Tunisie ne savent souvent pas quels sont les meilleurs endroits à visiter, le timing optimal, ou quelles attractions existent le long de leurs itinéraires de voyage.

### **solution:**

TuniWay est une plateforme qui aide les visiteurs à planifier leurs voyages en suggérant des destinations, en montrant les attractions à proximité le long de leur route, et en fournissant des informations sur le timing optimal.

### **utilisateurs cibles:**

touristes internationaux et voyageurs locaux explorant la Tunisie.

### **fonctionnalités principales:**

- parcourir les destinations touristiques
- planification d'itinéraires avec points d'intérêt le long du chemin
- système de réservation et personnalisation des tours via des guides
- consultation et partage d'avis sur les destinations
- sauvegarde des lieux favoris

### **objectifs:**

TuniWay vise à simplifier l'exploration touristique en Tunisie en connectant visiteurs et guides locaux, tout en permettant la réservation et la personnalisation d'itinéraires adaptés aux préférences de chaque utilisateur.

# **Style Architectural Utilisé: MVC (Model-View-Controller)**

**Nous avons adopté l'architecture MVC pour TuniWay car elle sépare l'application en trois parties claires: les données (Model), l'interface utilisateur (View) et la logique de traitement (Controller). Cette séparation rend le code plus facile à comprendre et à modifier.**

```
┌──────────┐         ┌──────────┐         ┌──────┐
│   VIEW          │ ◄─── │  CONTROLLER     │ ◄────│ MODEL    |
│   (Vue)         │         | (Contrôleur)    │         │(Modèle)  |
└──────────┘         └──────────┘         └──────┘
```

- **Model (Modèle):**
    - Les classes de données: `Utilisateur`, `Place`, `Review`, `Reservation`, `TourPersonnalise`
    - Représente ce qu'on stocke dans la base de données
- **View (Vue):**
    - Les pages que l'utilisateur voit
    - Interface graphique (web/mobile)
- **Controller (Contrôleur):**
    - Gère les actions: chercher un lieu, réserver, ajouter un avis
    - Fait le lien entre les pages et les données

**Structure du Projet:**

```
com.tuniway
│
├──  model
│   ├── Utilisateur.java
│   ├── Client.java
│   ├── Guide.java
│   ├── Admin.java
│   ├── Place.java
│   ├── Review.java
│   ├── Favori.java
│   ├── Reservation.java
│   └── TourPersonnalise.java
│
├──  controller
│   ├── UserController.java
│   ├── PlaceController.java
│   ├── ReviewController.java
│   ├── ReservationController.java
│   └── TourController.java
│
├──  view
│   ├── HomePage.java
│   ├── PlaceDetailsPage.java
│   ├── ReservationPage.java
│   ├── ProfilePage.java
│   └── AdminDashboard.java
│
├──  dao
│   ├── UserDAO.java
│   ├── PlaceDAO.java
│   ├── ReviewDAO.java
│   ├── ReservationDAO.java
│   └── TourDAO.java
│
└──  util
    ├── DatabaseConnection.java
    └── ValidationHelper.java
```

### **Justification du Choix:**

Nous avons choisi MVC car il sépare clairement les données, l'interface et la logique de traitement, ce qui rend le code plus simple à comprendre et à modifier. Cette architecture permet de changer l'interface sans toucher aux données, facilite le travail en équipe en divisant les tâches, et rend le projet facile à faire évoluer. MVC est bien adapté pour TuniWay car il gère efficacement les interactions entre utilisateurs, réservations et contenu touristique.

# **Diagramme de class**

### **Description :**

Le diagramme de classes de TuniWay représente la structure du système avec les entités principales et leurs relations:

**Classes Principales:**

- **Utilisateur (classe abstraite):** Classe parent avec trois types d'utilisateurs:
    - `Client`: Touriste qui visite des lieux et fait des réservations
    - `Guide`: Guide touristique qui crée des tours personnalisés
    - `Admin`: Administrateur qui gère le système
- **Place:** Représente les destinations touristiques avec coordonnées GPS, catégories et images
- **Review:** Avis et évaluations donnés par les clients sur les lieux visités
- **Favori:** Liste des lieux favoris sauvegardés par les utilisateurs
- **Reservation:** Réservations faites par les clients
- **TourPersonnalise:** Tours personnalisés créés par les guides pour les clients

**Relations:**

- Un Client peut donner plusieurs Reviews (0..*)
- Un Client peut visiter plusieurs Places
- Un Guide peut créer plusieurs TourPersonnalise (0..*)
- Un Client peut faire plusieurs Reservations
- Les utilisateurs peuvent ajouter des Places à leurs Favoris

<img src="assets/Pasted image 20251028183733.png" alt="Class Diagram">

# **Diagramme de packages**

### **Desctiption**

Le projet **TuniWay** suit une architecture **MVC enrichie** avec cinq packages : `model`, `controller`, `service` et `repository`, chacun ayant un rôle clair — du modèle de données jusqu'à la configuration technique. Cette organisation en couches garantit une **séparation des responsabilités**, une **maintenabilité élevée**, et un **respect des principes SOLID**, facilitant les tests, l'évolution et la scalabilité de l'application.****

<img src="assets/Pasted image 20251029124203.png" alt="Package Diagram">

# **Patrons de conception appliqués**

#### **Description**

Lors de la suppression d'un utilisateur dans TuniWay, plusieurs validations doivent être effectuées :

- Vérifier si l'utilisateur a des avis (reviews)
- Si c'est un Client : vérifier ses réservations et tours personnalisés
- Si c'est un Guide : vérifier ses réservations et tours créés

Sans ce patron, le contrôleur `UserController` contiendrait toute cette logique, rendant le code difficile à maintenir et violant le principe de responsabilité unique.

#### Solution :

- Implementation de Principe chain of responsibility : permet de faire passer une requête à travers une chaîne de handlers, où chaque handler décide soit de traiter la requête, soit de la passer au suivant. Cela évite de coupler l'émetteur de la requête aux objets qui la traitent.

#### Implementation :

Structure de la chaine :

```
	ReviewCheckHandler 
		    ↓
	ClientReservationCheckHandler
		    ↓
	ClientTourCheckHandler
		    ↓
	GuideReservationCheckHandler
		    ↓
	GuideTourCheckHandler
			↓
	Success (Utilisateur peut être supprimé)
```

`DeletionHandler.java` <img src="assets/Pasted image 20251029181857.png" alt="DeletionHandler">

`ReviewCheckHandler` <img src="assets/Pasted image 20251029182037.png" alt="ReviewCheckHandler">

`ClientReservationCheckHandler.java` <img src="assets/Pasted image 20251029182138.png" alt="ClientReservationCheckHandler">

`deletionResult.java` <img src="assets/Pasted image 20251029182319.png" alt="DeletionResult">

- Implementation de Principe Flyweight Le patron **Flyweight** permet de réduire la consommation mémoire en partageant les données communes entre plusieurs objets au lieu de les dupliquer. Il sépare les données partagées des données uniques.

### **Description**

Dans TuniWay, chaque lieu touristique (`Place`) appartient à une catégorie (HISTORICAL, BEACH, RESTAURANT, etc.). Chaque catégorie possède des données partagées :

- Une icône (emoji)
- Une description
- Un code couleur

#### **Implementation** :

```
	PlaceCategoryFactory (Factory)
			 ↓ 
	gère un cache de CategoryData (Flyweight - données partagées)
			 ↓
	utilisé par Place 1, Place 2, ..., Place N (contexte - données uniques)
```

`CategoryData.java` <img src="assets/Pasted image 20251029182728.png" alt="CategoryData">

`PlaceCategoryFactory.java` <img src="assets/Pasted image 20251029183334.png" alt="PlaceCategoryFactory">

`PlaceController.java` <img src="assets/Pasted image 20251029183720.png" alt="PlaceController">

# **Principes SOLID**