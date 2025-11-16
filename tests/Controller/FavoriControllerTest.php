<?php

namespace App\Tests\Controller;

use App\Entity\Favori;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\EntityRepository;
use Symfony\Bundle\FrameworkBundle\KernelBrowser;
use Symfony\Bundle\FrameworkBundle\Test\WebTestCase;

final class FavoriControllerTest extends WebTestCase
{
    private KernelBrowser $client;
    private EntityManagerInterface $manager;
    private EntityRepository $favoriRepository;
    private string $path = '/favori/';

    protected function setUp(): void
    {
        $this->client = static::createClient();
        $this->manager = static::getContainer()->get('doctrine')->getManager();
        $this->favoriRepository = $this->manager->getRepository(Favori::class);

        foreach ($this->favoriRepository->findAll() as $object) {
            $this->manager->remove($object);
        }

        $this->manager->flush();
    }

    public function testIndex(): void
    {
        $this->client->followRedirects();
        $crawler = $this->client->request('GET', $this->path);

        self::assertResponseStatusCodeSame(200);
        self::assertPageTitleContains('Favori index');

        // Use the $crawler to perform additional assertions e.g.
        // self::assertSame('Some text on the page', $crawler->filter('.p')->first()->text());
    }

    public function testNew(): void
    {
        $this->markTestIncomplete();
        $this->client->request('GET', sprintf('%snew', $this->path));

        self::assertResponseStatusCodeSame(200);

        $this->client->submitForm('Save', [
            'favori[createdAt]' => 'Testing',
            'favori[user]' => 'Testing',
            'favori[place]' => 'Testing',
        ]);

        self::assertResponseRedirects($this->path);

        self::assertSame(1, $this->favoriRepository->count([]));
    }

    public function testShow(): void
    {
        $this->markTestIncomplete();
        $fixture = new Favori();
        $fixture->setCreatedAt('My Title');
        $fixture->setUser('My Title');
        $fixture->setPlace('My Title');

        $this->manager->persist($fixture);
        $this->manager->flush();

        $this->client->request('GET', sprintf('%s%s', $this->path, $fixture->getId()));

        self::assertResponseStatusCodeSame(200);
        self::assertPageTitleContains('Favori');

        // Use assertions to check that the properties are properly displayed.
    }

    public function testEdit(): void
    {
        $this->markTestIncomplete();
        $fixture = new Favori();
        $fixture->setCreatedAt('Value');
        $fixture->setUser('Value');
        $fixture->setPlace('Value');

        $this->manager->persist($fixture);
        $this->manager->flush();

        $this->client->request('GET', sprintf('%s%s/edit', $this->path, $fixture->getId()));

        $this->client->submitForm('Update', [
            'favori[createdAt]' => 'Something New',
            'favori[user]' => 'Something New',
            'favori[place]' => 'Something New',
        ]);

        self::assertResponseRedirects('/favori/');

        $fixture = $this->favoriRepository->findAll();

        self::assertSame('Something New', $fixture[0]->getCreatedAt());
        self::assertSame('Something New', $fixture[0]->getUser());
        self::assertSame('Something New', $fixture[0]->getPlace());
    }

    public function testRemove(): void
    {
        $this->markTestIncomplete();
        $fixture = new Favori();
        $fixture->setCreatedAt('Value');
        $fixture->setUser('Value');
        $fixture->setPlace('Value');

        $this->manager->persist($fixture);
        $this->manager->flush();

        $this->client->request('GET', sprintf('%s%s', $this->path, $fixture->getId()));
        $this->client->submitForm('Delete');

        self::assertResponseRedirects('/favori/');
        self::assertSame(0, $this->favoriRepository->count([]));
    }
}
