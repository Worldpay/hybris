import { UntypedFormGroup } from '@angular/forms';

/**
 * Make form errors visible
 * @since 6.4.0
 * @param form - UntypedFormGroup
 */
export const makeFormErrorsVisible = (form: UntypedFormGroup): void => {
  try {
    if (!form || !form.controls) {
      return;
    }
    for (const ctrlName in form.controls) {
      // eslint-disable-next-line no-prototype-builtins
      if (form.controls.hasOwnProperty(ctrlName)) {
        const ctrl = form.controls[ctrlName];
        if (ctrl instanceof UntypedFormGroup) {
          makeFormErrorsVisible(ctrl);
        } else {
          ctrl.markAsTouched();
          ctrl.markAsDirty();
          ctrl.updateValueAndValidity();
        }
      }
    }
  } catch (e) {
  }
};
