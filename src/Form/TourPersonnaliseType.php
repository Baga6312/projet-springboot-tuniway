<?php

namespace App\Form;

use App\Entity\TourPersonnalise;
use App\Entity\User;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class TourPersonnaliseType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('title')
            ->add('description')
            ->add('duration')
            ->add('price')
            ->add('maxPersons')
            ->add('createdAt', null, [
                'widget' => 'single_text',
            ])
            ->add('guide', EntityType::class, [
                'class' => User::class,
                'choice_label' => 'id',
            ])
            ->add('client', EntityType::class, [
                'class' => User::class,
                'choice_label' => 'id',
            ])
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => TourPersonnalise::class,
        ]);
    }
}
