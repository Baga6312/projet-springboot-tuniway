<?php

namespace App\Tests\Controller;

use App\Entity\Review;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\EntityRepository;
use Symfony\Bundle\FrameworkBundle\KernelBrowser;
use Symfony\Bundle\FrameworkBundle\Test\WebTestCase;

final class ReviewControllerTest extends WebTestCase
{
    private KernelBrowser $client;
    private EntityManagerInterface $manager;
    private EntityRepository $reviewRepository;
    private string $path = '/review/';

    protected function setUp(): void
    {
        $this->client = static::createClient();
        $this->manager = static::getContainer()->get('doctrine')->getManager();
        $this->reviewRepository = $this->manager->getRepository(Review::class);

        foreach ($this->reviewRepository->findAll() as $object) {
            $this->manager->remove($object);
        }

        $this->manager->flush();
    }

    public function testIndex(): void
    {
        $this->client->followRedirects();
        $crawler = $this->client->request('GET', $this->path);

        self::assertResponseStatusCodeSame(200);
        self::assertPageTitleContains('Review index');

        // Use the $crawler to perform additional assertions e.g.
        // self::assertSame('Some text on the page', $crawler->filter('.p')->first()->text());
    }

    public function testNew(): void
    {
        $this->markTestIncomplete();
        $this->client->request('GET', sprintf('%snew', $this->path));

        self::assertResponseStatusCodeSame(200);

        $this->client->submitForm('Save', [
            'review[rating]' => 'Testing',
            'review[comment]' => 'Testing',
            'review[createdAt]' => 'Testing',
            'review[user]' => 'Testing',
            'review[place]' => 'Testing',
        ]);

        self::assertResponseRedirects($this->path);

        self::assertSame(1, $this->reviewRepository->count([]));
    }

    public function testShow(): void
    {
        $this->markTestIncomplete();
        $fixture = new Review();
        $fixture->setRating('My Title');
        $fixture->setComment('My Title');
        $fixture->setCreatedAt('My Title');
        $fixture->setUser('My Title');
        $fixture->setPlace('My Title');

        $this->manager->persist($fixture);
        $this->manager->flush();

        $this->client->request('GET', sprintf('%s%s', $this->path, $fixture->getId()));

        self::assertResponseStatusCodeSame(200);
        self::assertPageTitleContains('Review');

        // Use assertions to check that the properties are properly displayed.
    }

    public function testEdit(): void
    {
        $this->markTestIncomplete();
        $fixture = new Review();
        $fixture->setRating('Value');
        $fixture->setComment('Value');
        $fixture->setCreatedAt('Value');
        $fixture->setUser('Value');
        $fixture->setPlace('Value');

        $this->manager->persist($fixture);
        $this->manager->flush();

        $this->client->request('GET', sprintf('%s%s/edit', $this->path, $fixture->getId()));

        $this->client->submitForm('Update', [
            'review[rating]' => 'Something New',
            'review[comment]' => 'Something New',
            'review[createdAt]' => 'Something New',
            'review[user]' => 'Something New',
            'review[place]' => 'Something New',
        ]);

        self::assertResponseRedirects('/review/');

        $fixture = $this->reviewRepository->findAll();

        self::assertSame('Something New', $fixture[0]->getRating());
        self::assertSame('Something New', $fixture[0]->getComment());
        self::assertSame('Something New', $fixture[0]->getCreatedAt());
        self::assertSame('Something New', $fixture[0]->getUser());
        self::assertSame('Something New', $fixture[0]->getPlace());
    }

    public function testRemove(): void
    {
        $this->markTestIncomplete();
        $fixture = new Review();
        $fixture->setRating('Value');
        $fixture->setComment('Value');
        $fixture->setCreatedAt('Value');
        $fixture->setUser('Value');
        $fixture->setPlace('Value');

        $this->manager->persist($fixture);
        $this->manager->flush();

        $this->client->request('GET', sprintf('%s%s', $this->path, $fixture->getId()));
        $this->client->submitForm('Delete');

        self::assertResponseRedirects('/review/');
        self::assertSame(0, $this->reviewRepository->count([]));
    }
}
