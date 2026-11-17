import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MisPostulaciones } from './mis-postulaciones';
import { testProviders } from '../../../core/testing/test-providers';

describe('MisPostulaciones', () => {
  let component: MisPostulaciones;
  let fixture: ComponentFixture<MisPostulaciones>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MisPostulaciones],
      providers: testProviders
    }).compileComponents();

    fixture = TestBed.createComponent(MisPostulaciones);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
