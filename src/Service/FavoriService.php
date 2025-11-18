<?php

namespace App\Service;

use App\Entity\Favori;
use App\Entity\User;
use App\Repository\FavoriRepository;
use Doctrine\ORM\EntityManagerInterface;

class FavoriService
{
    public function __construct(
        private FavoriRepository $favoriRepository,
        private EntityManagerInterface $entityManager
    ) {
    }

    public function findAll(): array
    {
        return $this->favoriRepository->findAll();
    }

    public function find(int $id): ?Favori
    {
        return $this->favoriRepository->find($id);
    }

    public function create(Favori $favori): Favori
    {
        if (!$favori->getCreatedAt()) {
            $favori->setCreatedAt(new \DateTimeImmutable());
        }

        $this->entityManager->persist($favori);
        $this->entityManager->flush();

        return $favori;
    }

    public function update(Favori $favori): Favori
    {
        $this->entityManager->flush();
        return $favori;
    }

    public function delete(Favori $favori): bool
    {
        $this->entityManager->remove($favori);
        $this->entityManager->flush();
        return true;
    }

    public function findByUser(User $user): array
    {
        return $this->favoriRepository->findBy(['user' => $user]);
    }

    public function findUserFavoriteForPlace(User $user, int $placeId): ?Favori
    {
        return $this->favoriRepository->findOneBy([
            'user' => $user,
            'place' => $placeId
        ]);
    }
}

