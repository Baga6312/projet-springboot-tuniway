import { Component } from '@angular/core';
import { Navbar } from '../../components/navbar/navbar';
import { Hero } from '../../components/hero/hero';
import { TravelStats } from '../../components/travel-stats/travel-stats';
import { Destinations } from '../../components/destinations/destinations';
import { Testimonial } from '../../components/testimonial/testimonial';
import { Footer } from '../../components/footer/footer';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    Navbar,
    Hero,
    TravelStats,
    Destinations,
    Testimonial,
    Footer
  ],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {
  title = 'TuniWay - Home';
}

