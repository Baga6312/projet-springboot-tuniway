<?php

namespace App\Controller;

use App\Entity\Place;
use App\Entity\Reservation;
use App\Entity\TourPersonnalise;
use App\Entity\User;
use App\Service\ReservationService;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/reservation')]
final class ReservationController extends AbstractController
{
    public function __construct(
        private ReservationService $reservationService,
        private EntityManagerInterface $entityManager
    ) {
    }

    #[Route(name: 'app_reservation_index', methods: ['GET'])]
    public function index(): JsonResponse
    {
        return $this->json(
            $this->reservationService->findAll(),
            Response::HTTP_OK,
            [],
            ['groups' => 'reservation:read']
        );
    }

    #[Route('/new', name: 'app_reservation_new', methods: ['POST'])]
    public function new(Request $request): JsonResponse
    {
        $data = $this->parsePayload($request);
        if ($data === null) {
            return $this->json(['error' => 'Invalid JSON payload'], Response::HTTP_BAD_REQUEST);
        }

        $required = ['reservationDate', 'status', 'numbersOfPersons', 'userId', 'placeId'];
        foreach ($required as $field) {
            if (empty($data[$field])) {
                return $this->json(['error' => sprintf('Field "%s" is required', $field)], Response::HTTP_BAD_REQUEST);
            }
        }

        try {
            $reservationDate = new \DateTime($data['reservationDate']);
        } catch (\Exception) {
            return $this->json(['error' => 'Invalid "reservationDate" format'], Response::HTTP_BAD_REQUEST);
        }

        $user = $this->entityManager->getRepository(User::class)->find($data['userId']);
        $place = $this->entityManager->getRepository(Place::class)->find($data['placeId']);

        if (!$user || !$place) {
            return $this->json(['error' => 'User or Place not found'], Response::HTTP_NOT_FOUND);
        }

        $reservation = new Reservation();
        $reservation->setReservationDate($reservationDate);
        $reservation->setStatus($data['status']);
        $reservation->setNumbersOfPersons((int) $data['numbersOfPersons']);
        $reservation->setTotalPrice(isset($data['totalPrice']) ? (float) $data['totalPrice'] : null);
        $reservation->setType($data['type'] ?? 'PLACE');
        $reservation->setCreatedAt(new \DateTime());
        $reservation->setUser($user);
        $reservation->setPlace($place);

        if (!empty($data['tourId'])) {
            $tour = $this->entityManager->getRepository(TourPersonnalise::class)->find($data['tourId']);
            if (!$tour) {
                return $this->json(['error' => 'Tour not found'], Response::HTTP_NOT_FOUND);
            }
            $reservation->setTour($tour);
        }

        $reservation = $this->reservationService->create($reservation);

        return $this->json($reservation, Response::HTTP_CREATED, [], ['groups' => 'reservation:read']);
    }

    #[Route('/{id}', name: 'app_reservation_show', methods: ['GET'])]
    public function show(int $id): JsonResponse
    {
        $reservation = $this->reservationService->find($id);
        if (!$reservation) {
            return $this->json(['error' => 'Reservation not found'], Response::HTTP_NOT_FOUND);
        }

        return $this->json($reservation, Response::HTTP_OK, [], ['groups' => 'reservation:read']);
    }

    #[Route('/{id}/edit', name: 'app_reservation_edit', methods: ['PUT'])]
    public function edit(int $id, Request $request): JsonResponse
    {
        $reservation = $this->reservationService->find($id);
        if (!$reservation) {
            return $this->json(['error' => 'Reservation not found'], Response::HTTP_NOT_FOUND);
        }

        $data = $this->parsePayload($request);
        if ($data === null) {
            return $this->json(['error' => 'Invalid JSON payload'], Response::HTTP_BAD_REQUEST);
        }

        if (isset($data['reservationDate'])) {
            try {
                $reservation->setReservationDate(new \DateTime($data['reservationDate']));
            } catch (\Exception) {
                return $this->json(['error' => 'Invalid "reservationDate" format'], Response::HTTP_BAD_REQUEST);
            }
        }

        if (isset($data['status'])) {
            $reservation->setStatus($data['status']);
        }

        if (isset($data['numbersOfPersons'])) {
            $reservation->setNumbersOfPersons((int) $data['numbersOfPersons']);
        }

        if (array_key_exists('totalPrice', $data)) {
            $reservation->setTotalPrice($data['totalPrice'] !== null ? (float) $data['totalPrice'] : null);
        }

        if (isset($data['type'])) {
            $reservation->setType($data['type']);
        }

        if (isset($data['userId'])) {
            $user = $this->entityManager->getRepository(User::class)->find($data['userId']);
            if (!$user) {
                return $this->json(['error' => 'User not found'], Response::HTTP_NOT_FOUND);
            }
            $reservation->setUser($user);
        }

        if (isset($data['placeId'])) {
            $place = $this->entityManager->getRepository(Place::class)->find($data['placeId']);
            if (!$place) {
                return $this->json(['error' => 'Place not found'], Response::HTTP_NOT_FOUND);
            }
            $reservation->setPlace($place);
        }

        if (array_key_exists('tourId', $data)) {
            if ($data['tourId'] === null) {
                $reservation->setTour(null);
            } else {
                $tour = $this->entityManager->getRepository(TourPersonnalise::class)->find($data['tourId']);
                if (!$tour) {
                    return $this->json(['error' => 'Tour not found'], Response::HTTP_NOT_FOUND);
                }
                $reservation->setTour($tour);
            }
        }

        $reservation = $this->reservationService->update($reservation);

        return $this->json($reservation, Response::HTTP_OK, [], ['groups' => 'reservation:read']);
    }

    #[Route('/{id}', name: 'app_reservation_delete', methods: ['DELETE'])]
    public function delete(int $id): JsonResponse
    {
        $reservation = $this->reservationService->find($id);
        if (!$reservation) {
            return $this->json(['error' => 'Reservation not found'], Response::HTTP_NOT_FOUND);
        }

        $this->reservationService->delete($reservation);

        return $this->json(['message' => 'Reservation deleted successfully'], Response::HTTP_OK);
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
