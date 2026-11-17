import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { CursoDetalle } from './curso-detalle';
import { testProviders } from '../../../core/testing/test-providers';

describe('CursoDetalle', () => {
  let component: CursoDetalle;
  let fixture: ComponentFixture<CursoDetalle>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CursoDetalle],
      providers: [
        ...testProviders,
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => 'curso-1' } } }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CursoDetalle);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
