<?php

namespace App\Controller\Api;

use App\Entity\User;
use App\Service\UserService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Serializer\SerializerInterface;

#[Route('/api/users')]
class UserController extends AbstractController
{
    public function __construct(
        private UserService $userService,
        private SerializerInterface $serializer
    ) {
    }

    #[Route('', name: 'api_users_list', methods: ['GET'])]
    public function index(): JsonResponse
    {
        $users = $this->userService->findAll();
        $data = $this->serializer->serialize($users, 'json', ['groups' => 'user:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('/{id}', name: 'api_users_show', methods: ['GET'])]
    public function show(int $id): JsonResponse
    {
        $user = $this->userService->find($id);
        if (!$user) {
            return new JsonResponse(['error' => 'User not found'], 404);
        }

        $data = $this->serializer->serialize($user, 'json', ['groups' => 'user:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('', name: 'api_users_create', methods: ['POST'])]
    public function create(Request $request): JsonResponse
    {
        $data = json_decode($request->getContent(), true);
        $user = new User();
        $user->setEmail($data['email'] ?? '');
        $user->setUsername($data['username'] ?? '');
        $user->setRole($data['role'] ?? 'ROLE_USER');
        $user->setPhoneNumber($data['phoneNumber'] ?? '');
        $user->setRoles([$data['role'] ?? 'ROLE_USER']);

        try {
            $user = $this->userService->create($user, $data['password'] ?? null);
            $response = $this->serializer->serialize($user, 'json', ['groups' => 'user:read']);
            return new JsonResponse(json_decode($response, true), 201);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => $e->getMessage()], 400);
        }
    }

    #[Route('/{id}', name: 'api_users_update', methods: ['PUT'])]
    public function update(int $id, Request $request): JsonResponse
    {
        $user = $this->userService->find($id);
        if (!$user) {
            return new JsonResponse(['error' => 'User not found'], 404);
        }

        $data = json_decode($request->getContent(), true);
        if (isset($data['email'])) $user->setEmail($data['email']);
        if (isset($data['username'])) $user->setUsername($data['username']);
        if (isset($data['phoneNumber'])) $user->setPhoneNumber($data['phoneNumber']);

        try {
            $user = $this->userService->update($user);
            $response = $this->serializer->serialize($user, 'json', ['groups' => 'user:read']);
            return new JsonResponse(json_decode($response, true), 200);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => $e->getMessage()], 400);
        }
    }

    #[Route('/{id}', name: 'api_users_delete', methods: ['DELETE'])]
    public function delete(int $id): JsonResponse
    {
        $user = $this->userService->find($id);
        if (!$user) {
            return new JsonResponse(['error' => 'User not found'], 404);
        }

        try {
            $this->userService->delete($user);
            return new JsonResponse(['message' => 'User deleted successfully'], 200);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => $e->getMessage()], 400);
        }
    }

    #[Route('/{id}/reviews', name: 'api_users_reviews', methods: ['GET'])]
    public function getUserReviews(int $id): JsonResponse
    {
        $user = $this->userService->find($id);
        if (!$user) {
            return new JsonResponse(['error' => 'User not found'], 404);
        }

        $reviews = $this->userService->getUserReviews($user);
        $data = $this->serializer->serialize($reviews, 'json', ['groups' => 'review:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('/{id}/reservations', name: 'api_users_reservations', methods: ['GET'])]
    public function getUserReservations(int $id): JsonResponse
    {
        $user = $this->userService->find($id);
        if (!$user) {
            return new JsonResponse(['error' => 'User not found'], 404);
        }

        $reservations = $this->userService->getUserReservations($user);
        $data = $this->serializer->serialize($reservations, 'json', ['groups' => 'reservation:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }
}

