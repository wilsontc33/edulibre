import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ModalRechazo } from './modal-rechazo';
import { testProviders } from '../../../core/testing/test-providers';

describe('ModalRechazo', () => {
  let component: ModalRechazo;
  let fixture: ComponentFixture<ModalRechazo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalRechazo],
      providers: testProviders
    }).compileComponents();

    fixture = TestBed.createComponent(ModalRechazo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
