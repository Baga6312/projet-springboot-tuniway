<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;

class LoginController extends AbstractController
{
    #[Route('/login', name: 'app_login')]
    public function login(AuthenticationUtils $authenticationUtils): JsonResponse
    {
        // Get login error if there is one
        $error = $authenticationUtils->getLastAuthenticationError();

        // Last username entered
        $lastUsername = $authenticationUtils->getLastUsername();

        $status = $error ? Response::HTTP_UNAUTHORIZED : Response::HTTP_OK;

        return $this->json([
            'last_username' => $lastUsername,
            'error' => $error?->getMessageKey(),
            'message' => $error
                ? 'Authentication failed'
                : 'Provide your credentials to authenticate via the configured firewall.',
        ], $status);
    }

    #[Route('/logout', name: 'app_logout')]
    public function logout(): void
    {
        // This method can be blank - it will be intercepted by logout key on firewall
    }
}
