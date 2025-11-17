<?php

namespace App\Service;

use App\Entity\Place;
use App\Repository\PlaceRepository;
use Doctrine\ORM\EntityManagerInterface;

class PlaceService
{
    public function __construct(
        private PlaceRepository $placeRepository,
        private EntityManagerInterface $entityManager
    ) {
    }

    public function findAll(): array
    {
        return $this->placeRepository->findAll();
    }

    public function find(int $id): ?Place
    {
        return $this->placeRepository->find($id);
    }

    public function create(Place $place): Place
    {
        if (!$place->getCreatedAt()) {
            $place->setCreatedAt(new \DateTimeImmutable());
        }

        $this->entityManager->persist($place);
        $this->entityManager->flush();

        return $place;
    }

    public function update(Place $place): Place
    {
        $this->entityManager->flush();
        return $place;
    }

    public function delete(Place $place): bool
    {
        $this->entityManager->remove($place);
        $this->entityManager->flush();
        return true;
    }

    public function search(string $query): array
    {
        return $this->placeRepository->createQueryBuilder('p')
            ->where('p.name LIKE :query')
            ->orWhere('p.description LIKE :query')
            ->orWhere('p.address LIKE :query')
            ->setParameter('query', '%' . $query . '%')
            ->getQuery()
            ->getResult();
    }

    public function findByCategory(string $category): array
    {
        return $this->placeRepository->findBy(['category' => $category]);
    }

    public function findNearby(float $latitude, float $longitude, float $radiusKm = 10): array
    {
        // Haversine formula for distance calculation
        $earthRadius = 6371; // km

        $places = $this->placeRepository->createQueryBuilder('p')
            ->where('p.latitude IS NOT NULL')
            ->andWhere('p.longtitude IS NOT NULL')
            ->getQuery()
            ->getResult();

        $nearbyPlaces = [];
        foreach ($places as $place) {
            $lat1 = deg2rad($latitude);
            $lat2 = deg2rad($place->getLatitude());
            $lon1 = deg2rad($longitude);
            $lon2 = deg2rad($place->getLongtitude());

            $dLat = $lat2 - $lat1;
            $dLon = $lon2 - $lon1;

            $a = sin($dLat / 2) ** 2 + cos($lat1) * cos($lat2) * sin($dLon / 2) ** 2;
            $c = 2 * atan2(sqrt($a), sqrt(1 - $a));
            $distance = $earthRadius * $c;

            if ($distance <= $radiusKm) {
                $nearbyPlaces[] = $place;
            }
        }

        return $nearbyPlaces;
    }

    public function getPlaceReviews(Place $place): array
    {
        return $place->getReviews()->toArray();
    }
}

