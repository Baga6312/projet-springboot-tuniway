import { Component, OnInit, AfterViewInit, ElementRef } from '@angular/core';

@Component({
  selector: 'app-destinations',
  imports: [],
  templateUrl: './destinations.html',
  styleUrl: './destinations.css',
})
export class Destinations implements OnInit, AfterViewInit {
  constructor(private elementRef: ElementRef) {}
  
  ngOnInit(): void {}
  
  ngAfterViewInit(): void {
    if (typeof window !== 'undefined' && typeof IntersectionObserver !== 'undefined') {
      const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            const cards = this.elementRef.nativeElement.querySelectorAll('.destination-card');
            cards.forEach((card: HTMLElement) => {
              card.classList.add('visible');
            });
          }
        });
      }, { threshold: 0.3 }); 
      observer.observe(this.elementRef.nativeElement);
    }
  }
}