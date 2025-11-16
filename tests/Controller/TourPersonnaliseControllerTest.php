<?php

namespace App\Tests\Controller;

use App\Entity\TourPersonnalise;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\EntityRepository;
use Symfony\Bundle\FrameworkBundle\KernelBrowser;
use Symfony\Bundle\FrameworkBundle\Test\WebTestCase;

final class TourPersonnaliseControllerTest extends WebTestCase
{
    private KernelBrowser $client;
    private EntityManagerInterface $manager;
    private EntityRepository $tourPersonnaliseRepository;
    private string $path = '/tour/personnalise/';

    protected function setUp(): void
    {
        $this->client = static::createClient();
        $this->manager = static::getContainer()->get('doctrine')->getManager();
        $this->tourPersonnaliseRepository = $this->manager->getRepository(TourPersonnalise::class);

        foreach ($this->tourPersonnaliseRepository->findAll() as $object) {
            $this->manager->remove($object);
        }

        $this->manager->flush();
    }

    public function testIndex(): void
    {
        $this->client->followRedirects();
        $crawler = $this->client->request('GET', $this->path);

        self::assertResponseStatusCodeSame(200);
        self::assertPageTitleContains('TourPersonnalise index');

        // Use the $crawler to perform additional assertions e.g.
        // self::assertSame('Some text on the page', $crawler->filter('.p')->first()->text());
    }

    public function testNew(): void
    {
        $this->markTestIncomplete();
        $this->client->request('GET', sprintf('%snew', $this->path));

        self::assertResponseStatusCodeSame(200);

        $this->client->submitForm('Save', [
            'tour_personnalise[title]' => 'Testing',
            'tour_personnalise[description]' => 'Testing',
            'tour_personnalise[duration]' => 'Testing',
            'tour_personnalise[price]' => 'Testing',
            'tour_personnalise[maxPersons]' => 'Testing',
            'tour_personnalise[createdAt]' => 'Testing',
            'tour_personnalise[guide]' => 'Testing',
            'tour_personnalise[client]' => 'Testing',
        ]);

        self::assertResponseRedirects($this->path);

        self::assertSame(1, $this->tourPersonnaliseRepository->count([]));
    }

    public function testShow(): void
    {
        $this->markTestIncomplete();
        $fixture = new TourPersonnalise();
        $fixture->setTitle('My Title');
        $fixture->setDescription('My Title');
        $fixture->setDuration('My Title');
        $fixture->setPrice('My Title');
        $fixture->setMaxPersons('My Title');
        $fixture->setCreatedAt('My Title');
        $fixture->setGuide('My Title');
        $fixture->setClient('My Title');

        $this->manager->persist($fixture);
        $this->manager->flush();

        $this->client->request('GET', sprintf('%s%s', $this->path, $fixture->getId()));

        self::assertResponseStatusCodeSame(200);
        self::assertPageTitleContains('TourPersonnalise');

        // Use assertions to check that the properties are properly displayed.
    }

    public function testEdit(): void
    {
        $this->markTestIncomplete();
        $fixture = new TourPersonnalise();
        $fixture->setTitle('Value');
        $fixture->setDescription('Value');
        $fixture->setDuration('Value');
        $fixture->setPrice('Value');
        $fixture->setMaxPersons('Value');
        $fixture->setCreatedAt('Value');
        $fixture->setGuide('Value');
        $fixture->setClient('Value');

        $this->manager->persist($fixture);
        $this->manager->flush();

        $this->client->request('GET', sprintf('%s%s/edit', $this->path, $fixture->getId()));

        $this->client->submitForm('Update', [
            'tour_personnalise[title]' => 'Something New',
            'tour_personnalise[description]' => 'Something New',
            'tour_personnalise[duration]' => 'Something New',
            'tour_personnalise[price]' => 'Something New',
            'tour_personnalise[maxPersons]' => 'Something New',
            'tour_personnalise[createdAt]' => 'Something New',
            'tour_personnalise[guide]' => 'Something New',
            'tour_personnalise[client]' => 'Something New',
        ]);

        self::assertResponseRedirects('/tour/personnalise/');

        $fixture = $this->tourPersonnaliseRepository->findAll();

        self::assertSame('Something New', $fixture[0]->getTitle());
        self::assertSame('Something New', $fixture[0]->getDescription());
        self::assertSame('Something New', $fixture[0]->getDuration());
        self::assertSame('Something New', $fixture[0]->getPrice());
        self::assertSame('Something New', $fixture[0]->getMaxPersons());
        self::assertSame('Something New', $fixture[0]->getCreatedAt());
        self::assertSame('Something New', $fixture[0]->getGuide());
        self::assertSame('Something New', $fixture[0]->getClient());
    }

    public function testRemove(): void
    {
        $this->markTestIncomplete();
        $fixture = new TourPersonnalise();
        $fixture->setTitle('Value');
        $fixture->setDescription('Value');
        $fixture->setDuration('Value');
        $fixture->setPrice('Value');
        $fixture->setMaxPersons('Value');
        $fixture->setCreatedAt('Value');
        $fixture->setGuide('Value');
        $fixture->setClient('Value');

        $this->manager->persist($fixture);
        $this->manager->flush();

        $this->client->request('GET', sprintf('%s%s', $this->path, $fixture->getId()));
        $this->client->submitForm('Delete');

        self::assertResponseRedirects('/tour/personnalise/');
        self::assertSame(0, $this->tourPersonnaliseRepository->count([]));
    }
}
