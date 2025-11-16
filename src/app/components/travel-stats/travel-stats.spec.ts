import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TravelStats } from './travel-stats';

describe('TravelStats', () => {
  let component: TravelStats;
  let fixture: ComponentFixture<TravelStats>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TravelStats]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TravelStats);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
