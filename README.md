# TuniWay 🇹🇳

Application web Symfony pour explorer les destinations touristiques et les points d'intérêt en Tunisie.

## Prérequis

- PHP 8.1 ou supérieur
- Composer
- Symfony CLI (optionnel)
- MySQL 8.0+ ou MariaDB 10.4+

Extensions PHP requises :
```ini
extension=pdo_mysql
extension=mysqli
extension=openssl
extension=mbstring
extension=xml
extension=intl
```

## Installation

1. Clonez le repository :
```bash
git clone https://github.com/votre-username/tuniway.git
cd tuniway
```

2. Installez les dépendances :
```bash
composer install
```

3. Configurez la base de données dans `.env.local` :
```env
DATABASE_URL="mysql://root:@127.0.0.1:3306/tuniway?serverVersion=8.0"
```

4. Créez la base de données :
```bash
symfony console doctrine:database:create
```

5. Exécutez les migrations :
```bash
symfony console doctrine:migrations:migrate
```

## Lancement

Démarrez l'application avec Symfony CLI :
```bash
symfony serve
```

Ou avec le serveur PHP :
```bash
php -S localhost:8000 -t public/
```

L'application sera accessible sur `http://127.0.0.1:8000`

## Technologies utilisées

- Symfony 6.4
- Doctrine ORM
- Twig
- MySQL
- Bootstrap 5
