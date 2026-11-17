import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Catalogo } from './catalogo';
import { testProviders } from '../../../core/testing/test-providers';

describe('Catalogo', () => {
  let component: Catalogo;
  let fixture: ComponentFixture<Catalogo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Catalogo],
      providers: testProviders
    }).compileComponents();

    fixture = TestBed.createComponent(Catalogo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
