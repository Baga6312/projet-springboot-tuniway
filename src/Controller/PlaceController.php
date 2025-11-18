<?php

namespace App\Controller;

use App\Entity\Place;
use App\Service\PlaceService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/place')]
final class PlaceController extends AbstractController
{
    public function __construct(private PlaceService $placeService)
    {
    }

    #[Route(name: 'app_place_index', methods: ['GET'])]
    public function index(): JsonResponse
    {
        return $this->json(
            $this->placeService->findAll(),
            Response::HTTP_OK,
            [],
            ['groups' => 'place:read']
        );
    }

    #[Route('/new', name: 'app_place_new', methods: ['POST'])]
    public function new(Request $request): JsonResponse
    {
        $data = $this->parsePayload($request);
        if ($data === null) {
            return $this->json(['error' => 'Invalid JSON payload'], Response::HTTP_BAD_REQUEST);
        }

        if (empty($data['name']) || empty($data['category'])) {
            return $this->json(['error' => 'Fields "name" and "category" are required'], Response::HTTP_BAD_REQUEST);
        }

        $place = new Place();
        $place->setName($data['name']);
        $place->setCategory($data['category']);
        $place->setDescription($data['description'] ?? null);
        $place->setLatitude(isset($data['latitude']) ? (float) $data['latitude'] : null);
        $place->setLongtitude(isset($data['longitude']) ? (float) $data['longitude'] : null);
        $place->setAddress($data['address'] ?? null);
        $place->setImageUrl($data['imageUrl'] ?? null);
        $place->setCreatedAt(new \DateTimeImmutable());

        $place = $this->placeService->create($place);

        return $this->json($place, Response::HTTP_CREATED, [], ['groups' => 'place:read']);
    }

    #[Route('/{id}', name: 'app_place_show', methods: ['GET'])]
    public function show(int $id): JsonResponse
    {
        $place = $this->placeService->find($id);
        if (!$place) {
            return $this->json(['error' => 'Place not found'], Response::HTTP_NOT_FOUND);
        }

        return $this->json($place, Response::HTTP_OK, [], ['groups' => 'place:read']);
    }

    #[Route('/{id}/edit', name: 'app_place_edit', methods: ['PUT'])]
    public function edit(int $id, Request $request): JsonResponse
    {
        $place = $this->placeService->find($id);
        if (!$place) {
            return $this->json(['error' => 'Place not found'], Response::HTTP_NOT_FOUND);
        }

        $data = $this->parsePayload($request);
        if ($data === null) {
            return $this->json(['error' => 'Invalid JSON payload'], Response::HTTP_BAD_REQUEST);
        }

        if (isset($data['name'])) {
            $place->setName($data['name']);
        }
        if (isset($data['category'])) {
            $place->setCategory($data['category']);
        }
        if (array_key_exists('description', $data)) {
            $place->setDescription($data['description']);
        }
        if (isset($data['latitude'])) {
            $place->setLatitude((float) $data['latitude']);
        }
        if (isset($data['longitude'])) {
            $place->setLongtitude((float) $data['longitude']);
        }
        if (array_key_exists('address', $data)) {
            $place->setAddress($data['address']);
        }
        if (array_key_exists('imageUrl', $data)) {
            $place->setImageUrl($data['imageUrl']);
        }

        $place = $this->placeService->update($place);

        return $this->json($place, Response::HTTP_OK, [], ['groups' => 'place:read']);
    }

    #[Route('/{id}', name: 'app_place_delete', methods: ['DELETE'])]
    public function delete(int $id): JsonResponse
    {
        $place = $this->placeService->find($id);
        if (!$place) {
            return $this->json(['error' => 'Place not found'], Response::HTTP_NOT_FOUND);
        }

        $this->placeService->delete($place);

        return $this->json(['message' => 'Place deleted successfully'], Response::HTTP_OK);
    }

    private function parsePayload(Request $request): ?array
    {
        try {
            return $request->toArray();
        } catch (\JsonException) {
            return null;
        }
    }
}
