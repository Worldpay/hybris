import { ComponentFixture, TestBed } from '@angular/core/testing';
import { I18nTestingModule } from '@spartacus/core';
import { of } from 'rxjs';
import { WorldpayApmSubmitButtonsComponent } from './worldpay-apm-submit-buttons.component';

const dataTestId = 'continue-button';

describe('WorldpayApmSubmitButtonsComponent', () => {
  let component: WorldpayApmSubmitButtonsComponent;
  let fixture: ComponentFixture<WorldpayApmSubmitButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        I18nTestingModule,
        WorldpayApmSubmitButtonsComponent,
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(WorldpayApmSubmitButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    component.dataTestId = dataTestId;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit back event when onBack is called', () => {
    spyOn(component.back, 'emit');

    component.onBack();

    expect(component.back.emit).toHaveBeenCalled();
  });

  it('should emit continue event when onContinue is called', () => {
    spyOn(component.continue, 'emit');

    component.onContinue();

    expect(component.continue.emit).toHaveBeenCalled();
  });

  it('should disable continue button when disableContinue is true', () => {
    component.disableContinue$ = of(true);
    fixture.detectChanges();

    const continueButton = fixture.nativeElement.querySelector(`[data-test-id="${dataTestId}"]`);
    expect(continueButton.disabled).toBeTrue();
  });

  it('should enable continue button when disableContinue is false', () => {
    component.disableContinue$ = of(false);
    fixture.detectChanges();

    const continueButton = fixture.nativeElement.querySelector(`[data-test-id="${dataTestId}"]`);
    expect(continueButton.disabled).toBeFalse();
  });

  describe('ui tests', () => {
    it('should call onBack when back button is clicked', () => {
      spyOn(component, 'onBack');
      const backButton = fixture.nativeElement.querySelector('.btn-back');
      backButton.click();

      expect(component.onBack).toHaveBeenCalled();
    });

    it('should call onContinue when continue button is clicked', () => {
      spyOn(component, 'onContinue');
      const continueButton = fixture.nativeElement.querySelector('.btn-primary');
      continueButton.click();

      expect(component.onContinue).toHaveBeenCalled();
    });

    it('should disable continue element button when disableContinue is true', () => {
      component.disableContinue$ = of(true);
      fixture.detectChanges();

      const continueButton = fixture.nativeElement.querySelector('.btn-primary');
      expect(continueButton.disabled).toBeTrue();
    });
  });
});
