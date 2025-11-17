<?php

namespace App\Service;

use App\Entity\Review;
use App\Entity\Place;
use App\Entity\User;
use App\Repository\ReviewRepository;
use Doctrine\ORM\EntityManagerInterface;

class ReviewService
{
    public function __construct(
        private ReviewRepository $reviewRepository,
        private EntityManagerInterface $entityManager
    ) {
    }

    public function findAll(): array
    {
        return $this->reviewRepository->findAll();
    }

    public function find(int $id): ?Review
    {
        return $this->reviewRepository->find($id);
    }

    public function create(Review $review): Review
    {
        if (!$review->getCreatedAt()) {
            $review->setCreatedAt(new \DateTimeImmutable());
        }

        $this->entityManager->persist($review);
        $this->entityManager->flush();

        return $review;
    }

    public function update(Review $review): Review
    {
        $this->entityManager->flush();
        return $review;
    }

    public function delete(Review $review): bool
    {
        $this->entityManager->remove($review);
        $this->entityManager->flush();
        return true;
    }

    public function findByPlace(Place $place): array
    {
        return $this->reviewRepository->findBy(['place' => $place]);
    }

    public function findByUser(User $user): array
    {
        return $this->reviewRepository->findBy(['user' => $user]);
    }

    public function getAverageRating(Place $place): float
    {
        $reviews = $this->findByPlace($place);
        if (empty($reviews)) {
            return 0.0;
        }

        $totalRating = 0;
        foreach ($reviews as $review) {
            $totalRating += $review->getRating();
        }

        return round($totalRating / count($reviews), 2);
    }
}

