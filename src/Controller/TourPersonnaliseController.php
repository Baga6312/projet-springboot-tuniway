<?php

namespace App\Controller;

use App\Entity\TourPersonnalise;
use App\Entity\User;
use App\Service\TourPersonnaliseService;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/tour/personnalise')]
final class TourPersonnaliseController extends AbstractController
{
    public function __construct(
        private TourPersonnaliseService $tourService,
        private EntityManagerInterface $entityManager
    ) {
    }

    #[Route(name: 'app_tour_personnalise_index', methods: ['GET'])]
    public function index(): JsonResponse
    {
        return $this->json(
            $this->tourService->findAll(),
            Response::HTTP_OK,
            [],
            ['groups' => 'tour:read']
        );
    }

    #[Route('/new', name: 'app_tour_personnalise_new', methods: ['POST'])]
    public function new(Request $request): JsonResponse
    {
        $data = $this->parsePayload($request);
        if ($data === null) {
            return $this->json(['error' => 'Invalid JSON payload'], Response::HTTP_BAD_REQUEST);
        }

        $required = ['title', 'duration', 'price', 'maxPersons', 'guideId', 'clientId'];
        foreach ($required as $field) {
            if (!isset($data[$field])) {
                return $this->json(['error' => sprintf('Field "%s" is required', $field)], Response::HTTP_BAD_REQUEST);
            }
        }

        $guide = $this->entityManager->getRepository(User::class)->find($data['guideId']);
        $client = $this->entityManager->getRepository(User::class)->find($data['clientId']);

        if (!$guide || !$client) {
            return $this->json(['error' => 'Guide or Client not found'], Response::HTTP_NOT_FOUND);
        }

        $tour = new TourPersonnalise();
        $tour->setTitle($data['title']);
        $tour->setDescription($data['description'] ?? null);
        $tour->setDuration((int) $data['duration']);
        $tour->setPrice((float) $data['price']);
        $tour->setMaxPersons((int) $data['maxPersons']);
        $tour->setCreatedAt(new \DateTimeImmutable());
        $tour->setGuide($guide);
        $tour->setClient($client);

        $tour = $this->tourService->create($tour);

        return $this->json($tour, Response::HTTP_CREATED, [], ['groups' => 'tour:read']);
    }

    #[Route('/{id}', name: 'app_tour_personnalise_show', methods: ['GET'])]
    public function show(int $id): JsonResponse
    {
        $tour = $this->tourService->find($id);
        if (!$tour) {
            return $this->json(['error' => 'Tour not found'], Response::HTTP_NOT_FOUND);
        }

        return $this->json($tour, Response::HTTP_OK, [], ['groups' => 'tour:read']);
    }

    #[Route('/{id}/edit', name: 'app_tour_personnalise_edit', methods: ['PUT'])]
    public function edit(int $id, Request $request): JsonResponse
    {
        $tour = $this->tourService->find($id);
        if (!$tour) {
            return $this->json(['error' => 'Tour not found'], Response::HTTP_NOT_FOUND);
        }

        $data = $this->parsePayload($request);
        if ($data === null) {
            return $this->json(['error' => 'Invalid JSON payload'], Response::HTTP_BAD_REQUEST);
        }

        if (isset($data['title'])) {
            $tour->setTitle($data['title']);
        }
        if (array_key_exists('description', $data)) {
            $tour->setDescription($data['description']);
        }
        if (isset($data['duration'])) {
            $tour->setDuration((int) $data['duration']);
        }
        if (isset($data['price'])) {
            $tour->setPrice((float) $data['price']);
        }
        if (isset($data['maxPersons'])) {
            $tour->setMaxPersons((int) $data['maxPersons']);
        }
        if (isset($data['guideId'])) {
            $guide = $this->entityManager->getRepository(User::class)->find($data['guideId']);
            if (!$guide) {
                return $this->json(['error' => 'Guide not found'], Response::HTTP_NOT_FOUND);
            }
            $tour->setGuide($guide);
        }
        if (isset($data['clientId'])) {
            $client = $this->entityManager->getRepository(User::class)->find($data['clientId']);
            if (!$client) {
                return $this->json(['error' => 'Client not found'], Response::HTTP_NOT_FOUND);
            }
            $tour->setClient($client);
        }

        $tour = $this->tourService->update($tour);

        return $this->json($tour, Response::HTTP_OK, [], ['groups' => 'tour:read']);
    }

    #[Route('/{id}', name: 'app_tour_personnalise_delete', methods: ['DELETE'])]
    public function delete(int $id): JsonResponse
    {
        $tour = $this->tourService->find($id);
        if (!$tour) {
            return $this->json(['error' => 'Tour not found'], Response::HTTP_NOT_FOUND);
        }

        $this->tourService->delete($tour);

        return $this->json(['message' => 'Tour deleted successfully'], Response::HTTP_OK);
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