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
┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│     VIEW     │ ◄────── │  CONTROLLER  │ ◄────── │    MODEL     │
│    (Vue)     │         │ (Contrôleur) │         │  (Modèle)    │
└──────────────┘         └──────────────┘         └──────────────┘
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

### **1. Single Responsibility Principle (SRP)**

### **Principe:**

Une classe ne doit avoir qu'une seule raison de changer. Chaque classe doit avoir une seule responsabilité.

### **Application dans TuniWay:**

#### **Exemple 1: Services séparés**

Chaque service gère une seule entité et ses opérations:

- **`UserService.java`**: Responsable uniquement de la gestion des utilisateurs (CRUD operations)
- **`PlaceService.java`**: Responsable uniquement de la gestion des lieux touristiques
- **`ReviewService.java`**: Responsable uniquement de la gestion des avis
- **`ReservationService.java`**: Responsable uniquement de la gestion des réservations

<img src="assets/Pasted image 20251029190308.png" alt="Services séparés">

#### **Exemple 2: Chain of Responsibility Handlers**

Chaque handler a une seule responsabilité de validation:

- **`ReviewCheckHandler`**: Vérifie uniquement si l'utilisateur a des avis
- **`ClientReservationCheckHandler`**: Vérifie uniquement les réservations du client
- **`ClientTourCheckHandler`**: Vérifie uniquement les tours du client

<img src="assets/Pasted image 20251029190343.png" alt="Chain Handlers">

---

## **2. Open/Closed Principle (OCP)**

### **Principe:**

Les entités logicielles doivent être ouvertes à l'extension mais fermées à la modification.

### **Application dans TuniWay:**

#### **Exemple 1: Chain of Responsibility Pattern**

On peut ajouter de nouveaux handlers sans modifier le code existant:

```java
@Component
public class FavoriCheckHandler extends DeletionHandler {
    @Override
    public DeletionResult canDelete(Utilisateur user) {
        if (userHasFavorites(user)) {
            return new DeletionResult(false, "User has favorites");
        }
        return checkNext(user);
    }
}

reviewCheckHandler
    .setNext(clientReservationCheckHandler)
    .setNext(clientTourCheckHandler)
    .setNext(favoriCheckHandler)  // ← Nouveau handler ajouté
    .setNext(guideReservationCheckHandler);
```

#### **Exemple 2: Flyweight Pattern avec CategoryData**

Le système de catégories est extensible sans modification:

`PlaceCategoryFactory.java` <img src="assets/Pasted image 20251029190622.png" alt="PlaceCategoryFactory">

#### **Exemple 3: Hiérarchie Utilisateur**

On peut ajouter de nouveaux types d'utilisateurs sans modifier la classe parent:

`Utilisateur.java` 
<img src="assets/Pasted image 20251029190858.png" alt="Utilisateur">

---

## **3. Liskov Substitution Principle (LSP)**

### **Principe:**

Les objets d'une classe dérivée doivent pouvoir remplacer les objets de la classe de base sans altérer le comportement du programme.

### **Application dans TuniWay:**

#### **Exemple 1: Hiérarchie Utilisateur**

Tous les types d'utilisateurs peuvent remplacer `Utilisateur`:

`userService.java` 
<img src="assets/Pasted image 20251029191238.png" alt="UserService">

`UserController.java` 
<img src="assets/Pasted image 20251029191559.png" alt="UserController">

#### **Exemple 2: DeletionHandler Chain**

Tous les handlers peuvent remplacer `DeletionHandler`:

`DeletionHandler.java` <img src="assets/Pasted image 20251029191629.png" alt="DeletionHandler Chain">

### **Comportement Préservé:**

- `Client`, `Guide`, et `Admin` ont tous un `id`, `username`, `email`, `password`, `role`
- Ils peuvent tous être sauvegardés, mis à jour, supprimés de la même manière
- Le polymorphisme fonctionne correctement

---

## **4. Interface Segregation Principle (ISP)**

### **Principe:**

Les clients ne doivent pas dépendre d'interfaces qu'ils n'utilisent pas. Préférer plusieurs interfaces spécifiques plutôt qu'une interface générale.

### **Application dans TuniWay:**

#### **Exemple 1: Repositories Spécifiques**

Chaque repository expose uniquement les méthodes nécessaires:

`PlaceRepository.java` 
<img src="assets/Pasted image 20251029191747.png" alt="PlaceRepository">

`ReviewRepository.java` 
<img src="assets/Pasted image 20251029191828.png" alt="ReviewRepository">

`UserRepositor.java` 
<img src="assets/Pasted image 20251029191953.png" alt="UserRepository">

#### **Exemple 2: Services Ségrégués**

Chaque service n'expose que les opérations pertinentes:

`placeService.java` 
<img src="assets/Pasted image 20251029192136.png" alt="PlaceService">

#### **Exemple 3: Controllers Spécialisés**

Chaque controller gère uniquement ses endpoints:

//view source

---

## **5. Dependency Inversion Principle (DIP)**

### **Principe:**

Les modules de haut niveau ne doivent pas dépendre des modules de bas niveau. Les deux doivent dépendre d'abstractions. Les abstractions ne doivent pas dépendre des détails, mais les détails doivent dépendre des abstractions.

### **Application dans TuniWay:**

#### **Exemple 1: Controllers → Services (Abstraction)**

Les controllers dépendent des interfaces de service, pas des implémentations:

`UserController.java` 
<img src="assets/Pasted image 20251029192520.png" alt="UserController Dependencies">

#### **Exemple 2: Services → Repositories (Abstraction)**

Les services dépendent des interfaces repository (fournies par Spring Data JPA):

`placeService.java` 
<img src="assets/Pasted image 20251029192735.png" alt="PlaceService Dependencies">

#### **Exemple 3: Chain Handlers → Services**

Les handlers dépendent des abstractions de service:

`reviewCheckHanlder.java` 
<img src="assets/Pasted image 20251029192856.png" alt="ReviewCheckHandler Dependencies">

#### **Exemple 4: Dependency Injection avec Spring**

Spring Boot gère l'injection des dépendances:

`DatabaseConfig.java` 
<img src="assets/Pasted image 20251029193010.png" alt="DatabaseConfig">

**DIP (TuniWay - Bon):**

```
UserController → UserService (abstraction)
                      ↑
                UserServiceImpl
                      ↓
              UserRepository (abstraction)
                      ↑
              UserRepositoryImpl (Spring Data)
                      ↓
                   MySQL
```