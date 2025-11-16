<?php

namespace App\Controller;

use App\Entity\TourPersonnalise;
use App\Form\TourPersonnaliseType;
use App\Repository\TourPersonnaliseRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/tour/personnalise')]
final class TourPersonnaliseController extends AbstractController
{
    #[Route(name: 'app_tour_personnalise_index', methods: ['GET'])]
    public function index(TourPersonnaliseRepository $tourPersonnaliseRepository): Response
    {
        return $this->render('tour_personnalise/index.html.twig', [
            'tour_personnalises' => $tourPersonnaliseRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_tour_personnalise_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $tourPersonnalise = new TourPersonnalise();
        $form = $this->createForm(TourPersonnaliseType::class, $tourPersonnalise);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($tourPersonnalise);
            $entityManager->flush();

            return $this->redirectToRoute('app_tour_personnalise_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('tour_personnalise/new.html.twig', [
            'tour_personnalise' => $tourPersonnalise,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_tour_personnalise_show', methods: ['GET'])]
    public function show(TourPersonnalise $tourPersonnalise): Response
    {
        return $this->render('tour_personnalise/show.html.twig', [
            'tour_personnalise' => $tourPersonnalise,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_tour_personnalise_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, TourPersonnalise $tourPersonnalise, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(TourPersonnaliseType::class, $tourPersonnalise);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_tour_personnalise_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('tour_personnalise/edit.html.twig', [
            'tour_personnalise' => $tourPersonnalise,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_tour_personnalise_delete', methods: ['POST'])]
    public function delete(Request $request, TourPersonnalise $tourPersonnalise, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$tourPersonnalise->getId(), $request->getPayload()->getString('_token'))) {
            $entityManager->remove($tourPersonnalise);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_tour_personnalise_index', [], Response::HTTP_SEE_OTHER);
    }
}
