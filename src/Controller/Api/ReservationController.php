<?php

namespace App\Controller\Api;

use App\Entity\Reservation;
use App\Entity\Place;
use App\Entity\User;
use App\Service\ReservationService;
use App\Service\PlaceService;
use App\Service\UserService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Serializer\SerializerInterface;

#[Route('/api/reservations')]
class ReservationController extends AbstractController
{
    public function __construct(
        private ReservationService $reservationService,
        private PlaceService $placeService,
        private UserService $userService,
        private SerializerInterface $serializer
    ) {
    }

    #[Route('', name: 'api_reservations_list', methods: ['GET'])]
    public function index(): JsonResponse
    {
        $reservations = $this->reservationService->findAll();
        $data = $this->serializer->serialize($reservations, 'json', ['groups' => 'reservation:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('/{id}', name: 'api_reservations_show', methods: ['GET'])]
    public function show(int $id): JsonResponse
    {
        $reservation = $this->reservationService->find($id);
        if (!$reservation) {
            return new JsonResponse(['error' => 'Reservation not found'], 404);
        }

        $data = $this->serializer->serialize($reservation, 'json', ['groups' => 'reservation:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('', name: 'api_reservations_create', methods: ['POST'])]
    public function create(Request $request): JsonResponse
    {
        $data = json_decode($request->getContent(), true);
        
        $place = $this->placeService->find($data['placeId'] ?? 0);
        $user = $this->userService->find($data['userId'] ?? 0);

        if (!$place || !$user) {
            return new JsonResponse(['error' => 'Place or User not found'], 404);
        }

        $reservation = new Reservation();
        $reservation->setReservationDate(new \DateTime($data['reservationDate'] ?? 'now'));
        $reservation->setStatus($data['status'] ?? 'PENDING');
        $reservation->setNumbersOfPersons($data['numbersOfPersons'] ?? 1);
        $reservation->setTotalPrice($data['totalPrice'] ?? null);
        $reservation->setPlace($place);
        $reservation->setUser($user);

        try {
            $reservation = $this->reservationService->create($reservation);
            $response = $this->serializer->serialize($reservation, 'json', ['groups' => 'reservation:read']);
            return new JsonResponse(json_decode($response, true), 201);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => $e->getMessage()], 400);
        }
    }

    #[Route('/{id}', name: 'api_reservations_update', methods: ['PUT'])]
    public function update(int $id, Request $request): JsonResponse
    {
        $reservation = $this->reservationService->find($id);
        if (!$reservation) {
            return new JsonResponse(['error' => 'Reservation not found'], 404);
        }

        $data = json_decode($request->getContent(), true);
        if (isset($data['reservationDate'])) {
            $reservation->setReservationDate(new \DateTime($data['reservationDate']));
        }
        if (isset($data['status'])) $reservation->setStatus($data['status']);
        if (isset($data['numbersOfPersons'])) $reservation->setNumbersOfPersons($data['numbersOfPersons']);
        if (isset($data['totalPrice'])) $reservation->setTotalPrice($data['totalPrice']);

        try {
            $reservation = $this->reservationService->update($reservation);
            $response = $this->serializer->serialize($reservation, 'json', ['groups' => 'reservation:read']);
            return new JsonResponse(json_decode($response, true), 200);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => $e->getMessage()], 400);
        }
    }

    #[Route('/{id}', name: 'api_reservations_delete', methods: ['DELETE'])]
    public function delete(int $id): JsonResponse
    {
        $reservation = $this->reservationService->find($id);
        if (!$reservation) {
            return new JsonResponse(['error' => 'Reservation not found'], 404);
        }

        try {
            $this->reservationService->delete($reservation);
            return new JsonResponse(['message' => 'Reservation deleted successfully'], 200);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => $e->getMessage()], 400);
        }
    }

    #[Route('/user/{userId}', name: 'api_reservations_by_user', methods: ['GET'])]
    public function getByUser(int $userId): JsonResponse
    {
        $user = $this->userService->find($userId);
        if (!$user) {
            return new JsonResponse(['error' => 'User not found'], 404);
        }

        $reservations = $this->reservationService->findByUser($user);
        $data = $this->serializer->serialize($reservations, 'json', ['groups' => 'reservation:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('/status/{status}', name: 'api_reservations_by_status', methods: ['GET'])]
    public function getByStatus(string $status): JsonResponse
    {
        $reservations = $this->reservationService->findByStatus($status);
        $data = $this->serializer->serialize($reservations, 'json', ['groups' => 'reservation:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }
}

