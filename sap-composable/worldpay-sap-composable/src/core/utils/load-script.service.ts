/**
 *
 * @param src script url to load
 * @param onloadCallback callback function is called when new script is loaded
 */
import { WindowRef } from '@spartacus/core';
import { Injectable } from '@angular/core';

export interface Scripts {
  idScript: string;
  src: string;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  onloadCallback?: any;
  async?: boolean;
  defer?: boolean;
  attributes?: {
    [key: string]: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class LoadScriptService {

  /**
   * Constructor
   * @param winRef WindowRef
   */
  constructor(
    protected winRef: WindowRef
  ) {
  }

  /**
   * Load script to DOM
   * @since 6.4.0
   * @param idScript - string
   * @param src - string
   * @param onloadCallback - function
   * @param async - boolean
   * @param defer - boolean
   * @param attributes - object
   */
  loadScript(
    {
      idScript,
      src,
      onloadCallback,
      async,
      defer,
      attributes,
    }: Scripts
  ): void {
    if (this.winRef.isBrowser()) {
      let isFound = false;
      const scripts = this.winRef.document.getElementsByTagName('script');
      for (let i = 0; i < scripts.length; ++i) {
        if (
          scripts[i].getAttribute('src') != null &&
          scripts[i].getAttribute('src') === src
        ) {
          isFound = true;
          break;
        }
      }

      if (!isFound) {
        let node = this.winRef.document.createElement('script');
        node.src = src;
        node.id = idScript || Math.floor(Math.random() * 999999).toString();
        node.type = 'text/javascript';
        if (async) {
          node.async = async;
        }
        if (defer) {
          node.defer = defer;
        }
        if (onloadCallback) {
          node.onload = onloadCallback;
        }

        if (attributes) {
          node = this.updateScript(node, attributes);
        }
        this.winRef.document.getElementsByTagName('head')[0].appendChild(node);
      }
    } else {
      console.log('LoadScript service not loaded as test is running or SSR is mode enabled.');
    }
  }

  /**
   * Remove script from DOM
   * @since 6.4.0
   * @param idScript - string
   */
  removeScript(idScript: string): void {
    if (this.winRef.isBrowser()) {
      const script = this.winRef.document.querySelector(`script#${idScript}`);
      if (script) {
        script.remove();
      }
    } else {
      console.log('LoadScript service not loaded as test is running or SSR is mode enabled.');
    }
  }

  /**
   * Update script attributes
   * @since 6.4.0
   * @param node - Element or HTMLElement
   * @param attributes - object
   */
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  updateScript(node: any, attributes: { [key: string]: string }): any {
    if (node && attributes) {
      for (const key of Object.keys(attributes)) {
        node.setAttribute(key, attributes[key]);
      }
    }

    return node;
  }
}
