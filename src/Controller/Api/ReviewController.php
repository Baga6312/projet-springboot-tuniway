<?php

namespace App\Controller\Api;

use App\Entity\Review;
use App\Entity\Place;
use App\Entity\User;
use App\Service\ReviewService;
use App\Service\PlaceService;
use App\Service\UserService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Serializer\SerializerInterface;

#[Route('/api/reviews')]
class ReviewController extends AbstractController
{
    public function __construct(
        private ReviewService $reviewService,
        private PlaceService $placeService,
        private UserService $userService,
        private SerializerInterface $serializer
    ) {
    }

    #[Route('', name: 'api_reviews_list', methods: ['GET'])]
    public function index(): JsonResponse
    {
        $reviews = $this->reviewService->findAll();
        $data = $this->serializer->serialize($reviews, 'json', ['groups' => 'review:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('/{id}', name: 'api_reviews_show', methods: ['GET'])]
    public function show(int $id): JsonResponse
    {
        $review = $this->reviewService->find($id);
        if (!$review) {
            return new JsonResponse(['error' => 'Review not found'], 404);
        }

        $data = $this->serializer->serialize($review, 'json', ['groups' => 'review:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('', name: 'api_reviews_create', methods: ['POST'])]
    public function create(Request $request): JsonResponse
    {
        $data = json_decode($request->getContent(), true);
        
        $place = $this->placeService->find($data['placeId'] ?? 0);
        $user = $this->userService->find($data['userId'] ?? 0);

        if (!$place || !$user) {
            return new JsonResponse(['error' => 'Place or User not found'], 404);
        }

        $review = new Review();
        $review->setRating($data['rating'] ?? 0);
        $review->setComment($data['comment'] ?? null);
        $review->setPlace($place);
        $review->setUser($user);

        try {
            $review = $this->reviewService->create($review);
            $response = $this->serializer->serialize($review, 'json', ['groups' => 'review:read']);
            return new JsonResponse(json_decode($response, true), 201);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => $e->getMessage()], 400);
        }
    }

    #[Route('/{id}', name: 'api_reviews_update', methods: ['PUT'])]
    public function update(int $id, Request $request): JsonResponse
    {
        $review = $this->reviewService->find($id);
        if (!$review) {
            return new JsonResponse(['error' => 'Review not found'], 404);
        }

        $data = json_decode($request->getContent(), true);
        if (isset($data['rating'])) $review->setRating($data['rating']);
        if (isset($data['comment'])) $review->setComment($data['comment']);

        try {
            $review = $this->reviewService->update($review);
            $response = $this->serializer->serialize($review, 'json', ['groups' => 'review:read']);
            return new JsonResponse(json_decode($response, true), 200);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => $e->getMessage()], 400);
        }
    }

    #[Route('/{id}', name: 'api_reviews_delete', methods: ['DELETE'])]
    public function delete(int $id): JsonResponse
    {
        $review = $this->reviewService->find($id);
        if (!$review) {
            return new JsonResponse(['error' => 'Review not found'], 404);
        }

        try {
            $this->reviewService->delete($review);
            return new JsonResponse(['message' => 'Review deleted successfully'], 200);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => $e->getMessage()], 400);
        }
    }

    #[Route('/place/{placeId}', name: 'api_reviews_by_place', methods: ['GET'])]
    public function getByPlace(int $placeId): JsonResponse
    {
        $place = $this->placeService->find($placeId);
        if (!$place) {
            return new JsonResponse(['error' => 'Place not found'], 404);
        }

        $reviews = $this->reviewService->findByPlace($place);
        $data = $this->serializer->serialize($reviews, 'json', ['groups' => 'review:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('/user/{userId}', name: 'api_reviews_by_user', methods: ['GET'])]
    public function getByUser(int $userId): JsonResponse
    {
        $user = $this->userService->find($userId);
        if (!$user) {
            return new JsonResponse(['error' => 'User not found'], 404);
        }

        $reviews = $this->reviewService->findByUser($user);
        $data = $this->serializer->serialize($reviews, 'json', ['groups' => 'review:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }
}

