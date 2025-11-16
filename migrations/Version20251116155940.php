<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20251116155940 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE favori (id INT AUTO_INCREMENT NOT NULL, user_id INT NOT NULL, place_id INT NOT NULL, created_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', INDEX IDX_EF85A2CCA76ED395 (user_id), INDEX IDX_EF85A2CCDA6A219 (place_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE place (id INT AUTO_INCREMENT NOT NULL, name VARCHAR(200) NOT NULL, description LONGTEXT DEFAULT NULL, category VARCHAR(50) NOT NULL, latitude DOUBLE PRECISION DEFAULT NULL, longtitude DOUBLE PRECISION DEFAULT NULL, address VARCHAR(255) DEFAULT NULL, image_url VARCHAR(255) DEFAULT NULL, created_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE reservation (id INT AUTO_INCREMENT NOT NULL, user_id INT DEFAULT NULL, place_id INT NOT NULL, reservation_date DATETIME NOT NULL, status VARCHAR(50) NOT NULL, numbers_of_persons INT NOT NULL, total_price DOUBLE PRECISION DEFAULT NULL, created_at DATETIME NOT NULL, INDEX IDX_42C84955A76ED395 (user_id), INDEX IDX_42C84955DA6A219 (place_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE review (id INT AUTO_INCREMENT NOT NULL, user_id INT NOT NULL, place_id INT DEFAULT NULL, rating INT NOT NULL, comment LONGTEXT DEFAULT NULL, created_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', INDEX IDX_794381C6A76ED395 (user_id), INDEX IDX_794381C6DA6A219 (place_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE tour_personnalise (id INT AUTO_INCREMENT NOT NULL, guide_id INT NOT NULL, client_id INT NOT NULL, title VARCHAR(200) NOT NULL, description LONGTEXT DEFAULT NULL, duration INT NOT NULL, price DOUBLE PRECISION NOT NULL, max_persons INT NOT NULL, created_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', INDEX IDX_C51A082D7ED1D4B (guide_id), INDEX IDX_C51A08219EB6921 (client_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE user (id INT AUTO_INCREMENT NOT NULL, email VARCHAR(180) NOT NULL, roles JSON NOT NULL, password VARCHAR(255) NOT NULL, username VARCHAR(100) NOT NULL, role VARCHAR(50) NOT NULL, phone_number VARCHAR(20) NOT NULL, created_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', UNIQUE INDEX UNIQ_IDENTIFIER_EMAIL (email), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE messenger_messages (id BIGINT AUTO_INCREMENT NOT NULL, body LONGTEXT NOT NULL, headers LONGTEXT NOT NULL, queue_name VARCHAR(190) NOT NULL, created_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', available_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', delivered_at DATETIME DEFAULT NULL COMMENT \'(DC2Type:datetime_immutable)\', INDEX IDX_75EA56E0FB7336F0 (queue_name), INDEX IDX_75EA56E0E3BD61CE (available_at), INDEX IDX_75EA56E016BA31DB (delivered_at), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('ALTER TABLE favori ADD CONSTRAINT FK_EF85A2CCA76ED395 FOREIGN KEY (user_id) REFERENCES user (id)');
        $this->addSql('ALTER TABLE favori ADD CONSTRAINT FK_EF85A2CCDA6A219 FOREIGN KEY (place_id) REFERENCES place (id)');
        $this->addSql('ALTER TABLE reservation ADD CONSTRAINT FK_42C84955A76ED395 FOREIGN KEY (user_id) REFERENCES user (id)');
        $this->addSql('ALTER TABLE reservation ADD CONSTRAINT FK_42C84955DA6A219 FOREIGN KEY (place_id) REFERENCES place (id)');
        $this->addSql('ALTER TABLE review ADD CONSTRAINT FK_794381C6A76ED395 FOREIGN KEY (user_id) REFERENCES user (id)');
        $this->addSql('ALTER TABLE review ADD CONSTRAINT FK_794381C6DA6A219 FOREIGN KEY (place_id) REFERENCES place (id)');
        $this->addSql('ALTER TABLE tour_personnalise ADD CONSTRAINT FK_C51A082D7ED1D4B FOREIGN KEY (guide_id) REFERENCES user (id)');
        $this->addSql('ALTER TABLE tour_personnalise ADD CONSTRAINT FK_C51A08219EB6921 FOREIGN KEY (client_id) REFERENCES user (id)');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE favori DROP FOREIGN KEY FK_EF85A2CCA76ED395');
        $this->addSql('ALTER TABLE favori DROP FOREIGN KEY FK_EF85A2CCDA6A219');
        $this->addSql('ALTER TABLE reservation DROP FOREIGN KEY FK_42C84955A76ED395');
        $this->addSql('ALTER TABLE reservation DROP FOREIGN KEY FK_42C84955DA6A219');
        $this->addSql('ALTER TABLE review DROP FOREIGN KEY FK_794381C6A76ED395');
        $this->addSql('ALTER TABLE review DROP FOREIGN KEY FK_794381C6DA6A219');
        $this->addSql('ALTER TABLE tour_personnalise DROP FOREIGN KEY FK_C51A082D7ED1D4B');
        $this->addSql('ALTER TABLE tour_personnalise DROP FOREIGN KEY FK_C51A08219EB6921');
        $this->addSql('DROP TABLE favori');
        $this->addSql('DROP TABLE place');
        $this->addSql('DROP TABLE reservation');
        $this->addSql('DROP TABLE review');
        $this->addSql('DROP TABLE tour_personnalise');
        $this->addSql('DROP TABLE user');
        $this->addSql('DROP TABLE messenger_messages');
    }
}
