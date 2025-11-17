<?php

namespace App\Controller\Api;

use App\Entity\Favori;
use App\Entity\Place;
use App\Entity\User;
use App\Service\FavoriService;
use App\Service\PlaceService;
use App\Service\UserService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Serializer\SerializerInterface;

#[Route('/api/favorites')]
class FavoriController extends AbstractController
{
    public function __construct(
        private FavoriService $favoriService,
        private PlaceService $placeService,
        private UserService $userService,
        private SerializerInterface $serializer
    ) {
    }

    #[Route('', name: 'api_favorites_list', methods: ['GET'])]
    public function index(): JsonResponse
    {
        $favorites = $this->favoriService->findAll();
        $data = $this->serializer->serialize($favorites, 'json', ['groups' => 'favori:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('/{id}', name: 'api_favorites_show', methods: ['GET'])]
    public function show(int $id): JsonResponse
    {
        $favori = $this->favoriService->find($id);
        if (!$favori) {
            return new JsonResponse(['error' => 'Favorite not found'], 404);
        }

        $data = $this->serializer->serialize($favori, 'json', ['groups' => 'favori:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('', name: 'api_favorites_create', methods: ['POST'])]
    public function create(Request $request): JsonResponse
    {
        $data = json_decode($request->getContent(), true);
        
        $place = $this->placeService->find($data['placeId'] ?? 0);
        $user = $this->userService->find($data['userId'] ?? 0);

        if (!$place || !$user) {
            return new JsonResponse(['error' => 'Place or User not found'], 404);
        }

        // Check if already favorited
        $existing = $this->favoriService->findUserFavoriteForPlace($user, $place->getId());
        if ($existing) {
            return new JsonResponse(['error' => 'Place already in favorites'], 400);
        }

        $favori = new Favori();
        $favori->setPlace($place);
        $favori->setUser($user);

        try {
            $favori = $this->favoriService->create($favori);
            $response = $this->serializer->serialize($favori, 'json', ['groups' => 'favori:read']);
            return new JsonResponse(json_decode($response, true), 201);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => $e->getMessage()], 400);
        }
    }

    #[Route('/{id}', name: 'api_favorites_delete', methods: ['DELETE'])]
    public function delete(int $id): JsonResponse
    {
        $favori = $this->favoriService->find($id);
        if (!$favori) {
            return new JsonResponse(['error' => 'Favorite not found'], 404);
        }

        try {
            $this->favoriService->delete($favori);
            return new JsonResponse(['message' => 'Favorite removed successfully'], 200);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => $e->getMessage()], 400);
        }
    }

    #[Route('/user/{userId}', name: 'api_favorites_by_user', methods: ['GET'])]
    public function getByUser(int $userId): JsonResponse
    {
        $user = $this->userService->find($userId);
        if (!$user) {
            return new JsonResponse(['error' => 'User not found'], 404);
        }

        $favorites = $this->favoriService->findByUser($user);
        $data = $this->serializer->serialize($favorites, 'json', ['groups' => 'favori:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }
}

