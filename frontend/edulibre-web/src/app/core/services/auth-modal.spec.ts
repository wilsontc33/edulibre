import { TestBed } from '@angular/core/testing';
import { AuthModalService } from './auth-modal';
import { testProviders } from '../testing/test-providers';

describe('AuthModalService', () => {
  let service: AuthModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: testProviders });
    service = TestBed.inject(AuthModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
