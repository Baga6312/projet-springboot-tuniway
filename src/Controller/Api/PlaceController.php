<?php

namespace App\Controller\Api;

use App\Entity\Place;
use App\Service\PlaceService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Serializer\SerializerInterface;

#[Route('/api/places')]
class PlaceController extends AbstractController
{
    public function __construct(
        private PlaceService $placeService,
        private SerializerInterface $serializer
    ) {
    }

    #[Route('', name: 'api_places_list', methods: ['GET'])]
    public function index(): JsonResponse
    {
        $places = $this->placeService->findAll();
        $data = $this->serializer->serialize($places, 'json', ['groups' => 'place:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('/{id}', name: 'api_places_show', methods: ['GET'])]
    public function show(int $id): JsonResponse
    {
        $place = $this->placeService->find($id);
        if (!$place) {
            return new JsonResponse(['error' => 'Place not found'], 404);
        }

        $data = $this->serializer->serialize($place, 'json', ['groups' => 'place:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('', name: 'api_places_create', methods: ['POST'])]
    public function create(Request $request): JsonResponse
    {
        $data = json_decode($request->getContent(), true);
        $place = new Place();
        $place->setName($data['name'] ?? '');
        $place->setDescription($data['description'] ?? null);
        $place->setCategory($data['category'] ?? '');
        $place->setLatitude($data['latitude'] ?? null);
        $place->setLongtitude($data['longitude'] ?? null);
        $place->setAddress($data['address'] ?? null);
        $place->setImageUrl($data['imageUrl'] ?? null);

        try {
            $place = $this->placeService->create($place);
            $response = $this->serializer->serialize($place, 'json', ['groups' => 'place:read']);
            return new JsonResponse(json_decode($response, true), 201);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => $e->getMessage()], 400);
        }
    }

    #[Route('/{id}', name: 'api_places_update', methods: ['PUT'])]
    public function update(int $id, Request $request): JsonResponse
    {
        $place = $this->placeService->find($id);
        if (!$place) {
            return new JsonResponse(['error' => 'Place not found'], 404);
        }

        $data = json_decode($request->getContent(), true);
        if (isset($data['name'])) $place->setName($data['name']);
        if (isset($data['description'])) $place->setDescription($data['description']);
        if (isset($data['category'])) $place->setCategory($data['category']);
        if (isset($data['latitude'])) $place->setLatitude($data['latitude']);
        if (isset($data['longitude'])) $place->setLongtitude($data['longitude']);
        if (isset($data['address'])) $place->setAddress($data['address']);
        if (isset($data['imageUrl'])) $place->setImageUrl($data['imageUrl']);

        try {
            $place = $this->placeService->update($place);
            $response = $this->serializer->serialize($place, 'json', ['groups' => 'place:read']);
            return new JsonResponse(json_decode($response, true), 200);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => $e->getMessage()], 400);
        }
    }

    #[Route('/{id}', name: 'api_places_delete', methods: ['DELETE'])]
    public function delete(int $id): JsonResponse
    {
        $place = $this->placeService->find($id);
        if (!$place) {
            return new JsonResponse(['error' => 'Place not found'], 404);
        }

        try {
            $this->placeService->delete($place);
            return new JsonResponse(['message' => 'Place deleted successfully'], 200);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => $e->getMessage()], 400);
        }
    }

    #[Route('/search', name: 'api_places_search', methods: ['GET'])]
    public function search(Request $request): JsonResponse
    {
        $query = $request->query->get('q', '');
        if (empty($query)) {
            return new JsonResponse(['error' => 'Query parameter "q" is required'], 400);
        }

        $places = $this->placeService->search($query);
        $data = $this->serializer->serialize($places, 'json', ['groups' => 'place:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('/category/{category}', name: 'api_places_category', methods: ['GET'])]
    public function findByCategory(string $category): JsonResponse
    {
        $places = $this->placeService->findByCategory($category);
        $data = $this->serializer->serialize($places, 'json', ['groups' => 'place:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('/nearby', name: 'api_places_nearby', methods: ['GET'])]
    public function findNearby(Request $request): JsonResponse
    {
        $lat = (float) $request->query->get('lat', 0);
        $lng = (float) $request->query->get('lng', 0);
        $radius = (float) $request->query->get('radius', 10);

        if ($lat === 0.0 || $lng === 0.0) {
            return new JsonResponse(['error' => 'Latitude and longitude parameters are required'], 400);
        }

        $places = $this->placeService->findNearby($lat, $lng, $radius);
        $data = $this->serializer->serialize($places, 'json', ['groups' => 'place:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('/{id}/reviews', name: 'api_places_reviews', methods: ['GET'])]
    public function getPlaceReviews(int $id): JsonResponse
    {
        $place = $this->placeService->find($id);
        if (!$place) {
            return new JsonResponse(['error' => 'Place not found'], 404);
        }

        $reviews = $this->placeService->getPlaceReviews($place);
        $data = $this->serializer->serialize($reviews, 'json', ['groups' => 'review:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }
}

