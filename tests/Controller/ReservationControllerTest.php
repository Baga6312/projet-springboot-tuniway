<?php

namespace App\Tests\Controller;

use App\Entity\Reservation;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\EntityRepository;
use Symfony\Bundle\FrameworkBundle\KernelBrowser;
use Symfony\Bundle\FrameworkBundle\Test\WebTestCase;

final class ReservationControllerTest extends WebTestCase
{
    private KernelBrowser $client;
    private EntityManagerInterface $manager;
    private EntityRepository $reservationRepository;
    private string $path = '/reservation/';

    protected function setUp(): void
    {
        $this->client = static::createClient();
        $this->manager = static::getContainer()->get('doctrine')->getManager();
        $this->reservationRepository = $this->manager->getRepository(Reservation::class);

        foreach ($this->reservationRepository->findAll() as $object) {
            $this->manager->remove($object);
        }

        $this->manager->flush();
    }

    public function testIndex(): void
    {
        $this->client->followRedirects();
        $crawler = $this->client->request('GET', $this->path);

        self::assertResponseStatusCodeSame(200);
        self::assertPageTitleContains('Reservation index');

        // Use the $crawler to perform additional assertions e.g.
        // self::assertSame('Some text on the page', $crawler->filter('.p')->first()->text());
    }

    public function testNew(): void
    {
        $this->markTestIncomplete();
        $this->client->request('GET', sprintf('%snew', $this->path));

        self::assertResponseStatusCodeSame(200);

        $this->client->submitForm('Save', [
            'reservation[reservationDate]' => 'Testing',
            'reservation[status]' => 'Testing',
            'reservation[numbersOfPersons]' => 'Testing',
            'reservation[totalPrice]' => 'Testing',
            'reservation[createdAt]' => 'Testing',
            'reservation[user]' => 'Testing',
            'reservation[Place]' => 'Testing',
        ]);

        self::assertResponseRedirects($this->path);

        self::assertSame(1, $this->reservationRepository->count([]));
    }

    public function testShow(): void
    {
        $this->markTestIncomplete();
        $fixture = new Reservation();
        $fixture->setReservationDate('My Title');
        $fixture->setStatus('My Title');
        $fixture->setNumbersOfPersons('My Title');
        $fixture->setTotalPrice('My Title');
        $fixture->setCreatedAt('My Title');
        $fixture->setUser('My Title');
        $fixture->setPlace('My Title');

        $this->manager->persist($fixture);
        $this->manager->flush();

        $this->client->request('GET', sprintf('%s%s', $this->path, $fixture->getId()));

        self::assertResponseStatusCodeSame(200);
        self::assertPageTitleContains('Reservation');

        // Use assertions to check that the properties are properly displayed.
    }

    public function testEdit(): void
    {
        $this->markTestIncomplete();
        $fixture = new Reservation();
        $fixture->setReservationDate('Value');
        $fixture->setStatus('Value');
        $fixture->setNumbersOfPersons('Value');
        $fixture->setTotalPrice('Value');
        $fixture->setCreatedAt('Value');
        $fixture->setUser('Value');
        $fixture->setPlace('Value');

        $this->manager->persist($fixture);
        $this->manager->flush();

        $this->client->request('GET', sprintf('%s%s/edit', $this->path, $fixture->getId()));

        $this->client->submitForm('Update', [
            'reservation[reservationDate]' => 'Something New',
            'reservation[status]' => 'Something New',
            'reservation[numbersOfPersons]' => 'Something New',
            'reservation[totalPrice]' => 'Something New',
            'reservation[createdAt]' => 'Something New',
            'reservation[user]' => 'Something New',
            'reservation[Place]' => 'Something New',
        ]);

        self::assertResponseRedirects('/reservation/');

        $fixture = $this->reservationRepository->findAll();

        self::assertSame('Something New', $fixture[0]->getReservationDate());
        self::assertSame('Something New', $fixture[0]->getStatus());
        self::assertSame('Something New', $fixture[0]->getNumbersOfPersons());
        self::assertSame('Something New', $fixture[0]->getTotalPrice());
        self::assertSame('Something New', $fixture[0]->getCreatedAt());
        self::assertSame('Something New', $fixture[0]->getUser());
        self::assertSame('Something New', $fixture[0]->getPlace());
    }

    public function testRemove(): void
    {
        $this->markTestIncomplete();
        $fixture = new Reservation();
        $fixture->setReservationDate('Value');
        $fixture->setStatus('Value');
        $fixture->setNumbersOfPersons('Value');
        $fixture->setTotalPrice('Value');
        $fixture->setCreatedAt('Value');
        $fixture->setUser('Value');
        $fixture->setPlace('Value');

        $this->manager->persist($fixture);
        $this->manager->flush();

        $this->client->request('GET', sprintf('%s%s', $this->path, $fixture->getId()));
        $this->client->submitForm('Delete');

        self::assertResponseRedirects('/reservation/');
        self::assertSame(0, $this->reservationRepository->count([]));
    }
}
