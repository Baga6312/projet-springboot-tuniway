<?php

namespace App\Service;

use App\Entity\User;
use App\Repository\UserRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;

class UserService
{
    public function __construct(
        private UserRepository $userRepository,
        private EntityManagerInterface $entityManager,
        private UserPasswordHasherInterface $passwordHasher
    ) {
    }

    public function findAll(): array
    {
        return $this->userRepository->findAll();
    }

    public function find(int $id): ?User
    {
        return $this->userRepository->find($id);
    }

    public function findByEmail(string $email): ?User
    {
        return $this->userRepository->findOneBy(['email' => $email]);
    }

    public function create(User $user, string $plainPassword = null): User
    {
        if ($plainPassword) {
            $hashedPassword = $this->passwordHasher->hashPassword($user, $plainPassword);
            $user->setPassword($hashedPassword);
        }

        if (!$user->getCreatedAt()) {
            $user->setCreatedAt(new \DateTimeImmutable());
        }

        $this->entityManager->persist($user);
        $this->entityManager->flush();

        return $user;
    }

    public function update(User $user): User
    {
        $this->entityManager->flush();
        return $user;
    }

    public function delete(User $user): bool
    {
        $this->entityManager->remove($user);
        $this->entityManager->flush();
        return true;
    }

    public function getUserReviews(User $user): array
    {
        return $user->getReviews()->toArray();
    }

    public function getUserReservations(User $user): array
    {
        return $user->getReservations()->toArray();
    }

    public function getUserTours(User $user): array
    {
        return $user->getTourPersonnalises()->toArray();
    }

    public function getUserFavorites(User $user): array
    {
        return $user->getFavoris()->toArray();
    }
}

