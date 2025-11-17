<?php

namespace App\Service;

use App\Entity\TourPersonnalise;
use App\Entity\User;
use App\Repository\TourPersonnaliseRepository;
use Doctrine\ORM\EntityManagerInterface;

class TourPersonnaliseService
{
    public function __construct(
        private TourPersonnaliseRepository $tourRepository,
        private EntityManagerInterface $entityManager
    ) {
    }

    public function findAll(): array
    {
        return $this->tourRepository->findAll();
    }

    public function find(int $id): ?TourPersonnalise
    {
        return $this->tourRepository->find($id);
    }

    public function create(TourPersonnalise $tour): TourPersonnalise
    {
        if (!$tour->getCreatedAt()) {
            $tour->setCreatedAt(new \DateTimeImmutable());
        }

        $this->entityManager->persist($tour);
        $this->entityManager->flush();

        return $tour;
    }

    public function update(TourPersonnalise $tour): TourPersonnalise
    {
        $this->entityManager->flush();
        return $tour;
    }

    public function delete(TourPersonnalise $tour): bool
    {
        $this->entityManager->remove($tour);
        $this->entityManager->flush();
        return true;
    }

    public function findByGuide(User $guide): array
    {
        return $this->tourRepository->findBy(['guide' => $guide]);
    }

    public function findByClient(User $client): array
    {
        return $this->tourRepository->findBy(['client' => $client]);
    }
}

