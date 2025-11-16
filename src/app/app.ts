import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';
import { Hero} from './components/hero/hero';
import { TravelStats} from './components/travel-stats/travel-stats';
import { Destinations} from './components/destinations/destinations';
import { Testimonial} from './components/testimonial/testimonial';
import { Footer} from './components/footer/footer';

@Component({
	  selector: 'app-root',
	  standalone: true,
	  imports: [
		  RouterOutlet,
		  Navbar,
		  Hero,
		  TravelStats,
		  Destinations,
		  Testimonial,
		  Footer
	  ],
	  templateUrl: './app.html',
	  styleUrl: './app.css'})

export class App{
	  title = 'tuniway-frontend';
}
