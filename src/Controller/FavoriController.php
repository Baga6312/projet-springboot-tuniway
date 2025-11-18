<?php

namespace App\Controller;

use App\Entity\Favori;
use App\Entity\Place;
use App\Entity\User;
use App\Service\FavoriService;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/favori')]
final class FavoriController extends AbstractController
{
    public function __construct(
        private FavoriService $favoriService,
        private EntityManagerInterface $entityManager
    ) {
    }

    #[Route(name: 'app_favori_index', methods: ['GET'])]
    public function index(): JsonResponse
    {
        return $this->json(
            $this->favoriService->findAll(),
            Response::HTTP_OK,
            [],
            ['groups' => 'favori:read']
        );
    }

    #[Route('/new', name: 'app_favori_new', methods: ['POST'])]
    public function new(Request $request): JsonResponse
    {
        $data = $this->parsePayload($request);
        if ($data === null) {
            return $this->json(['error' => 'Invalid JSON payload'], Response::HTTP_BAD_REQUEST);
        }

        $userId = $data['userId'] ?? null;
        $placeId = $data['placeId'] ?? null;

        if (!$userId || !$placeId) {
            return $this->json(['error' => 'Both "userId" and "placeId" are required'], Response::HTTP_BAD_REQUEST);
        }

        $user = $this->entityManager->getRepository(User::class)->find($userId);
        $place = $this->entityManager->getRepository(Place::class)->find($placeId);

        if (!$user || !$place) {
            return $this->json(['error' => 'User or Place not found'], Response::HTTP_NOT_FOUND);
        }

        $favori = new Favori();
        $favori->setUser($user);
        $favori->setPlace($place);

        $favori = $this->favoriService->create($favori);

        return $this->json($favori, Response::HTTP_CREATED, [], ['groups' => 'favori:read']);
    }

    #[Route('/{id}', name: 'app_favori_show', methods: ['GET'])]
    public function show(int $id): JsonResponse
    {
        $favori = $this->favoriService->find($id);
        if (!$favori) {
            return $this->json(['error' => 'Favorite not found'], Response::HTTP_NOT_FOUND);
        }

        return $this->json($favori, Response::HTTP_OK, [], ['groups' => 'favori:read']);
    }

    #[Route('/{id}/edit', name: 'app_favori_edit', methods: ['PUT'])]
    public function edit(int $id, Request $request): JsonResponse
    {
        $favori = $this->favoriService->find($id);
        if (!$favori) {
            return $this->json(['error' => 'Favorite not found'], Response::HTTP_NOT_FOUND);
        }

        $data = $this->parsePayload($request);
        if ($data === null) {
            return $this->json(['error' => 'Invalid JSON payload'], Response::HTTP_BAD_REQUEST);
        }

        if (isset($data['userId'])) {
            $user = $this->entityManager->getRepository(User::class)->find($data['userId']);
            if (!$user) {
                return $this->json(['error' => 'User not found'], Response::HTTP_NOT_FOUND);
            }
            $favori->setUser($user);
        }

        if (isset($data['placeId'])) {
            $place = $this->entityManager->getRepository(Place::class)->find($data['placeId']);
            if (!$place) {
                return $this->json(['error' => 'Place not found'], Response::HTTP_NOT_FOUND);
            }
            $favori->setPlace($place);
        }

        $favori = $this->favoriService->update($favori);

        return $this->json($favori, Response::HTTP_OK, [], ['groups' => 'favori:read']);
    }

    #[Route('/{id}', name: 'app_favori_delete', methods: ['DELETE'])]
    public function delete(int $id): JsonResponse
    {
        $favori = $this->favoriService->find($id);
        if (!$favori) {
            return $this->json(['error' => 'Favorite not found'], Response::HTTP_NOT_FOUND);
        }

        $this->favoriService->delete($favori);

        return $this->json(['message' => 'Favorite deleted successfully'], Response::HTTP_OK);
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
