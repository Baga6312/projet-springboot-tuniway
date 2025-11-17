<?php

namespace App\Service;

use App\Entity\Reservation;
use App\Entity\User;
use App\Repository\ReservationRepository;
use Doctrine\ORM\EntityManagerInterface;

class ReservationService
{
    public function __construct(
        private ReservationRepository $reservationRepository,
        private EntityManagerInterface $entityManager
    ) {
    }

    public function findAll(): array
    {
        return $this->reservationRepository->findAll();
    }

    public function find(int $id): ?Reservation
    {
        return $this->reservationRepository->find($id);
    }

    public function create(Reservation $reservation): Reservation
    {
        if (!$reservation->getCreatedAt()) {
            $reservation->setCreatedAt(new \DateTime());
        }

        $this->entityManager->persist($reservation);
        $this->entityManager->flush();

        return $reservation;
    }

    public function update(Reservation $reservation): Reservation
    {
        $this->entityManager->flush();
        return $reservation;
    }

    public function delete(Reservation $reservation): bool
    {
        $this->entityManager->remove($reservation);
        $this->entityManager->flush();
        return true;
    }

    public function findByUser(User $user): array
    {
        return $this->reservationRepository->findBy(['user' => $user]);
    }

    public function findByStatus(string $status): array
    {
        return $this->reservationRepository->findBy(['status' => $status]);
    }
}

