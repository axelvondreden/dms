import { PolymerElement } from "@polymer/polymer/polymer-element.js";
import { html } from "@polymer/polymer/lib/utils/html-tag.js";
import "@vaadin/vaadin-split-layout/vaadin-split-layout.js";
import "@vaadin/vaadin-grid/vaadin-grid.js";
import "@vaadin/vaadin-grid/vaadin-grid-column.js";
import "@vaadin/vaadin-form-layout/vaadin-form-layout.js";
import "@vaadin/vaadin-form-layout/vaadin-form-item.js";
import "@vaadin/vaadin-text-field/vaadin-text-field.js";
import "@vaadin/vaadin-text-field/vaadin-password-field.js";
import "@vaadin/vaadin-button/vaadin-button.js";
import "@vaadin/vaadin-ordered-layout/vaadin-horizontal-layout.js";
import '@vaadin/vaadin-date-picker/src/vaadin-date-picker.js';

class PersonsView extends PolymerElement {
  static get template() {
    return html`
<style include="shared-styles">
        :host {
          display: block;
          height: 100%;
        }
      </style>
<vaadin-split-layout style="width: 100%; height: 100%;">
 <div style="flex-grow:1;width:100%;" id="wrapper">
  <vaadin-grid style="height:100%" theme="no-border" id="grid"></vaadin-grid>
 </div>
 <div style="width:400px;padding:var(--lumo-space-l)">
  <vaadin-form-layout>
   <vaadin-form-item>
    <label slot="label">First name</label>
    <vaadin-text-field class="full-width" value="" id="first_name"></vaadin-text-field>
   </vaadin-form-item>
   <vaadin-form-item>
    <label slot="label">Last name</label>
    <vaadin-text-field class="full-width" value="" id="last_name"></vaadin-text-field>
   </vaadin-form-item>
   <vaadin-form-item>
    <label slot="label">Date of birth</label>
    <vaadin-date-picker id="dateOfBirth" class="full-width"></vaadin-date-picker>
   </vaadin-form-item>
  </vaadin-form-layout>
  <vaadin-horizontal-layout style="display:flex;flex-wrap:wrap-reverse;width:100%;justify-content:flex-end;" theme="spacing">
   <vaadin-button theme="tertiary" slot="" id="cancel">
     Cancel 
   </vaadin-button>
   <vaadin-button theme="primary" id="save">
     Save 
   </vaadin-button>
  </vaadin-horizontal-layout>
 </div>
</vaadin-split-layout>
`;
  }


  static get is() {
    return "persons-view";
  }

  static get properties() {
    return {
      // Declare your properties here.
    };
  }
}

customElements.define(PersonsView.is, PersonsView);
