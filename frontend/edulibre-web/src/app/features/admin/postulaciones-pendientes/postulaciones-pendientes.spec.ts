import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PostulacionesPendientes } from './postulaciones-pendientes';
import { testProviders } from '../../../core/testing/test-providers';

describe('PostulacionesPendientes', () => {
  let component: PostulacionesPendientes;
  let fixture: ComponentFixture<PostulacionesPendientes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostulacionesPendientes],
      providers: testProviders
    }).compileComponents();

    fixture = TestBed.createComponent(PostulacionesPendientes);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
