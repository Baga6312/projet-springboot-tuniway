# TuniWay 🇹🇳

Application web Spring Boot pour explorer les destinations touristiques et les points d'intérêt en Tunisie.

## Prérequis
- Java 17 ou supérieur
- Maven 3.6+
- MySQL (ou votre base de données préférée)

## Installation

1. Clonez le repository :
```bash
git clone https://github.com/votre-username/tunisia-explorer.git
cd tunisia-explorer
```

2. Configurez la base de données dans `src/main/resources/application.properties` :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tunisia_explorer
spring.datasource.username=votre_username
spring.datasource.password=votre_password
```

3. Installez les dépendances :
```bash
mvn clean install
```
4. injection des données :
```
mysql -u root 
create database tuniway ; 
```
```
mysql -u root tuniway < seed_data.sql
```

## Lancement

Démarrez l'application avec Maven :
```bash
mvn spring-boot:run
```


L'application sera accessible sur `http://localhost:8080`



## Technologies utilisées

- Spring Boot
- Spring Data JPA
- MySQL
- Maven  
