<?php

namespace App\Controller;

use App\Entity\User;
use App\Service\JwtService;
use App\Service\UserService;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Routing\Attribute\Route;

class RegistrationController extends AbstractController
{
    public function __construct(
        private UserService $userService,
        private JwtService $jwtService
    ) {
    }

    #[Route('/api/register', name: 'app_register', methods: ['POST'])]
    public function register(
        Request $request,
        UserPasswordHasherInterface $userPasswordHasher,
        EntityManagerInterface $entityManager
    ): JsonResponse
    {
        try {
            $data = $request->toArray();
        } catch (\JsonException) {
            return $this->json([
                'success' => false,
                'message' => 'Invalid JSON payload'
            ], Response::HTTP_BAD_REQUEST);
        }

        $required = ['email', 'username', 'plainPassword'];
        foreach ($required as $field) {
            if (empty($data[$field])) {
                return $this->json([
                    'success' => false,
                    'message' => sprintf('Field "%s" is required', $field)
                ], Response::HTTP_BAD_REQUEST);
            }
        }

        // Check if user already exists
        $existingUser = $this->userService->findByEmail($data['email']);
        if ($existingUser) {
            return $this->json([
                'success' => false,
                'message' => 'User with this email already exists'
            ], Response::HTTP_CONFLICT);
        }

        // Always create User with role = CLIENT
        $user = new User();
        $user->setEmail($data['email']);
        $user->setUsername($data['username']);
        $user->setRole('CLIENT');
        $user->setRoles(['CLIENT']);
        $user->setPassword(
            $userPasswordHasher->hashPassword($user, $data['plainPassword'])
        );

        $entityManager->persist($user);
        $entityManager->flush();

        // Generate JWT token after successful registration
        $token = $this->jwtService->generateToken([
            'email' => $user->getEmail(),
            'id' => $user->getId(),
            'roles' => $user->getRoles()
        ]);

        return $this->json([
            'success' => true,
            'message' => 'User registered successfully',
            'token' => $token,
            'user' => [
                'id' => $user->getId(),
                'email' => $user->getEmail(),
                'username' => $user->getUsername(),
                'role' => $user->getRole(),
                'roles' => $user->getRoles()
            ]
        ], Response::HTTP_CREATED, [], ['groups' => 'user:read']);
    }
}
