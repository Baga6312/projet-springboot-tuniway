<?php

namespace App\Entity;

use App\Repository\ReservationRepository;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: ReservationRepository::class)]
class Reservation
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column]
    private ?\DateTime $reservationDate = null;

    #[ORM\Column(length: 50)]
    private ?string $status = null;

    #[ORM\Column]
    private ?int $numbersOfPersons = null;

    #[ORM\Column(nullable: true)]
    private ?float $totalPrice = null;

    #[ORM\Column]
    private ?\DateTime $createdAt = null;

    #[ORM\ManyToOne(inversedBy: 'reservations')]
    private ?User $user = null;

    #[ORM\ManyToOne(inversedBy: 'reservations')]
    #[ORM\JoinColumn(nullable: false)]
    private ?Place $Place = null;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getReservationDate(): ?\DateTime
    {
        return $this->reservationDate;
    }

    public function setReservationDate(\DateTime $reservationDate): static
    {
        $this->reservationDate = $reservationDate;

        return $this;
    }

    public function getStatus(): ?string
    {
        return $this->status;
    }

    public function setStatus(string $status): static
    {
        $this->status = $status;

        return $this;
    }

    public function getNumbersOfPersons(): ?int
    {
        return $this->numbersOfPersons;
    }

    public function setNumbersOfPersons(int $numbersOfPersons): static
    {
        $this->numbersOfPersons = $numbersOfPersons;

        return $this;
    }

    public function getTotalPrice(): ?float
    {
        return $this->totalPrice;
    }

    public function setTotalPrice(?float $totalPrice): static
    {
        $this->totalPrice = $totalPrice;

        return $this;
    }

    public function getCreatedAt(): ?\DateTime
    {
        return $this->createdAt;
    }

    public function setCreatedAt(\DateTime $createdAt): static
    {
        $this->createdAt = $createdAt;

        return $this;
    }

    public function getUser(): ?User
    {
        return $this->user;
    }

    public function setUser(?User $user): static
    {
        $this->user = $user;

        return $this;
    }

    public function getPlace(): ?Place
    {
        return $this->Place;
    }

    public function setPlace(?Place $Place): static
    {
        $this->Place = $Place;

        return $this;
    }
}
