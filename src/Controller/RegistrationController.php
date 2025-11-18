<?php

namespace App\Controller;

use App\Entity\User;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Routing\Attribute\Route;

class RegistrationController extends AbstractController
{
    #[Route('/register', name: 'app_register', methods: ['POST'])]
    public function register(
        Request $request,
        UserPasswordHasherInterface $userPasswordHasher,
        EntityManagerInterface $entityManager
    ): JsonResponse
    {
        try {
            $data = $request->toArray();
        } catch (\JsonException) {
            return $this->json(['error' => 'Invalid JSON payload'], Response::HTTP_BAD_REQUEST);
        }

        $required = ['email', 'username', 'plainPassword', 'phoneNumber'];
        foreach ($required as $field) {
            if (empty($data[$field])) {
                return $this->json(['error' => sprintf('Field "%s" is required', $field)], Response::HTTP_BAD_REQUEST);
            }
        }

        $user = new User();
        $user->setEmail($data['email']);
        $user->setUsername($data['username']);
        $user->setPhoneNumber($data['phoneNumber']);
        $user->setRole($data['role'] ?? 'ROLE_USER');
        $user->setRoles($data['roles'] ?? [$user->getRole()]);
        $user->setCreatedAt(new \DateTimeImmutable());
        $user->setPassword(
            $userPasswordHasher->hashPassword($user, $data['plainPassword'])
        );

        $entityManager->persist($user);
        $entityManager->flush();

        return $this->json([
            'message' => 'User registered successfully',
            'user' => $user,
        ], Response::HTTP_CREATED, [], ['groups' => 'user:read']);
    }
}
