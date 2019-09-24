import {html, PolymerElement} from '@polymer/polymer/polymer-element.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-button/src/vaadin-button.js';
import '@vaadin/vaadin-combo-box/src/vaadin-combo-box.js';
import '@vaadin/vaadin-text-field/src/vaadin-password-field.js';
import '@vaadin/vaadin-form-layout/src/vaadin-form-item.js';
import '@vaadin/vaadin-text-field/src/vaadin-text-field.js';

class RegisterView extends PolymerElement {

    static get template() {
        return html`
<style include="shared-styles">
                :host {
                    display: block;
                    height: 100%;
                }
            </style>
<vaadin-vertical-layout style="width: 100%; height: 100%; display: flex; align-items: center; padding-top: 100px;">
 <vaadin-form-item>
  <label slot="label">Username</label>
  <vaadin-text-field class="full-width" value="Jane" required id="userName"></vaadin-text-field>
 </vaadin-form-item>
 <vaadin-form-item>
  <label slot="label">First Name</label>
  <vaadin-text-field class="full-width" value="Jane" id="firstName"></vaadin-text-field>
 </vaadin-form-item>
 <vaadin-form-item>
  <label slot="label">Last Name</label>
  <vaadin-text-field class="full-width" value="Doe" id="lastName"></vaadin-text-field>
 </vaadin-form-item>
 <vaadin-form-item>
  <label slot="label">Password</label>
  <vaadin-password-field id="password1" class="full-width"></vaadin-password-field>
 </vaadin-form-item>
 <vaadin-form-item>
  <label slot="label">Password</label>
  <vaadin-password-field class="full-width" id="password2"></vaadin-password-field>
 </vaadin-form-item>
 <vaadin-form-item>
  <label slot="label">Role</label>
  <vaadin-combo-box id="roles" class="full-width" prevent-invalid-input></vaadin-combo-box>
 </vaadin-form-item>
 <vaadin-form-item>
  <vaadin-button theme="primary" id="submit" class="full-width">
    Submit 
  </vaadin-button>
 </vaadin-form-item>
</vaadin-vertical-layout>
`;
    }

    static get is() {
        return 'register-view';
    }

    static get properties() {
        return {
            // Declare your properties here.
        };
    }
}

customElements.define(RegisterView.is, RegisterView);
