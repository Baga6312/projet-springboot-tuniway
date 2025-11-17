<?php

namespace App\Controller\Api;

use App\Entity\TourPersonnalise;
use App\Entity\User;
use App\Service\TourPersonnaliseService;
use App\Service\UserService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Serializer\SerializerInterface;

#[Route('/api/tours')]
class TourPersonnaliseController extends AbstractController
{
    public function __construct(
        private TourPersonnaliseService $tourService,
        private UserService $userService,
        private SerializerInterface $serializer
    ) {
    }

    #[Route('', name: 'api_tours_list', methods: ['GET'])]
    public function index(): JsonResponse
    {
        $tours = $this->tourService->findAll();
        $data = $this->serializer->serialize($tours, 'json', ['groups' => 'tour:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('/{id}', name: 'api_tours_show', methods: ['GET'])]
    public function show(int $id): JsonResponse
    {
        $tour = $this->tourService->find($id);
        if (!$tour) {
            return new JsonResponse(['error' => 'Tour not found'], 404);
        }

        $data = $this->serializer->serialize($tour, 'json', ['groups' => 'tour:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('', name: 'api_tours_create', methods: ['POST'])]
    public function create(Request $request): JsonResponse
    {
        $data = json_decode($request->getContent(), true);
        
        $guide = $this->userService->find($data['guideId'] ?? 0);
        $client = $this->userService->find($data['clientId'] ?? 0);

        if (!$guide || !$client) {
            return new JsonResponse(['error' => 'Guide or Client not found'], 404);
        }

        $tour = new TourPersonnalise();
        $tour->setTitle($data['title'] ?? '');
        $tour->setDescription($data['description'] ?? null);
        $tour->setDuration($data['duration'] ?? 0);
        $tour->setPrice($data['price'] ?? 0.0);
        $tour->setMaxPersons($data['maxPersons'] ?? 1);
        $tour->setGuide($guide);
        $tour->setClient($client);

        try {
            $tour = $this->tourService->create($tour);
            $response = $this->serializer->serialize($tour, 'json', ['groups' => 'tour:read']);
            return new JsonResponse(json_decode($response, true), 201);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => $e->getMessage()], 400);
        }
    }

    #[Route('/{id}', name: 'api_tours_update', methods: ['PUT'])]
    public function update(int $id, Request $request): JsonResponse
    {
        $tour = $this->tourService->find($id);
        if (!$tour) {
            return new JsonResponse(['error' => 'Tour not found'], 404);
        }

        $data = json_decode($request->getContent(), true);
        if (isset($data['title'])) $tour->setTitle($data['title']);
        if (isset($data['description'])) $tour->setDescription($data['description']);
        if (isset($data['duration'])) $tour->setDuration($data['duration']);
        if (isset($data['price'])) $tour->setPrice($data['price']);
        if (isset($data['maxPersons'])) $tour->setMaxPersons($data['maxPersons']);

        try {
            $tour = $this->tourService->update($tour);
            $response = $this->serializer->serialize($tour, 'json', ['groups' => 'tour:read']);
            return new JsonResponse(json_decode($response, true), 200);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => $e->getMessage()], 400);
        }
    }

    #[Route('/{id}', name: 'api_tours_delete', methods: ['DELETE'])]
    public function delete(int $id): JsonResponse
    {
        $tour = $this->tourService->find($id);
        if (!$tour) {
            return new JsonResponse(['error' => 'Tour not found'], 404);
        }

        try {
            $this->tourService->delete($tour);
            return new JsonResponse(['message' => 'Tour deleted successfully'], 200);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => $e->getMessage()], 400);
        }
    }

    #[Route('/guide/{guideId}', name: 'api_tours_by_guide', methods: ['GET'])]
    public function getByGuide(int $guideId): JsonResponse
    {
        $guide = $this->userService->find($guideId);
        if (!$guide) {
            return new JsonResponse(['error' => 'Guide not found'], 404);
        }

        $tours = $this->tourService->findByGuide($guide);
        $data = $this->serializer->serialize($tours, 'json', ['groups' => 'tour:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }

    #[Route('/client/{clientId}', name: 'api_tours_by_client', methods: ['GET'])]
    public function getByClient(int $clientId): JsonResponse
    {
        $client = $this->userService->find($clientId);
        if (!$client) {
            return new JsonResponse(['error' => 'Client not found'], 404);
        }

        $tours = $this->tourService->findByClient($client);
        $data = $this->serializer->serialize($tours, 'json', ['groups' => 'tour:read']);
        return new JsonResponse(json_decode($data, true), 200);
    }
}

