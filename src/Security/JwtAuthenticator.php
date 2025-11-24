<?php

namespace App\Security;

use App\Service\JwtService;
use App\Service\UserService;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Security\Core\Authentication\Token\TokenInterface;
use Symfony\Component\Security\Core\Exception\AuthenticationException;
use Symfony\Component\Security\Http\Authenticator\AbstractAuthenticator;
use Symfony\Component\Security\Http\Authenticator\Passport\Badge\UserBadge;
use Symfony\Component\Security\Http\Authenticator\Passport\Passport;
use Symfony\Component\Security\Http\Authenticator\Passport\SelfValidatingPassport;
use Symfony\Component\Security\Http\EntryPoint\AuthenticationEntryPointInterface;

class JwtAuthenticator extends AbstractAuthenticator implements AuthenticationEntryPointInterface
{
    public function __construct(
        private JwtService $jwtService,
        private UserService $userService
    ) {
    }

    public function supports(Request $request): ?bool
    {
        // Only authenticate API routes (excluding login and register)
        $path = $request->getPathInfo();
        if (str_starts_with($path, '/api/login') || str_starts_with($path, '/api/register')) {
            return false;
        }

        // Only support if Authorization header is present
        return $request->headers->has('Authorization');
    }

    public function authenticate(Request $request): Passport
    {
        $authHeader = $request->headers->get('Authorization');

        if (!$authHeader) {
            throw new AuthenticationException('No authorization header provided');
        }

        if (!str_starts_with($authHeader, 'Bearer ')) {
            throw new AuthenticationException('Invalid authorization header format. Expected: Bearer <token>');
        }

        $token = substr($authHeader, 7);

        if (empty($token)) {
            throw new AuthenticationException('Token is empty');
        }

        $payload = $this->jwtService->validateToken($token);

        if (!$payload) {
            throw new AuthenticationException('Invalid or expired token');
        }

        if (!isset($payload['email'])) {
            throw new AuthenticationException('Token payload missing email');
        }

        $user = $this->userService->findByEmail($payload['email']);

        if (!$user) {
            throw new AuthenticationException('User not found');
        }

        return new SelfValidatingPassport(
            new UserBadge($payload['email'], function($email) {
                return $this->userService->findByEmail($email);
            })
        );
    }

    public function onAuthenticationSuccess(Request $request, TokenInterface $token, string $firewallName): ?Response
    {
        return null; // Let the request continue
    }

    public function onAuthenticationFailure(Request $request, AuthenticationException $exception): ?Response
    {
        return new JsonResponse([
            'success' => false,
            'message' => $exception->getMessage()
        ], Response::HTTP_UNAUTHORIZED);
    }

    public function start(Request $request, AuthenticationException $authException = null): Response
    {
        return new JsonResponse([
            'success' => false,
            'message' => 'Authentication required. Please provide a valid JWT token in the Authorization header.'
        ], Response::HTTP_UNAUTHORIZED);
    }
}
