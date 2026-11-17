import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs'



@Injectable({ providedIn: 'root' })
export class AuthModalService {
  // Estado del modal: abierto/cerrado y qué pestaña mostrar
  private modalState = new BehaviorSubject<{ abierto: boolean, tab: 'login' | 'registro' }>({ abierto: false, tab: 'login' });

  // Observable que los componentes pueden escuchar
  modalState$ = this.modalState.asObservable();

  abrirModal(tab: 'login' | 'registro' = 'login') {
    console.log('Servicio: Abriendo modal');
    this.modalState.next({ abierto: true, tab });
  }

  cerrarModal() {
    this.modalState.next({ abierto: false, tab: 'login' });
  }

}
