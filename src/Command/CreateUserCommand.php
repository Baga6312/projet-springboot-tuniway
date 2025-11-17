<?php

namespace App\Command;

use App\Entity\User;
use App\Entity\Client;
use App\Entity\Guide;
use App\Entity\Admin;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\Console\Attribute\AsCommand;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;
use Symfony\Component\Console\Question\Question;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;

#[AsCommand(name: 'app:create-user')]
class CreateUserCommand extends Command
{
    public function __construct(
        private EntityManagerInterface $entityManager,
        private UserPasswordHasherInterface $passwordHasher
    ) {
        parent::__construct();
    }

    protected function execute(InputInterface $input, OutputInterface $output): int
    {
        $helper = $this->getHelper('question');

        $emailQuestion = new Question('Email: ');
        $email = $helper->ask($input, $output, $emailQuestion);

        $usernameQuestion = new Question('Username: ');
        $username = $helper->ask($input, $output, $usernameQuestion);

        $passwordQuestion = new Question('Password: ');
        $passwordQuestion->setHidden(true);
        $password = $helper->ask($input, $output, $passwordQuestion);

        $typeQuestion = new Question('Type (admin/client/guide): ', 'client');
        $type = $helper->ask($input, $output, $typeQuestion);

        $phoneQuestion = new Question('Phone: ', '00000000');
        $phone = $helper->ask($input, $output, $phoneQuestion);

        // Create user based on type
        $user = match($type) {
            'admin' => new Admin(),
            'guide' => new Guide(),
            default => new Client(),
        };

        $user->setEmail($email);
        $user->setUsername($username);
        $user->setPhoneNumber($phone);
        $user->setRole('ROLE_' . strtoupper($type));
        $user->setRoles(['ROLE_' . strtoupper($type)]);
        $user->setCreatedAt(new \DateTimeImmutable());

        $hashedPassword = $this->passwordHasher->hashPassword($user, $password);
        $user->setPassword($hashedPassword);

        $this->entityManager->persist($user);
        $this->entityManager->flush();

        $output->writeln('User created successfully!');

        return Command::SUCCESS;
    }
}
