/**
 *
 * @param src script url to load
 * @param onloadCallback callback function is called when new script is loaded
 */
import { inject, Injectable } from '@angular/core';
import { LoggerService, WindowRef } from '@spartacus/core';

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

  protected winRef: WindowRef = inject(WindowRef);
  private logger: LoggerService = inject(LoggerService);

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
      let isFound: boolean = false;
      const scripts: HTMLCollectionOf<HTMLScriptElement> = this.winRef.document.getElementsByTagName('script');
      for (let i: number = 0; i < scripts.length; ++i) {
        if (
          scripts[i].getAttribute('src') != null &&
          scripts[i].getAttribute('src') === src
        ) {
          isFound = true;
          break;
        }
      }

      if (!isFound) {
        let node: HTMLScriptElement = this.winRef.document.createElement('script');
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
      this.logger.log('LoadScript service not loaded as test is running or SSR is mode enabled.');
    }
  }

  /**
   * Remove script from DOM
   * @since 6.4.0
   * @param idScript - string
   */
  removeScript(idScript: string): void {
    if (this.winRef.isBrowser()) {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const script: any = this.winRef.document.querySelector(`script#${idScript}`);
      if (script) {
        script.remove();
      }
    } else {
      this.logger.log('LoadScript service not loaded as test is running or SSR is mode enabled.');
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
    if (node && attributes && this.winRef.isBrowser()) {
      for (const key of Object.keys(attributes)) {
        node.setAttribute(key, attributes[key]);
      }
    }

    return node;
  }
}
