<?php

namespace App\Controller;

use App\Entity\Place;
use App\Entity\Review;
use App\Entity\User;
use App\Service\ReviewService;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/review')]
final class ReviewController extends AbstractController
{
    public function __construct(
        private ReviewService $reviewService,
        private EntityManagerInterface $entityManager
    ) {
    }

    #[Route(name: 'app_review_index', methods: ['GET'])]
    public function index(): JsonResponse
    {
        return $this->json(
            $this->reviewService->findAll(),
            Response::HTTP_OK,
            [],
            ['groups' => 'review:read']
        );
    }

    #[Route('/new', name: 'app_review_new', methods: ['POST'])]
    public function new(Request $request): JsonResponse
    {
        $data = $this->parsePayload($request);
        if ($data === null) {
            return $this->json(['error' => 'Invalid JSON payload'], Response::HTTP_BAD_REQUEST);
        }

        $required = ['rating', 'userId', 'placeId'];
        foreach ($required as $field) {
            if (!isset($data[$field])) {
                return $this->json(['error' => sprintf('Field "%s" is required', $field)], Response::HTTP_BAD_REQUEST);
            }
        }

        $user = $this->entityManager->getRepository(User::class)->find($data['userId']);
        $place = $this->entityManager->getRepository(Place::class)->find($data['placeId']);

        if (!$user || !$place) {
            return $this->json(['error' => 'User or Place not found'], Response::HTTP_NOT_FOUND);
        }

        $review = new Review();
        $review->setRating((int) $data['rating']);
        $review->setComment($data['comment'] ?? null);
        $review->setCreatedAt(new \DateTimeImmutable());
        $review->setUser($user);
        $review->setPlace($place);

        $review = $this->reviewService->create($review);

        return $this->json($review, Response::HTTP_CREATED, [], ['groups' => 'review:read']);
    }

    #[Route('/{id}', name: 'app_review_show', methods: ['GET'])]
    public function show(int $id): JsonResponse
    {
        $review = $this->reviewService->find($id);
        if (!$review) {
            return $this->json(['error' => 'Review not found'], Response::HTTP_NOT_FOUND);
        }

        return $this->json($review, Response::HTTP_OK, [], ['groups' => 'review:read']);
    }

    #[Route('/{id}/edit', name: 'app_review_edit', methods: ['PUT'])]
    public function edit(int $id, Request $request): JsonResponse
    {
        $review = $this->reviewService->find($id);
        if (!$review) {
            return $this->json(['error' => 'Review not found'], Response::HTTP_NOT_FOUND);
        }

        $data = $this->parsePayload($request);
        if ($data === null) {
            return $this->json(['error' => 'Invalid JSON payload'], Response::HTTP_BAD_REQUEST);
        }

        if (isset($data['rating'])) {
            $review->setRating((int) $data['rating']);
        }

        if (array_key_exists('comment', $data)) {
            $review->setComment($data['comment']);
        }

        if (isset($data['userId'])) {
            $user = $this->entityManager->getRepository(User::class)->find($data['userId']);
            if (!$user) {
                return $this->json(['error' => 'User not found'], Response::HTTP_NOT_FOUND);
            }
            $review->setUser($user);
        }

        if (isset($data['placeId'])) {
            $place = $this->entityManager->getRepository(Place::class)->find($data['placeId']);
            if (!$place) {
                return $this->json(['error' => 'Place not found'], Response::HTTP_NOT_FOUND);
            }
            $review->setPlace($place);
        }

        $review = $this->reviewService->update($review);

        return $this->json($review, Response::HTTP_OK, [], ['groups' => 'review:read']);
    }

    #[Route('/{id}', name: 'app_review_delete', methods: ['DELETE'])]
    public function delete(int $id): JsonResponse
    {
        $review = $this->reviewService->find($id);
        if (!$review) {
            return $this->json(['error' => 'Review not found'], Response::HTTP_NOT_FOUND);
        }

        $this->reviewService->delete($review);

        return $this->json(['message' => 'Review deleted successfully'], Response::HTTP_OK);
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